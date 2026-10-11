# Paletas de blocos · Minecraft 1.21.1

## Índice
1. Como montar uma paleta
2. Famílias com escada, laje e muro
3. Paletas prontas (P1 a P8)
4. Kits: luz, vegetação, detalhes e magia
5. Blocos a evitar

---

## 1. Como montar uma paleta

Defina **papéis** e, para cada papel, uma lista de blocos com pesos (soma 100). O código sorteia por papel com ruído determinístico (ver a skill `mc-construcao-estruturas`).

Papéis usuais: `BASE` (embasamento) · `WALL` (parede principal) · `WALL_ALT` (painéis e detalhes de parede) · `PILLAR` (pilastras, cantos, vigas) · `TRIM` (molduras, cintas, cornijas) · `ROOF` (telhado: bloco + escada + laje) · `WINDOW` (vidro) · `FRAME` (caixilhos, venezianas, grades) · `FLOOR` · `ACCENT` · `LIGHT`.

Regras:
- **60-30-10**: 60% de material dominante, 30% secundário, 10% acento.
- Escreva IDs completos com namespace (`minecraft:stone_bricks`) e, quando preciso, propriedades (`minecraft:lantern[hanging=true]`).
- Papéis que viram inclinação (`ROOF`, `TRIM`) precisam de família com **escada e laje**; consulte a seção 2 antes de escolher.
- 2 a 3 blocos por papel principal; o primeiro domina (50 a 60%). Variantes gastas (musgosa, rachada) entram com peso baixo e mais perto do chão.
- **Contraste de valor**: `BASE` mais escura que `WALL`; `ROOF` contrasta com `WALL` (escuro sobre claro, ou saturado sobre neutro).
- Prefira as variantes **enceradas** (`waxed_*`) de cobre: não oxidam com o tempo e a cor fica estável.
- Um tom quente + um frio por prédio; no máximo 5 famílias de material e 2 cores saturadas.

---

## 2. Famílias com escada, laje e muro

As variantes seguem o prefixo da coluna “Prefixo”, acrescido de `_stairs`, `_slab` e `_wall` (só as que existem). Os nomes são irregulares (`stone_bricks` → `stone_brick_stairs`), por isso confira sempre.

| Bloco | Prefixo | Tom | Variantes que existem |
|---|---|---|---|
| `stone_bricks` | `stone_brick` | cinza neutro | stairs, slab, wall |
| `mossy_stone_bricks` | `mossy_stone_brick` | cinza + musgo | stairs, slab, wall |
| `cobblestone` | `cobblestone` | cinza irregular | stairs, slab, wall |
| `mossy_cobblestone` | `mossy_cobblestone` | cinza + musgo | stairs, slab, wall |
| `andesite` | `andesite` | cinza | stairs, slab, wall |
| `polished_andesite` | `polished_andesite` | cinza claro liso | stairs, slab |
| `polished_diorite` | `polished_diorite` | quase branco | stairs, slab |
| `end_stone_bricks` | `end_stone_brick` | creme | stairs, slab, wall |
| `smooth_quartz` | `smooth_quartz` | branco | stairs, slab |
| `sandstone` | `sandstone` | areia | stairs, slab, wall |
| `smooth_sandstone` | `smooth_sandstone` | areia lisa | stairs, slab |
| `cut_sandstone` | `cut_sandstone` | areia cortada | slab |
| `bricks` | `brick` | vermelho-tijolo | stairs, slab, wall |
| `mud_bricks` | `mud_brick` | marrom terroso | stairs, slab, wall |
| `nether_bricks` | `nether_brick` | vinho escuro | stairs, slab, wall |
| `red_nether_bricks` | `red_nether_brick` | vermelho escuro | stairs, slab, wall |
| `deepslate_bricks` | `deepslate_brick` | grafite azulado | stairs, slab, wall |
| `deepslate_tiles` | `deepslate_tile` | grafite (telhas) | stairs, slab, wall |
| `polished_deepslate` | `polished_deepslate` | grafite liso | stairs, slab, wall |
| `cobbled_deepslate` | `cobbled_deepslate` | grafite bruto | stairs, slab, wall |
| `polished_blackstone_bricks` | `polished_blackstone_brick` | preto arroxeado | stairs, slab, wall |
| `tuff_bricks` | `tuff_brick` | cinza esverdeado | stairs, slab, wall |
| `polished_tuff` | `polished_tuff` | cinza esverdeado liso | stairs, slab, wall |
| `prismarine_bricks` | `prismarine_brick` | verde-azulado | stairs, slab |
| `dark_prismarine` | `dark_prismarine` | verde-azulado escuro | stairs, slab |
| `cut_copper` (e `exposed_`, `weathered_`, `oxidized_`, `waxed_…`) | `cut_copper` | laranja → verde-azulado | stairs, slab |
| `purpur_block` | `purpur` | lilás | stairs, slab |
| Madeiras: `oak`, `spruce`, `birch`, `jungle`, `acacia`, `dark_oak`, `mangrove`, `cherry`, `bamboo`, `crimson`, `warped` | `<madeira>` | cada uma com sua cor | planks, stairs, slab, fence, fence_gate, door, trapdoor, button, pressure_plate, sign |
| `calcite`, terracotas, concretos, lãs | — | brancos e cores chapadas | nenhuma (sem escada, laje ou muro) |

