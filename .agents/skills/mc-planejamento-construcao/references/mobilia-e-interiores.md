# Mobília e interiores · Minecraft 1.21.1

## Índice
1. Regras de layout
2. Receitas de móveis em blocos
3. Receitas de cômodos
4. Iluminação em camadas

---

## 1. Regras de layout

- **Encoste na parede.** Móveis ficam junto às paredes; o centro fica livre em salões e circulação de 1 a 2 blocos em salas pequenas.
- **Um ponto focal por cômodo**: lareira, lousa, altar, janela grande, mesa do professor. O resto do layout se organiza em torno dele.
- **Agrupe por função** (zona de estudo, zona de descanso). Simetria em espaços formais (salões, capela, hall); assimetria em quartos e salas comuns.
- **Três alturas**: chão (tapetes), 1 bloco (mesas, baús, bancos), 2 ou mais (estantes, lustres, estandartes). Cômodo plano parece vazio.
- **Densidade**: 25% a 35% do piso ocupado em salas habitadas; no máximo 15% em corredores e saguões.
- **Uso coerente**: cadeira de frente para a mesa, cabeceira da cama na parede, lareira no centro da parede menor.
- **Nunca bloqueie passagem**: porta e escada precisam de 2 blocos livres acima e 1 bloco de folga na frente.

---

## 2. Receitas de móveis em blocos

Os exemplos usam `spruce`; troque pela madeira da paleta. Orientação (`facing`) é decidida no código; aqui vai só a intenção.

- **Mesa pequena**: `spruce_fence` com `spruce_pressure_plate` por cima.
- **Mesa longa** (refeitório): fileira de `spruce_slab[type=top]` apoiada em `spruce_fence` a cada 2 ou 3 blocos; toalha opcional com `carpet` por cima.
- **Cadeira**: `spruce_stairs` com o encosto **para longe** da mesa (a escada “sobe” em direção ao encosto). Com costas altas: `spruce_trapdoor` aberto atrás do assento.
- **Banco comprido**: fileira de `stairs` com o mesmo sentido, ou `slab[type=bottom]` sobre `fence`.
- **Carteira de sala de aula**: mesa pequena + cadeira atrás; fileiras com 1 bloco de corredor entre elas.
- **Estante**: `bookshelf` empilhado em 2 a 3 de altura, encostado na parede. Alterne com `barrel`, `lectern` e `chiseled_bookshelf` (pouco). Prateleira de parede: `slab[type=top]` com `flower_pot` ou `candle`.
- **Cama**: `*_bed` (duas partes: pé e cabeceira), cabeceira na parede. Criado-mudo: `barrel` ou `chest` com `candle`; baú no pé da cama.
- **Guarda-roupa**: dois `barrel` lado a lado, com `trapdoor` fechados como portas.
- **Lareira**: nicho de 3 de largura em `stone_bricks`; `campfire[lit=true]` no chão; `iron_bars` ou `trapdoor` no alto da abertura; manto em `stone_brick_slab[type=top]` com `lantern`; chaminé oca até acima do telhado. **Nunca `fire`**: espalha para a madeira.
- **Lustre simples**: `chain` do teto até uma `lantern[hanging=true]`.
- **Lustre grande**: disco de `iron_bars` (ou `trapdoor` aberto) pendurado por 4 `chain`, com 4 a 8 `candle[lit=true]` por cima.
- **Tapete**: `*_carpet`; corredor-tapete com duas cores (borda e miolo).
- **Lousa**: painel de 5×2 em `deepslate_tiles` ou `polished_blackstone`, emoldurado com `stripped_spruce_log` ou `spruce_planks`; bandeja de giz com `spruce_slab[type=top]` embaixo.
- **Mesa do professor**: `lectern` ao lado de uma mesa pequena, com `bookshelf` atrás.
- **Balcão ou bar**: `barrel` ou `stripped_*_log` com `slab[type=top]` por cima.
- **Cozinha**: `smoker`, `furnace`, `blast_furnace`, `cauldron`, `composter`, `barrel`; coifa em `stone_brick_stairs[half=top]` com chaminé; utensílios pendurados em `chain` e `iron_bars`.
- **Alquimia**: `brewing_stand` sobre mesa ou laje, `cauldron`, `flower_pot` com ervas, `decorated_pot`, `amethyst_cluster` em suportes.
- **Forja**: `anvil`, `grindstone`, `smithing_table`, `blast_furnace`, `cauldron` com água, `barrel` de ferramentas; brilho de `shroomlight` atrás de grade de `iron_bars`.
- **Altar**: base em `stone_brick_slab` ou `chiseled_stone_bricks`, `lectern`, `candle` em grupos, vitrais atrás, tapete central; bancos em `stairs` + `slab`.
- **Poste de luz**: `stone_brick_wall` na base, `fence` ou `wall` como haste e `lantern` no topo; ou braço de `fence` com `chain` e `lantern[hanging=true]`.
- **Banco de pátio**: `stairs` nas pontas (voltadas para dentro) e `slab` no meio.
- **Floreira**: `trapdoor` sob a janela com `flowering_azalea`, flores ou `flower_pot`.
- **Cortina ou divisória**: `*_carpet` ou estandartes lisos pendurados; biombo de `trapdoor` abertos alinhados.
- **Sino**: `bell` pendurado sob uma viga ou entre dois postes.

