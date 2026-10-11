---
name: mc-planejamento-construcao
description: Planeja e especifica construções e regiões de Minecraft 1.21.1 (NeoForge) em estilo anime + fantasia medieval antes de qualquer código — conceito, silhueta, planta, paleta de blocos, mobília e interiores, acabamento/refinamento (fachada, telhado, janelas, iluminação, detalhes) e naturalidade/integração ao terreno. Use sempre que o pedido for projetar, desenhar, planejar, decorar, definir paleta, melhorar, refinar ou “dar acabamento” em prédio, torre, campus, academia, vila, muralha, jardim, arena ou qualquer região do servidor, mesmo que o usuário só diga “faz a biblioteca” ou “o prédio ficou quadradão”. Entrega um Plano de Construção que a skill mc-construcao-estruturas implementa em código.
---

# Planejamento de construções · Minecraft 1.21.1 · anime + fantasia medieval

Esta skill produz o **Plano de Construção**: o contrato entre a ideia do usuário e o código que vai gerar os blocos. Um bom plano evita os dois erros mais comuns da construção gerada por código: o **caixote sem alma** (paralelepípedo, teto reto, um bloco só) e o **detalhe que contradiz o conceito** (biblioteca com cara de celeiro).

Ela decide *o que* construir e *como deve parecer*. Quem escreve o código é a skill irmã `mc-construcao-estruturas`. Converse com o usuário em português do Brasil; IDs de bloco ficam em inglês.

## Contexto do projeto (edite se o projeto mudar)

- Servidor de **Academia de Heróis** num mundo de **fantasia medieval**; o campus inteiro será gerado por código (Minecraft 1.21.1, NeoForge).
- Estética **anime + fantasia medieval**: silhuetas icônicas e dramáticas (torres, agulhas, cúpulas), cores vivas porém harmônicas, janelas com luz quente, estandartes, cerejeiras, acentos mágicos luminosos. O clima é heroico e acolhedor: é uma escola, não uma fortaleza sombria.
- Os jogadores caminham, treinam e exploram. Tudo precisa ser navegável, bem iluminado e legível de longe: um aluno novo deve achar a biblioteca só pela silhueta.

## Quando usar

Use ao projetar ou refinar qualquer coisa que vá virar blocos: prédio, torre, interior, jardim, muralha, trecho de terreno ou o campus todo. Use também no **modo refinamento** (abaixo) quando o usuário disser que algo está “quadradão”, “vazio”, “sem acabamento” ou “artificial”.

Não use para escrever o código gerador (é a `mc-construcao-estruturas`) nem para dúvidas gerais de mecânica do Minecraft.

## Fluxo

Siga as etapas na ordem. Em pedidos pequenos (uma casinha, um poste) compacte as etapas 2 a 4 em poucas linhas, mas não pule paleta, acabamento nem naturalidade: é neles que a construção deixa de parecer gerada.

### 1. Entender o pedido
Extraia: **função** (o que acontece lá dentro), **tamanho** (use a tabela de `references/campus-academia.md` se o usuário não disse), **posição** (no campus e no relevo), **tom** (prestigioso, aconchegante, antigo, mágico, sombrio) e **restrições** (blocos permitidos, desempenho, prazo). O que faltar, assuma pelo arquétipo e registre em “Suposições” no plano. Só pergunte se a dúvida mudaria o conceito (por exemplo, “dormitório de qual casa?”), no máximo uma pergunta por vez.

### 2. Conceito e identidade
Escreva uma frase de conceito, três palavras-chave de identidade visual e **uma marca registrada**: o elemento que torna o prédio reconhecível (cúpula de cobre oxidado com rosácea; torre torta de mago com anéis flutuantes). Toda construção importante tem uma marca; sem ela o prédio vira “mais um”.

### 3. Massa e silhueta
Decida a forma antes dos detalhes, porque a silhueta é o que se vê a 100 blocos de distância.
- Componha em **três níveis**: massa principal (≈60% do volume), volumes secundários (alas, anexos) e acentos verticais (torres, torreões, lucarnas, chaminés, pináculos).
- **Alturas diferentes**: nunca duas massas coladas com a mesma altura de cumeeira. Razões que funcionam: 1 : 1,5 : 2,5.
- **Planta não retangular** quando o prédio passar de ~12×12: L, T, U, H, pátio interno ou retângulo com projeções (janelas salientes, escadarias, torres de canto).
- **Teste da silhueta**: imagine o contorno visto de norte, sul, leste e oeste. Cada lado precisa de pelo menos três alturas distintas ou de um remate marcante. Se algum lado é um retângulo, redesenhe.

### 4. Planta, níveis e circulação
- Defina entrada principal (voltada para a praça ou o caminho principal), vestíbulo, distribuição (corredor, hall, escadaria) e cômodos.
- Medidas que funcionam: andar de **5 blocos piso a piso** (4 livres) como padrão; salas pequenas com 4 (3 livres); salões com 7 a 10; naves e átrios com 10 a 14.
- Corredores com **3 ou mais de largura** (5 nos principais). Portas comuns 1×2; portais nobres 3×4 em arco.
- Escadas com 2 a 3 de largura; abra no piso de cima um vão de 4 a 5 blocos de comprimento para ninguém bater a cabeça.
- Liste cada cômodo com função e tamanho aproximado. Cômodo sem função vira sala vazia.