Notas:
- **Telhado azul** não existe em escada vanilla. Para tons azulados use `warped`, `prismarine_brick`, `dark_prismarine` ou cobre oxidado (`waxed_oxidized_cut_copper`); blocos cheios azuis (`blue_terracotta`, `light_blue_terracotta`, `lapis_block`) servem só para remates.
- Cobre encerado completo: `waxed_oxidized_cut_copper`, `waxed_oxidized_cut_copper_stairs`, `waxed_oxidized_cut_copper_slab`; o mesmo vale para `waxed_weathered_*` e `waxed_exposed_*`.
- Madeiras `crimson` e `warped` usam `_stem` e `_hyphae` no lugar de `_log` e `_wood`; bambu usa `bamboo_block` e `bamboo_mosaic`.
- `calcite` e terracotas não têm escada: combine-os com uma família vizinha (por exemplo `polished_diorite` ou `stone_bricks`) para molduras.

---

## 3. Paletas prontas

Cada paleta indica o uso, os papéis e pesos. Sempre que escrever “+ `_stairs` + `_slab`”, inclua as três formas da família.

### P1 · Academia Real — pedra clara + cobre oxidado
Uso: edifício principal, salões, torre do relógio, pavilhões de aulas, capela.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `deepslate_bricks` 50 · `polished_deepslate` 30 · `stone_bricks` 15 · `cracked_deepslate_bricks` 5 |
| `WALL` | `end_stone_bricks` 50 · `stone_bricks` 25 · `calcite` 15 · `polished_diorite` 10 |
| `PILLAR` | `polished_andesite` 70 · `stone_bricks` 30 (capitel e base em `chiseled_stone_bricks`) |
| `TRIM` | `stone_brick_stairs` / `stone_brick_slab` · `end_stone_brick_stairs` / `end_stone_brick_slab` · `smooth_stone_slab`; cornijas com escadas `half=top` |
| `ROOF` (padrão) | `waxed_oxidized_cut_copper` + `_stairs` + `_slab` 75 · `waxed_weathered_cut_copper` + `_stairs` + `_slab` 25, em faixas de 2 a 3 fiadas |
| `ROOF` (alternativas) | `deepslate_tile_stairs` / `deepslate_tile_slab` (grafite) · `warped_stairs` / `warped_slab` (turquesa) |
| `WINDOW` | `glass_pane` 70 · `light_blue_stained_glass_pane` 30 |
| `FRAME` | `dark_oak_trapdoor` · `spruce_trapdoor` · `iron_bars` |
| `FLOOR` | `polished_andesite` · `smooth_stone` · `stone_bricks` em padrão; tapete-corredor `blue_carpet` com borda `yellow_carpet` |
| `ACCENT` | estandartes azul, branco e amarelo · `lightning_rod` (ponta de agulha) · `waxed_cut_copper` · `gold_block` só no brasão e no relógio |
| `LIGHT` | `lantern` · `candle` (lit) · `sea_lantern` atrás de alçapões · `end_rod` nos pináculos |

Variantes rápidas: **Enfermaria** = P1 mais clara (`calcite` 40), telhado verde (`waxed_weathered_cut_copper`), acento verde ou vermelho e muitas plantas. **Capela** = P1 com vitrais multicoloridos e `candle` em grupos. **Casa do Diretor** = P1 com a madeira de P2 nos beirais.