---

## 3. Receitas de cômodos

**Sala de aula** (interior ≈9×11, 4 livres): lousa de 5×2 na parede da frente; mesa do professor com `lectern`; 3 fileiras de 3 a 4 carteiras com corredor de 1 entre elas; estantes laterais; janelas altas no lado oposto à lousa; lustre ou lanternas; tapete na frente; estandarte da academia ao lado da lousa; um vaso numa janela.

**Quarto de dormitório** (≈5×6): duas camas com a cabeceira na parede, nas cores da casa; criado-mudo com vela ou lanterna entre elas; baú no pé de cada cama; escrivaninha sob a janela; tapete central; guarda-roupa; estandarte da casa. Porta com 1 bloco de folga.

**Sala comum do dormitório** (≈10×8): lareira na parede menor, com manto e lanterna; poltronas de `stairs` em semicírculo; mesa baixa (`slab` sobre `fence`); estante de livros e jogos; quadro de avisos em `dark_oak_planks` emoldurado com alçapões.

**Biblioteca**: corredores de estantes (`bookshelf` em 3 a 4 de altura) com 2 de passagem entre eles; mezanino com guarda-corpo (`iron_bars` ou `fence`) e escada; mesas de leitura longas com `lantern` ou `candle`; nichos de leitura junto às janelas (banco de `stairs`, vela, tapete); balcão de empréstimo com `lectern`; escada de biblioteca (`ladder` com `fence`); lustres grandes no átrio; claraboia ou cúpula no centro.

**Refeitório / Grande Salão**: três mesas longas no sentido do comprimento (mesa + banco dos dois lados), espaçadas de 3 blocos; mesa alta num tablado de 1 bloco na cabeceira; lareiras grandes nas laterais ou na cabeceira; vigas expostas; estandartes por turma; 3 a 4 lustres; balcões de servir junto à porta da cozinha.

**Cozinha**: ilha central (`barrel` + `slab`), fogões sob coifa com chaminé, `cauldron` com água, bancadas de `barrel`, despensa (`barrel`, `hay_block`, `composter`), janelas pequenas.

**Enfermaria**: leitos de `white_bed` separados por divisórias de alçapões ou estandartes lisos; farmácia com estantes, `brewing_stand`, `flower_pot` e `decorated_pot`; mesa de cirurgia (`quartz_slab` com `white_carpet`); janela para o jardim de ervas; luz clara e muitas plantas.

