---
name: mc-construcao-estruturas
description: Constrói de fato, via código (mod NeoForge 1.21.1 em Java), estruturas e regiões de Minecraft em estilo anime + fantasia medieval — prédios, torres, castelos, muralhas, pontes, campus, jardins e terreno — com volumes variados, telhados, relevo de fachada, janelas, interiores mobiliados, iluminação e paisagismo, evitando “quadradões” sem acabamento e respeitando a arquitetura do que foi pedido. Valida o resultado (blocos flutuando, estados de bloco, portas, alcançabilidade, paleta) antes de aplicar no mundo. Use sempre que o usuário pedir para gerar, construir, codar, implementar, ajustar ou corrigir uma estrutura ou região por código, comando ou gerador (por exemplo “cria a torre do relógio”, “gera a muralha”, “/campus build”), mesmo sem citar a palavra skill. Se existir um Plano de Construção (skill mc-planejamento-construcao), siga-o; senão faça um mini-plano primeiro.
---

# Construção de estruturas e regiões por código · NeoForge 1.21.1 · anime + fantasia medieval

Gera estruturas com **arquitetura**, não caixas. Uma entrega só está pronta quando, vista a 100 blocos, tem silhueta própria; a 30, fachada com profundidade; a 5, detalhes; e por dentro, cômodos com função e luz. Se o resultado parece um paralelepípedo com teto reto, volte aos passes.

Converse em português do Brasil; nomes de classes, métodos e blocos ficam em inglês. Os trechos Java das referências são ilustrativos (1.21.1, mapeamento Mojang): confirme as assinaturas no código do projeto, porque o compilador tem a palavra final.

## 1. Antes de codar

1. **Plano.** Se existir um Plano de Construção (skill `mc-planejamento-construcao` ou um arquivo em `docs/builds/`), siga paleta, dimensões e passes dele. Se não existir e o pedido for maior que um poste ou uma casinha, escreva primeiro um **mini-plano** (abaixo), mostre em até 15 linhas e só então codifique. Se a skill de planejamento estiver disponível, leia-a para o template completo.
2. **Projeto.** Leia `gradle.properties` e `build.gradle` (confirme Minecraft 1.21.1, NeoForge 21.1.x e Java 21), o pacote base e o que já existe de kit de construção, comandos e estruturas. Reutilize e estenda; não duplique.
3. **Kit.** Se não houver kit, crie o mínimo no Run 0 (seção 11).
4. Se o projeto não for um mod Java (por exemplo, um gerador em Python que escreve `.nbt` ou `.mcfunction`), mantenha a mesma arquitetura (Blueprint → validação → saída) e troque só o aplicador.

**Mini-plano (use quando não houver plano):**
```
Conceito e marca registrada:
Pegada (L×P), andares, altura total:
Volumes (massa principal / secundários / acentos) com alturas:
Telhado (tipo, beiral) e remates:
Paleta por papel (BASE/WALL/PILLAR/TRIM/ROOF/WINDOW/FLOOR/ACCENT/LIGHT):
Cômodos e função:
Três acentos de tema:
Terreno e entorno:
```

## 2. Princípios do código

- **Desenho ≠ aplicação.** Monte a construção num `Blueprint` (mapa esparso posição local → `BlockState`) e só depois aplique no mundo. Isso permite validar, pré-visualizar em ASCII, girar e espelhar, medir e desfazer. Loops que chamam `level.setBlock` direto dentro das formas não fazem nada disso.
- **Determinismo.** Toda variação nasce de `seed` + posição (hash ou ruído), nunca de `Random` global. Mesma seed, mesma estrutura: dá para corrigir um prédio sem refazer o campus e reproduzir bugs.
- **Parâmetros.** Largura, profundidade, andares, paleta e seed são parâmetros; a mesma classe gera dormitórios diferentes.
- **Passes.** Cada camada de acabamento é um passe (seção 5). O código espelha o checklist do plano e um passe pode ser refinado sem quebrar os outros.
- **Formas reutilizáveis.** Telhados, arcos, torres, janelas e escadas são primitivas (ver `references/kit-e-formas.md`). Evite `for` soltos dentro de cada prédio: é onde nascem cantos errados e telhados diferentes de um prédio para outro.

## 3. Estrutura de pacotes recomendada