### P2 · Dormitório Enxaimel — reboco claro + madeira escura + telhado quente
Uso: dormitórios, casas, estábulos, anexos acolhedores.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `cobblestone` 40 · `stone_bricks` 30 · `mossy_cobblestone` 15 · `andesite` 15 |
| `WALL` (reboco) | `calcite` 50 · `white_terracotta` 30 · `polished_diorite` 20 |
| `PILLAR` (madeira) | `stripped_dark_oak_log` 60 · `dark_oak_log` 40 (eixo `y` nos pilares, `x`/`z` nas vigas) |
| `TRIM` | `dark_oak_stairs` / `dark_oak_slab` (diagonais e cornijas) |
| `ROOF` A (madeira) | `spruce_stairs` / `spruce_slab` 70 · `dark_oak_stairs` / `dark_oak_slab` 30 |
| `ROOF` B (telha) | `brick_stairs` / `brick_slab` 70 · `mud_brick_stairs` / `mud_brick_slab` 30 |
| `WINDOW` | `glass_pane` |
| `FRAME` | `spruce_trapdoor` (venezianas abertas) · `spruce_fence` (peitoril) |
| `FLOOR` | `spruce_planks` 50 · `oak_planks` 30 · `dark_oak_planks` 20; vigas `stripped_dark_oak_log` |
| `ACCENT` | estandarte da casa · `barrel` · `flower_pot` · `hay_block` |
| `LIGHT` | `lantern` · `candle` · `campfire` (lareira) |

### P3 · Torre Arcana — ardósia + roxo mágico
Uso: torre dos magos, observatório, laboratórios.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `polished_deepslate` 50 · `cobbled_deepslate` 30 · `deepslate_tiles` 20 |
| `WALL` | `deepslate_bricks` 55 · `tuff_bricks` 25 · `polished_deepslate` 20 |
| `PILLAR` / anéis | `purpur_pillar` · `purpur_block` · `chiseled_deepslate` |
| `ROOF` A (roxo) | `purpur_block` + `purpur_stairs` + `purpur_slab`; remate com `amethyst_block` |
| `ROOF` B (turquesa) | cobre oxidado encerado, como em P1 |
| `WINDOW` | `purple_stained_glass_pane` 40 · `blue_stained_glass_pane` 40 · `glass_pane` 20; vitrais grandes com `tinted_glass` |
| `ACCENT` (magia) | `amethyst_block` · `amethyst_cluster` · `large_amethyst_bud` · `crying_obsidian` (pouco) · `enchanting_table` · `lodestone` |
| `FLOOR` | `polished_deepslate` · `deepslate_tiles`; tapete `purple_carpet` ou `black_carpet` |
| `LIGHT` | `end_rod` · `sea_lantern` embutida · `soul_lantern` · `purple_candle` e `blue_candle` |

### P4 · Arena de Treino — arenito + madeira escura
Uso: arena, dojo, arquibancadas, galerias de treino.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `stone_bricks` 60 · `cobblestone` 25 · `andesite` 15 |
| `WALL` | `sandstone` 35 · `smooth_sandstone` 30 · `cut_sandstone` 30 · `chiseled_sandstone` 5 |
| `PILLAR` | `dark_oak_log` · `stripped_dark_oak_log` |
| `TRIM` | `sandstone_stairs` / `sandstone_slab` · `smooth_sandstone_stairs` / `smooth_sandstone_slab` |
| `ROOF` (galerias) | `dark_oak_stairs` / `dark_oak_slab` 70 · `spruce_stairs` / `spruce_slab` 30 |
| `FLOOR` (arena) | `coarse_dirt` 45 · `packed_mud` 25 · `sand` 20 (só sobre base sólida) · `dirt_path` 10; marcações em `polished_andesite` |
| `ACCENT` | estandartes vermelho e dourado · `iron_bars` · `chain` · `target` · `hay_block` · `grindstone` |
| `LIGHT` | `campfire` em braseiros sobre `stone_brick_wall` · `lantern` · `shroomlight` embutida |