### 5. Paleta de blocos
Leia `references/paletas-de-blocos.md`. Resumo das regras:
- **60-30-10**: 60% material dominante (com 2 a 3 variantes misturadas), 30% secundário, 10% acento.
- **Base escura e pesada, topo mais claro ou colorido**; o telhado é a cor de assinatura do prédio.
- **Um tom quente + um frio** (pedra fria × madeira ou dourado quente) dá vida sem brigar.
- No máximo **5 famílias de material** e **2 cores saturadas** por prédio.
- Todo material estrutural precisa de **família completa** (bloco, escada, laje e, se existir, muro) para dar molduras e telhados; confira a tabela de famílias.
- Evite superfícies grandes de concreto, lã ou terracota lisa nas paredes; use-os em acentos e telhados.
- Use somente blocos que existem no **1.21.1**.

### 6. Fachadas e cobertura
Planeje os quatro lados e o céu, não só a frente.
- **Relevo**: nada de parede lisa. Pelo menos dois níveis de profundidade (pilastras, contrafortes, janelas salientes, sacadas, beirais, molduras recuadas).
- **Embasamento** (plinto) diferente do corpo, **cintas** horizontais entre andares, **cantos** reforçados, **cornija ou beiral** no topo.
- **Janelas**: altas (1×2, 1×3, 2×3), emolduradas, com peitoril; ritmo regular com um eixo de simetria na frente; rosácea e vitral nas áreas nobres; venezianas com alçapões.
- **Telhado**: inclinado, com beiral de pelo menos 1 bloco; empenas com detalhe; cumeeira com pináculos ou cata-vento; lucarnas e chaminés quebram a linha. Terraço plano só com parapeito ou ameias e algum elemento vertical.

### 7. Interiores e mobília
Leia `references/mobilia-e-interiores.md`. Cada cômodo recebe: piso, parede interna, **ponto focal** (lareira, lousa, altar, janela grande), mobília funcional, **luz em camadas** e um toque de vida (planta, tapete, estandarte). Prédios que o jogador visita (salões, biblioteca, dormitório, refeitório) têm interior completo; anexos de passagem podem ser mais simples.

### 8. Acabamento (refinamento)
Liste os passes de acabamento no plano. Cada um vira uma função separada no código:
1. **Estrutura e fundação**: plinto, pilares, vigas expostas.
2. **Fachada**: relevo, molduras, cintas, cantos.
3. **Aberturas**: portas, janelas, arcos, venezianas, peitoris.
4. **Cobertura**: beirais, cumeeira, empenas, lucarnas, chaminés, pináculos.
5. **Detalhes externos**: lanternas, estandartes, correntes, brasão, relógio, sino, vasos, trepadeiras, barris.
6. **Interior**: pisos, vigas, móveis, tapetes, lustres.
7. **Iluminação**: ambiente, funcional e de destaque; nunca tochas cruas aparentes; luz embutida atrás de vidro, alçapões ou molduras.
8. **Entorno**: caminhos, canteiros, árvores, bancos, postes, cercas.
9. **Envelhecimento e variação**: musgo, rachaduras, ruído nos materiais, hera.
10. **Toque mágico/anime**: cristais de ametista, bastões de End, brasas, pétalas, luz dourada, runas em blocos cinzelados.

### 9. Naturalidade e terreno
Aplique a seção “Naturalidade” abaixo e registre no plano onde haverá desgaste, que vegetação entra, como o terreno é tratado e como o prédio senta nele.

### 10. Entregar o Plano
Use o template do final deste arquivo e salve em `docs/builds/<nome>.md` (ou onde o projeto guardar documentação), para que código e revisões apontem para ele. Em construções grandes (edifício principal, campus), mostre o plano e espere a aprovação antes de codar; em pedidos pequenos, entregue o plano e siga para a skill de construção.

## Modo refinamento (melhorar o que já existe)

Quando o usuário pedir para melhorar, “dar acabamento” ou disser que ficou quadradão:
1. Audite com o checklist anti-quadradão (abaixo) e liste as falhas.
2. Ordene as melhorias por **impacto visual**: silhueta e proporção → telhado e remate → contraste de materiais → relevo da fachada → ritmo de janelas e portais → iluminação → entorno e terreno → interior → micro-detalhes e envelhecimento.
3. Entregue um plano de refinamento curto (o que mudar, por passe), não um redesenho completo, a menos que a silhueta seja o problema.

## Naturalidade (o que separa construção gerada de construção humana)