```
<pacote>.build/
  Blueprint.java     mapa esparso local → BlockState, bounds, tags (cômodos, entradas, luzes)
  Box.java           caixa imutável
  Role.java          enum de papéis da paleta
  Palette.java       papéis → estados ponderados; escolha determinística por posição
  Noise.java         hash e manchas determinísticos
  Shapes.java        primitivas: caixas, paredes, cilindros, telhados, arcos, escadas...
  RoofField.java     telhados compostos (L, T, lucarnas) por mapa de alturas
  Furniture.java     móveis e props com estado correto
  Lighting.java      cobertura de luz
  Weathering.java    envelhecimento por ruído
  Transform.java     origem + rotação + espelho
  Validator.java     gates e checagens
  Preview.java       ASCII por andar, elevações, estatísticas
  BuildJob.java      aplicação por ticks, fix-up de estados, undo
  Structure.java     interface: Blueprint build(Params, Palette, long seed)
<pacote>.structures/ uma classe por tipo de edifício (MainHall, Library, Dorm...)
<pacote>.campus/     layout do campus, terraplanagem, caminhos, água, vegetação
<pacote>.command/    /campus dryrun | build | undo | test
```

O contrato de cada classe e as receitas de forma estão em `references/kit-e-formas.md`.

## 4. Convenções

- **Eixos locais**: `x` → direita (leste com rotação 0), `y` → cima, `z` → frente (sul com rotação 0). A **frente da construção é +Z**. Origem no canto de menor x e z, ao nível do piso térreo; o embasamento pode ter `y < 0` para descer até o terreno.
- **IDs como strings** nas paletas (`minecraft:stone_brick_stairs`). Converta com `BlockStateParser` no carregamento e **falhe de forma explícita listando todos os IDs inválidos**: isso pega de uma vez os blocos que não existem no 1.21.1.
- **Rotação e espelho só no commit**: espelhe, depois gire; transforme a posição **e** o estado (`state.mirror(m).rotate(r)`). Dentro das estruturas use sempre direções locais.
- Uma `Structure` devolve um `Blueprint` e nunca toca o `Level`.

## 5. Passes de construção (ordem fixa)

| # | Passe | O que entrega | Armadilha comum |
|---|---|---|---|
| 0 | Terreno e fundação | pad nivelado, talude suave, embasamento descendo até o solo | prédio flutuando ou enterrado; paredão liso de terra |
| 1 | Volumes e paredes | massa principal + secundários + acentos com alturas distintas; planta não retangular | tudo na mesma altura |
| 2 | Relevo da fachada | pilastras, cintas, cantos reforçados, balanços (jetty), contrafortes | parede lisa |
| 3 | Aberturas | portais em arco, janelas altas emolduradas, venezianas, peitoris | buracos 1×1 repetidos |
| 4 | Cobertura | telhado com beiral, empenas, lucarnas, chaminés, pináculos | teto reto; escadas na direção errada |
| 5 | Detalhes externos | lanternas, estandartes, correntes, brasão, relógio, sino, floreiras, barris | detalhes soltos, sem relação com o tema |
| 6 | Interior | pisos, vigas, escadas, divisórias, cômodos mobiliados | cômodo vazio; escada sem vão de cabeça |
| 7 | Iluminação | luz em camadas, fontes escondidas, emissor a cada 7 blocos ou menos dentro | tocha crua; ponto escuro |
| 8 | Entorno | caminhos, canteiros, árvores, bancos, postes | árvores em fileira, em grade |
| 9 | Envelhecimento | ruído nos materiais, musgo e rachaduras, trepadeiras | sal e pimenta; xadrez |
| 10 | Toque anime e mágico | cristais, bastões de End, pétalas, vitrais, runas em blocos cinzelados | exagero que atrapalha a leitura |
| 11 | Fix-up e validação | corrige estados (escadas, cercas, vidraças), roda o validador | pular esta etapa |

## 6. Requisitos mínimos anti-quadradão (gates)

O `Validator` mede estes itens e relata os números. Gate reprovado = corrija o passe responsável antes de aplicar no mundo (ou justifique por escrito no relatório).