### P5 · Biblioteca e Salões Quentes — pedra + madeira escura
Uso: biblioteca, refeitório, salão de missões, sala do diretor.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `stone_bricks` 55 · `mossy_stone_bricks` 10 · `cobblestone` 20 · `andesite` 15 |
| `WALL` | `stone_bricks` 50 · `polished_andesite` 20 · `tuff_bricks` 20 · `mossy_stone_bricks` 10 |
| `PILLAR` | `stripped_spruce_log` · `dark_oak_log` (vigas expostas) |
| `ROOF` | `deepslate_tile_stairs` / `_slab` 70 · `cobbled_deepslate_stairs` / `_slab` 30 |
| `WINDOW` | `glass_pane` 60 · `orange_stained_glass_pane` 25 · `yellow_stained_glass_pane` 15 (luz âmbar nos vitrais) |
| `FLOOR` | `spruce_planks` 50 · `dark_oak_planks` 40 · `stripped_spruce_log` 10 (faixas); tapetes `red_carpet` e `brown_carpet` |
| `ACCENT` | `bookshelf` · `chiseled_bookshelf` (pouco) · `lectern` · `bell` · `chain` |
| `LIGHT` | `lantern` · `candle` · `shroomlight` embutida entre vigas |

### P6 · Muralha e Portão — pedra pesada
Uso: muralha, torres de guarda, portão, pontes de pedra.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `cobblestone` 40 · `stone_bricks` 35 · `mossy_cobblestone` 15 · `andesite` 10 |
| `WALL` | `stone_bricks` 60 · `mossy_stone_bricks` 15 · `cracked_stone_bricks` 10 · `andesite` 15 |
| `TRIM` | `stone_brick_wall` · `stone_brick_slab` · `stone_brick_stairs`; brasão em `chiseled_stone_bricks` |
| Portão | `iron_bars` · `dark_oak_planks` · `chain` (grade) |
| `ROOF` (torres) | igual a P1 (liga com o campus) ou `deepslate_tile_*` |
| `ACCENT` | estandartes · `lantern` |
| `LIGHT` | `lantern` · `campfire` em braseiros |

### P7 · Jardim Sakura e Estufa — cerejeira + vidro + verde
Uso: jardins, pavilhões, estufa, casa de chá, pontes de madeira.

| Papel | Blocos (pesos) |
|---|---|
| Madeira | `cherry_planks` · `cherry_log` · `stripped_cherry_log` · `cherry_stairs` / `cherry_slab` / `cherry_fence` / `cherry_trapdoor`; contraste com `dark_oak_*` |
| `BASE` | `stone_bricks` 50 · `mossy_stone_bricks` 30 · `cobblestone` 20 |
| Vidro | `glass` · `glass_pane` · `tinted_glass` (pouco) |
| Vegetação | `cherry_leaves` (`persistent=true`) · `pink_petals` · `flowering_azalea` · `azalea` · `moss_block` · `moss_carpet` · samambaias · flores |
| Água | `water` em bacia fechada · `lily_pad` |
| Caminho | `dirt_path` · `gravel` · `stone_brick_slab` · `mossy_cobblestone` (pedras de passagem) |
| `LIGHT` | `lantern` em postes de `cherry_fence` · `candle` |

### P8 · Oficinas e Ferraria — pedra escura + madeira queimada
Uso: ferraria, oficinas, depósitos, cozinha externa.

| Papel | Blocos (pesos) |
|---|---|
| `BASE` | `blackstone` 40 · `cobbled_deepslate` 35 · `polished_blackstone_bricks` 25 |
| `WALL` | `stone_bricks` 40 · `cobblestone` 25 · `deepslate_bricks` 20 · `bricks` 15 |
| `PILLAR` | `spruce_log` · `dark_oak_planks` · `stripped_dark_oak_log` |
| `ROOF` | `deepslate_tile_stairs` / `_slab` ou `spruce_stairs` / `_slab` |
| Equipamento | `anvil` · `grindstone` · `smithing_table` · `blast_furnace` · `furnace` · `smoker` · `cauldron` · `barrel` · `chain` · `iron_bars` |
| `LIGHT` | `campfire` · `shroomlight` ou `ochre_froglight` atrás de `iron_bars` (brilho de forja) · `lantern` |
| Cuidado | `lava` só em bacia fechada; `magma_block` dá dano em piso de circulação |

---

## 4. Kits: luz, vegetação, detalhes e magia