**Laboratório de alquimia**: bancadas em U com `brewing_stand`, `cauldron` e vasos de ervas; estantes com `decorated_pot`; `amethyst_cluster` em suportes; `lectern` com o livro de receitas; piso de `polished_deepslate`; janela alta; `soul_lantern` pendurada em corrente.

**Arena: vestiário e camarote**: vestiário com bancos de `stairs`, armários de `barrel` ou `chest` e um `cauldron` com água; camarote como varanda sobre a arquibancada, com poltronas, `lectern` e estandartes; depósito de armas com racks de `fence` e `iron_bars`, mais `target`; piso de terra batida com marcações em `polished_andesite`.

**Ferraria / oficina**: forja central (`blast_furnace`, `anvil`, `cauldron`), bancada de ferramentas (`grindstone`, `smithing_table`, `barrel`), racks de `iron_bars` e `chain` nas paredes, brilho de forja escondido, piso de `cobblestone` ou `blackstone`.

**Estábulo e poleiro de grifos**: baias de ≈3×4 com `fence` e `fence_gate`, manjedoura de `cauldron` com água e `hay_block`, selaria com `barrel` e `chest`, feno no sótão. Poleiro: plataforma de pouso de ≈7×7 com grade de `iron_bars`, ninhos de `hay_block` com `moss_block` e um mastro de estandarte.

**Sala do diretor**: mesa grande (`dark_oak_slab` sobre `dark_oak_fence`), poltrona atrás, estantes até o teto, mesa de mapas (`cartography_table`) no centro, janelão atrás da mesa, lareira, estandartes e vitrine de troféus (`glass` com `decorated_pot` ou `gold_block`).

**Hall de entrada e corredores**: tapete-corredor central em duas cores; colunas com lanternas; escadaria monumental ao fundo; lustre alto (pelo menos 2 blocos acima do pé-direito normal); estátuas e brasões; bancos de espera nas laterais; quadro de avisos. Corredores: arandelas (`lantern` em consolos) a cada 5 a 6 blocos, tapete fino, nichos com plantas ou armaduras.

**Capela / santuário**: nave com bancos (`stairs` + `slab`), tapete central, altar elevado em 1 a 2 degraus com `lectern` e velas em grupos, vitrais na parede do fundo, sino na torre; galeria de estátuas dos heróis em nichos.

**Salão de Missões (guilda)**: grande quadro de missões na parede (painel de `dark_oak_planks` emoldurado com alçapões e cartazes de `*_carpet` e estandartes lisos), balcão de atendimento (`barrel` + `slab`), mesas de equipe, mapa na `cartography_table`, vitrine de troféus.

---

## 4. Iluminação em camadas

- **Camadas**: ambiente (lustres, luz embutida), funcional (lanternas em mesas e bancadas), destaque (velas em estantes e altares, braseiros).
- **Espaçamento**: um emissor de luz 15 a cada 7 blocos ou menos em interiores e 10 ou menos em exteriores. Velas (3 por vela) contam como luz de destaque, não de cobertura.
- **Esconda a fonte**: `sea_lantern`, `shroomlight` ou `glowstone` atrás de alçapões, grades de `iron_bars`, vigas ocas ou lajes invertidas; `lantern` em consolos ou correntes; `candle` em estantes.
- **Janelas acesas à noite**: um emissor a 1 ou 2 blocos atrás de cada 2 ou 3 janelas.
- **Altura dos pendurados**: a parte de baixo do lustre fica pelo menos 2 blocos acima do piso livre.
- **Tocha aparente**: só em estruturas rústicas ou industriais (arena, ferraria, muralha), e mesmo ali prefira `lantern` e `campfire`.
- **Monstros**: no 1.21.1 os monstros comuns só nascem com luz de bloco 0. Nenhum piso caminhável pode ficar em 0; mire em 8 ou mais por conforto visual.