| Gate | Requisito mínimo | Como medir |
|---|---|---|
| G1 Volumes | em prédios com pegada acima de 12×12: pelo menos 3 alturas de topo distintas e planta com taxa de ocupação do retângulo envolvente ≤ 0,85 | mapa de alturas por coluna; área ocupada ÷ área do retângulo |
| G2 Coroamento | pelo menos 90% do topo em telhado inclinado, cônico ou cúpula; terraço plano só com parapeito de 1 bloco ou mais e um elemento vertical | camada mais alta de cada coluna; tipo de bloco (escada/laje) |
| G3 Profundidade | fachada principal com pelo menos 2 níveis de profundidade (≥ 20% das células de parede avançam ou recuam 1 bloco ou mais) | compara o plano da parede com os blocos à frente e atrás |
| G4 Textura | nenhuma janela de 5×5 da fachada com um único bloco (vidro e telhado à parte); pelo menos 3 famílias estruturais | janela deslizante sobre as faces externas; contagem por família |
| G5 Aberturas | pelo menos 1 abertura a cada 6 blocos de fachada; janelas de 1×2 ou maiores; portal principal com destaque | varredura de buracos na parede externa |
| G6 Faixas | embasamento, cinta ou cornija e beiral diferentes do plano da parede | presença de camadas `BASE` e `TRIM` nos níveis esperados |
| G7 Interior | cada cômodo com pelo menos 3 itens funcionais e luz; a entrada alcança todos os cômodos | tags `ROOM`; BFS com folga 1×2 a partir da entrada |
| G8 Estrutura | zero blocos flutuando (exceto tag `FLOATING_OK`); fundação contínua até o solo | BFS de suporte a partir do solo e da fundação |
| G9 Luz | nenhum ponto caminhável a mais de 7 blocos de um emissor (interior) ou 10 (exterior) | distância BFS ao emissor mais próximo |
| G10 Identidade | marca registrada e pelo menos 3 acentos de tema presentes | tags `LANDMARK` e `ACCENT` do Blueprint |

## 7. Arquitetura condizente com o pedido

Antes de codar, escreva uma linha traduzindo o pedido em arquitetura (“biblioteca → átrio com cúpula, janelões, mezanino”) e confira com a tabela. Se o pedido citar estilo, escala ou material, isso vence a tabela. Pedido pequeno não é construção pobre: uma cabana ainda tem beiral, chaminé, varanda e floreira.

| O pedido contém | Assinatura arquitetônica obrigatória |
|---|---|
| biblioteca | janelões altos (1×3 ou mais), átrio com claraboia ou cúpula, mezanino com guarda-corpo, paredes de estantes, nichos de leitura, torre ou cúpula como marca |
| dormitório, alojamento | baías repetidas com janelas pequenas, sacadas, chaminés, lucarnas, torreão de escada, enxaimel, sala comum com lareira |
| refeitório, grande salão | nave única alta (10 ou mais livres) com vigas, telhado de duas águas íngreme, rosácea na empena, lareiras, anexo de cozinha com chaminé |
| torre do relógio, sino | fuste esbelto (11×11 ou menos), cintas, mostradores nos 4 lados, câmara do sino com venezianas, agulha ou pináculo |
| torre de mago, arcana | planta circular ou octogonal, anéis de raio variável, cúpula de observatório, cristais, varandas, escada espiral |
| arena, coliseu, treino | planta oval ou circular, arquibancadas em degraus, anel de arcos, portões, torres de acesso, estandartes |
| enfermaria | planta em cruz ou pátio, ala de leitos, jardim de ervas, cores claras, telhado verde-água |
| ferraria, oficina | volume baixo e largo, chaminé alta, abertura frontal, telhado assimétrico, pedra escura |
| portão, entrada | arco central de 5×7 ou maior, torres gêmeas, ameias, grade e brasão, ponte ou escadaria |
| muralha | batente (base mais larga), caminho de ronda, ameias, torres a cada 14 a 20 blocos, contrafortes |
| estábulo, poleiro | galpão longo e baixo com portões grandes, sótão de feno, torre de pouso para montarias aladas |
| estufa | estrutura leve com abóbada de vidro nervurada, canteiros, fonte |
| ponte | arcos sob o tabuleiro, guarda-corpo, lanternas a cada 4 ou 5 blocos, leve elevação central |
| casa, cabana | volumes cruzados, lucarna, chaminé, varanda, jardim |
| ruína, antigo | partes colapsadas, muito musgo e rachadura, trepadeiras, vegetação invadindo |