### Luz
| Bloco | Luz | Uso |
|---|---|---|
| `lantern` | 15 | beirais, lustres (`hanging=true`), postes |
| `soul_lantern` | 10 | cantos frios, torre arcana |
| `candle` (1 a 4 velas, `lit=true`) | 3 por vela (máx. 12) | estantes, altares, mesas |
| `end_rod` | 14 | pináculos, magia |
| `campfire` (`lit=true`) | 15 | lareiras, braseiros |
| `sea_lantern`, `glowstone`, `shroomlight`, `ochre_froglight`, `verdant_froglight`, `pearlescent_froglight` | 15 | luz embutida atrás de vidro, alçapões ou vigas |
| `amethyst_cluster` | 5 | acento mágico |
| `glow_lichen` | 7 | acento em paredes úmidas |
| `crying_obsidian` | 10 | acento mágico |
| `light[level=N]` | N | invisível; só quando não houver como esconder uma fonte real |

### Vegetação
- Folhas decorativas, sempre com `persistent=true`: `cherry_leaves`, `azalea_leaves`, `flowering_azalea_leaves`, `oak_leaves`, `birch_leaves`, `spruce_leaves`. Troncos: `cherry_log`, `oak_log`, `birch_log`.
- Chão: `grass_block`, `podzol`, `coarse_dirt`, `rooted_dirt`, `moss_block`, `mud`.
- Rasteira: `short_grass`, `tall_grass`, `fern`, `large_fern`, `moss_carpet`, `pink_petals`.
- Flores: `dandelion`, `poppy`, `blue_orchid`, `allium`, `azure_bluet`, `red_tulip`, `orange_tulip`, `white_tulip`, `pink_tulip`, `oxeye_daisy`, `cornflower`, `lily_of_the_valley`, `lilac`, `rose_bush`, `peony`, `sunflower`.
- Arbustos e trepadeiras: `azalea`, `flowering_azalea`, `vine`, `glow_lichen`, `hanging_roots`, `spore_blossom`, `big_dripleaf`, `small_dripleaf`.
- Água: `lily_pad`, `sugar_cane` (junto à água), `bamboo`.
- Evite em áreas de circulação: `sweet_berry_bush` e `cactus` (causam dano).

### Detalhes
`chain`, `iron_bars`, `scaffolding`, `ladder`, `lightning_rod`, `bell`, `lectern`, `barrel`, `composter`, `cauldron`, `flower_pot`, `decorated_pot`, `hay_block`, `target`, `loom`, `smithing_table`, `fletching_table`, `cartography_table`, `grindstone`, `stonecutter`, `anvil`, `bookshelf`. Estandartes lisos funcionam sem configuração extra; padrões exigem dados no BlockEntity.

### Magia
`amethyst_block`, `amethyst_cluster`, `large_amethyst_bud`, `medium_amethyst_bud`, `small_amethyst_bud`, `end_rod`, `crying_obsidian`, `enchanting_table`, `lodestone`, `purpur_pillar`, `chiseled_deepslate`, `chiseled_stone_bricks` (runas), `sea_lantern`, `tinted_glass` e vitrais. Evite `budding_amethyst` (cresce sozinho) e `beacon` (precisa de base e vidro para o feixe).

---

## 5. Blocos a evitar

**Não existem no 1.21.1** (chegaram em versões posteriores): `pale_oak_*`, `resin_*`, `creaking_heart`, `leaf_litter`, `wildflowers`, `bush`, `firefly_bush`, `copper_lantern`, entre outros. Se um ID de outra versão entrar na paleta, o carregamento falha: o código deve validar todos os IDs na inicialização e listar os inválidos.

**Evite por comportamento:**
- `water` e `lava` fora de bacia fechada (escorrem quando atualizados).
- Blocos com gravidade (`sand`, `red_sand`, `gravel`, `*_concrete_powder`, `anvil`) sem suporte sólido embaixo.
- Folhas sem `persistent=true` (decaem).
- `fire` (espalha para a madeira), `redstone_lamp` (apaga sem energia), `tnt`.
- `farmland` sem água por perto (vira terra).
- `sweet_berry_bush`, `cactus` e `magma_block` em áreas de circulação (causam dano).
- `spawner`, `trial_spawner`, `vault` e `barrier` (só se for intencional e documentado).
- `torch` visível e cru em interiores nobres; prefira `lantern`, `candle` e `campfire`.