- **Ruído, não sorteio puro.** Aleatoriedade uniforme dá “sal e pimenta”. Use manchas (ruído suave) com proporção 70/20/10 entre variantes, mais variação perto da base e menos no alto. Nunca xadrez.
- **Desgaste coerente.** Musgo e rachaduras concentram-se na base, em cantos expostos, perto de água e sob beirais. Prédios novos e nobres (edifício principal) têm pouco desgaste; muralha, torre antiga e ruínas têm bastante.
- **Assimetria controlada.** Fachada principal simétrica em torno de um eixo; alas e fundos assimétricos. Pequenas quebras (um andar que avança 1 bloco, chaminés de alturas diferentes, lucarnas em ritmos distintos) lembram construção feita à mão.
- **Terreno primeiro.** O prédio senta no terreno: a fundação desce até o solo, o desnível vira escadaria, terraço ou muro de arrimo; o talude da plataforma tem inclinação suave (≈1:2), nunca um paredão liso de terra. Nenhum bloco flutuando.
- **Vegetação em grupos.** Árvores em bosquetes de densidade variável, espécies misturadas (cerejeira, carvalho, bétula), sub-bosque (samambaias, flores, arbustos) e trepadeiras nas paredes com comprimentos variados.
- **Caminhos orgânicos.** Largura 3 a 5 nos principais e 2 a 3 nos secundários; traçado levemente curvo; bordas irregulares; materiais misturados (dirt_path, cascalho, pedregulho, terra grossa). Praças com desenho (anéis, rosa dos ventos, brasão da academia).
- **Água com borda.** Lagoas de contorno irregular, profundidade 2 a 3, bordas de areia, argila ou pedra, vitórias-régias. Fontes e canais sempre em bacia fechada.
- **Campus vivo, não maquete.** Reserve pelo menos 40% da área para vegetação, água e terreno natural; construção e pavimento ocupam o resto.

## Checklist anti-quadradão (revisão final do plano)

- [ ] Três ou mais volumes de alturas distintas (ou planta não retangular).
- [ ] Telhado inclinado, cônico ou em cúpula, com beiral; nenhum teto reto sem parapeito e remate.
- [ ] Fachada com pelo menos dois níveis de profundidade e nenhuma região lisa de 5×5 blocos iguais.
- [ ] Embasamento, cinta e cornija distintos do corpo da parede.
- [ ] Janelas altas e emolduradas; portal principal com destaque (arco, escada, brasão).
- [ ] Paleta com 3 a 5 famílias, 60-30-10, variação por ruído, escadas e lajes compatíveis.
- [ ] Marca registrada definida e pelo menos três acentos de tema (estandartes, lanternas, brasão, magia).
- [ ] Todos os cômodos com função, mobília e luz; a entrada alcança todos os cômodos.
- [ ] Fundação adaptada ao terreno; entorno com caminhos e vegetação.
- [ ] Nenhum bloco inexistente no 1.21.1 na paleta.

## Template do Plano de Construção

Entregue exatamente nesta estrutura (preencha todas as seções; escreva “n/a” quando não se aplicar):

```markdown
# Plano: <nome da construção>

## 1. Identidade
- Conceito: <uma frase>
- Palavras-chave: <3>
- Marca registrada: <elemento reconhecível>
- Tom e idade: <novo e nobre | antigo e gasto | aconchegante | mágico ...>

## 2. Dimensões e implantação
- Pegada (L×P) e altura total:
- Andares e pé-direito:
- Posição no campus e orientação da frente:
- Terreno: <plano | encosta | beira d'água> + estratégia de fundação

## 3. Volumes e silhueta
- Massa principal / secundários / acentos verticais (com dimensões e alturas):
- Teste da silhueta (o que se vê de N, S, L, O):

## 4. Planta e circulação
- Entrada, vestíbulo, escadas, corredores (larguras):
- Cômodos por andar: <nome — função — tamanho>

## 5. Paleta
| Papel | Blocos (pesos) |
| --- | --- |
(embasamento, parede principal, parede secundária, pilares/cantos, molduras, telhado [bloco/escada/laje], janelas, piso, acentos, luz)

## 6. Fachadas e cobertura
- Frente / lados / fundos: relevo, ritmo de janelas, molduras
- Telhado: tipo, inclinação, beiral, remates, lucarnas, chaminés

## 7. Interiores e mobília
- Por cômodo: ponto focal, móveis, luz, vida

## 8. Acabamento (passes)
- Os 10 passes e o que cada um inclui neste prédio

## 9. Naturalidade e entorno
- Desgaste (onde e quanto), vegetação, caminhos, água, transição de terreno

## 10. Parâmetros para o código
- Origem e orientação, tamanho variável (L, P, andares), seed, variantes, id da paleta

## 11. Suposições e perguntas abertas
```

## Mapa de referências

- `references/paletas-de-blocos.md`: etapa 5 e sempre que o usuário pedir troca de paleta. Traz famílias com escada/laje/muro, oito paletas prontas, kits de luz e vegetação e a lista de blocos a evitar.
- `references/mobilia-e-interiores.md`: etapa 7. Receitas de móveis em blocos e de 15 tipos de cômodo.
- `references/campus-academia.md`: etapa 1 (tamanhos e arquétipos) e todo plano de região ou campus. Traz zoneamento, circulação, catálogo de edifícios e ordem de construção.