## 8. Regiões e campus

Uma região é uma lista de construções + terreno + ligações. Modele como dados (`CampusLayout`: id, tipo, posição relativa à origem do campus, rotação, parâmetros, paleta, seed) e execute em etapas: **terraplanagem → caminhos e água → edifícios (do maior para o menor) → pontes e galerias → vegetação → props e luz**. Receitas em `references/kit-e-formas.md` §4.12.

- Gire cada prédio para a frente apontar para a praça ou o caminho mais próximo.
- Terraplane só o necessário: pad do prédio + talude suave de 4 a 8 blocos; nunca deixe paredões lisos.
- Reuse features vanilla (cerejeira, carvalho, azaleia) para árvores; reinventar copa de árvore dá resultado pior.
- Árvores e flores em grupos de densidade variável (Poisson disk modulado por ruído), nunca em grade; mantenha 2 blocos de afastamento de caminhos e paredes.
- O plano do campus (`mc-planejamento-construcao`, `references/campus-academia.md`) define zonas, eixos e catálogo de prédios; siga-o.

## 9. Validação e pré-visualização

O `Validator` roda sobre o Blueprint, antes de qualquer `setBlock`, e devolve um relatório (erros bloqueiam, avisos informam):

1. **Estados**: portas com duas metades, camas com duas partes, plantas altas completas, propriedades válidas em escadas, portões e alçapões.
2. **Paleta**: todo ID existe no 1.21.1 (falha com a lista completa).
3. **Estrutura**: zero blocos flutuando (BFS de suporte; exceções com a tag `FLOATING_OK`) e fundação contínua até o solo.
4. **Alcançabilidade**: BFS com folga 1×2 e degraus de no máximo 1 bloco a partir da entrada; todo cômodo marcado como `ROOM` precisa ser alcançado.
5. **Luz**: nenhum ponto caminhável a mais de 7 blocos de um emissor (interior) ou 10 (exterior).
6. **Gates** da seção 6, com os números medidos.
7. **Estatística**: contagem por família e papel (confira o 60-30-10), bounding box, total de blocos, estimativa de ticks de aplicação.

O `Preview` imprime planta ASCII por andar (legenda: `#` parede, `.` ar, `=` piso, `^` telhado, `D` porta, `W` janela, `L` luz, `F` móvel), **elevações** N/S/L/O (altura máxima por coluna) e as estatísticas. Use as elevações para checar a silhueta (pelo menos 3 alturas por lado) sem abrir o jogo.

Comandos de depuração (adapte o prefixo ao projeto): `/campus dryrun <id> [seed]` valida e imprime sem tocar o mundo; `/campus build <id> [rot] [seed]` aplica; `/campus undo` desfaz o último job; `/campus test orientation` gera o tabuleiro de orientação (escadas, portas, alçapões, camas e baús nas 4 direções, com rotação e espelho).

**Revise em jogo, nesta ordem**: 100 blocos (silhueta e cor do telhado) → 30 (fachada, profundidade, janelas) → 5 (detalhes e transições) → interior (circulação, luz, mobília) → à noite (janelas acesas, pontos escuros) → de cima (planta e caminhos). Se algo parecer errado, peça uma captura de tela ou as coordenadas; não adivinhe.

## 10. Aplicação no mundo (resumo)

- Aplique por `BuildJob` com orçamento de tempo por tick (≈8 ms), ordenado por chunk e Y crescente; blocos que dependem de suporte (lanternas, trepadeiras, tapetes, placas, botões, velas, flores) vão por último.
- Use `Block.UPDATE_CLIENTS` na colocação em massa e depois rode o fix-up com `Block.updateFromNeighbourShapes` em escadas, cercas, muros, vidraças, grades e portões.
- Limpe o terreno da pegada antes (inclua árvores inteiras que invadem). Limpe containers (`Clearable.tryClear`) antes de substituí-los, senão soltam itens.
- Água e lava só em bacia fechada; areia e cascalho só com suporte sólido; folhas com `persistent=true`.
- BlockEntities (baús com conteúdo, placas, estandartes com padrões) e entidades (quadros, suportes de armadura) entram depois dos blocos e de forma idempotente (marque as entidades com tag para removê-las ao reconstruir).
- Detalhes, trechos de código e armadilhas de estados: `references/blockstates-e-neoforge-1-21-1.md`.

## 11. Entrega em Runs

Entregue em **Runs**: cada Run termina em algo testável em jogo, com a lista “como testar” (comandos e o que olhar).

- **Run 0 — Kit mínimo** (se não existir): Blueprint, Palette, Noise, Transform, formas básicas (caixa, paredes, piso, telhado de duas águas, janela, portal, porta), Validator básico (estados, paleta, flutuantes), Preview, BuildJob com fix-up e undo, comandos `dryrun`/`build`/`undo` e o tabuleiro de orientação. Teste: tabuleiro correto nas 4 rotações + uma casinha completa.
- **Run 1 — Volumes, fundação e telhado**: silhueta aprovada (elevações com 3 alturas ou mais por lado).
- **Run 2 — Fachada e aberturas**: relevo, cintas, cantos, portais, janelas, venezianas.
- **Run 3 — Interior**: pisos, escadas, cômodos mobiliados, luz; alcançabilidade verde.
- **Run 4 — Entorno e terreno**: pad e talude, caminhos, vegetação, postes.
- **Run 5 — Envelhecimento, magia e QA**: ruído, musgo, acentos mágicos, validação completa, exportação NBT opcional.

Para o campus inteiro, repita o ciclo por prédio na ordem do plano de campus.

## 12. Segurança em servidor

- Lembre o usuário de fazer **backup do mundo** antes do primeiro `build` real.
- Comandos exigem permissão de operador (nível 2 ou mais); `build` sobre área com construções não naturais exige `confirm`.
- Sempre `dryrun` antes de `build`; imprima bounding box e número de blocos.
- Tire um snapshot da região (NBT) antes de aplicar, para o `undo`.
- Registre no log quem rodou, o quê, onde e com qual seed.
- Limite o tamanho por execução (por exemplo, 2 milhões de blocos) e divida regiões maiores em jobs.

## 13. Quando algo dá errado

| Sintoma | Causa provável | Correção |
|---|---|---|
| Escadas e telhado invertidos | `facing` da escada é a direção em que ela **sobe** | rode o tabuleiro de orientação; confira `facing` e `half` |
| Cercas, muros e vidraças desconectados | conexões não calculadas (colocação com flag 2) | fix-up com `Block.updateFromNeighbourShapes` |
| Porta ou cama pela metade | faltou a segunda célula | use `Furniture.door` e `Furniture.bed` |
| Folhas somem com o tempo | `persistent=false` | `persistent=true` |
| Prédio flutuando ou enterrado | altura do solo medida sobre folhagem ou plantas | `groundY` ignorando folhas, troncos e plantas; fundação até o solo |
| Lag ao construir | tudo num tick só ou flag 3 | `BuildJob` com orçamento, flag 2, ordem por chunk |
| Água ou lava escorrendo | sem bacia fechada | contenção e validador |
| Areia ou cascalho caindo | sem suporte | base sólida sob blocos com gravidade |
| Interior escuro, monstros nascendo | luz insuficiente | `Lighting.ensureCoverage` |
| Ar onde devia haver bloco | ID inexistente no 1.21.1 (`BuiltInRegistries.BLOCK.get` devolve ar) | `BlockStateParser` e falha explícita na carga |
| Prédio “plano” apesar dos passes | parâmetros pequenos ou passes sem efeito | `Preview.elevation`; gates G1 a G3 |
| Resultado muda a cada execução | `Random` global | derive tudo de seed + posição |

## Referências

- `references/kit-e-formas.md` — contrato do kit, código de ruído e paleta, receitas de telhados, torres, arcos, janelas, escadas, pontes, terreno e envelhecimento.
- `references/blockstates-e-neoforge-1-21-1.md` — APIs do 1.21.1, flags de `setBlock`, armadilhas de estados de bloco, fix-up, `BuildJob`, altura do solo, undo e desempenho.
- `examples/exemplo-dormitorio.md` — do plano ao código, passe a passe, para um dormitório enxaimel em L.
- Skill irmã `mc-planejamento-construcao`: plano, paletas, mobília e catálogo do campus.
