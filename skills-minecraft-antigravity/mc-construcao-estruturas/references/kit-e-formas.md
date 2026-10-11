# Kit de construção e receitas de forma

## Índice
1. Convenções
2. Contrato do kit
3. Ruído e paleta determinísticos (código)
4. Receitas de forma
   - 4.1 Telhado de duas águas (código)
   - 4.2 Telhados compostos e de quatro águas (mapa de alturas)
   - 4.3 Telhado cônico e agulhas (código)
   - 4.4 Cúpulas
   - 4.5 Portais, arcos e janelas
   - 4.6 Torres
   - 4.7 Contrafortes, cintas e cornijas
   - 4.8 Ameias, muralhas e portões
   - 4.9 Sacadas, janelas salientes, lucarnas e chaminés
   - 4.10 Escadas
   - 4.11 Pontes e galerias
   - 4.12 Terreno e região
   - 4.13 Envelhecimento

Os trechos Java são ilustrativos: imports omitidos (`Direction`, `Half`, `SlabType`, `BlockState` são tipos do Minecraft) e assinaturas a adaptar ao kit real do projeto.

---

## 1. Convenções

- **Eixos locais**: `x` → direita (leste com rotação 0), `y` → cima, `z` → frente (sul com rotação 0). **A frente da construção é +Z.** Origem no canto de menor x e z, no nível do piso térreo; o embasamento pode ter `y < 0`.
- **Rotação no commit** (`Rotation.CLOCKWISE_90`): `(x, z) → (−z, x)`; 180°: `(−x, −z)`; `COUNTERCLOCKWISE_90`: `(z, −x)`. Com a frente em +Z, 90° horário leva a frente para oeste. Transforme a posição **e** o estado: `state.mirror(m).rotate(r)` (espelha primeiro, depois gira).
- **Direções locais** dentro das estruturas; o `Transform` converte. Nunca escreva direções do mundo numa estrutura.
- **Facing de escada**: é a direção em que a escada **sobe** (o encosto fica desse lado). Escada de telhado sobe em direção à cumeeira.
- Ordem dos passes: SKILL.md §5.

---

## 2. Contrato do kit

Implemente sob demanda: comece pelo mínimo do Run 0 e acrescente primitivas conforme os prédios pedirem. Se o projeto já tiver algo equivalente, adapte os nomes abaixo ao que existe.

| Tipo | Responsabilidade | API essencial |
|---|---|---|
| `Box` | caixa imutável (x0..x1, y0..y1, z0..z1) | `of(...)`, `expand(n)`, `contains(x,y,z)`, `width()`, `height()`, `depth()` |
| `Role` | enum de papéis da paleta | `BASE, WALL, WALL_ALT, PILLAR, TRIM, ROOF, WINDOW, FRAME, FLOOR, ACCENT, LIGHT` |
| `Blueprint` | mapa esparso local → `BlockState`, bounds e tags | `set`, `setIfAir`, `get`, `isEmpty`, `bounds()`, `tag(kind, name, box)`, `forEach` |
| `Palette` | papéis → estados ponderados, por seed e posição | `block(role,x,y,z)`, `stair(role,x,y,z,facing,half)`, `slab(role,x,y,z,type)`, `wall(role,x,y,z)`, `state("minecraft:lantern[hanging=true]")` |
| `Noise` | hash e manchas determinísticos | `rand01`, `patch01` |
| `Shapes` | primitivas de forma | lista abaixo |
| `RoofField` | telhados compostos por mapa de alturas | `gableAlongX`, `gableAlongZ`, `hip`, `dormer`, `emit` |
| `Furniture` | móveis e props com estado correto | `door`, `bed`, `table`, `chair`, `chandelier`, `fireplace`, `bookshelfWall`, `lampPost`, `flowerBox` |
| `Lighting` | cobertura de luz | `ensureCoverage(bp, pal, maxGapInterior, maxGapExterior)` |
| `Weathering` | envelhecimento por ruído | `apply(bp, pal, wear)` |
| `Transform` | origem + rotação + espelho | `pos(x,y,z)`, `state(BlockState)` |
| `Validator` | gates e checagens | `validate(bp, plan)` → `Report` |
| `Preview` | ASCII por andar, elevações, estatísticas | `plan(bp, y)`, `elevation(bp, dir)`, `stats(bp)` |
| `BuildJob` | aplicação por ticks, fix-up, undo | `tick()`, `undo()` |
| `Structure` | contrato de cada edifício | `Blueprint build(Params, Palette, long seed)` |

**Tags do Blueprint** (`kind`, enum `Tag`): `ROOM` (cômodo, para alcançabilidade e mobília), `ENTRANCE` (ponto de partida do BFS), `FLOATING_OK` (exceção ao teste de blocos flutuando), `LANDMARK` (marca registrada), `ACCENT` (acento de tema).

**`WindowStyle`** (enum usado por `Shapes.window`): `PLAIN`, `SHUTTERED` (venezianas), `ARCHED` (verga em arco), `STAINED` (vitral), `ROUND` (rosácea).

**`Shapes` (assinaturas ilustrativas):**
```
box(bp, pal, role, x0,y0,z0, x1,y1,z1)                 sólido
shell(bp, pal, role, x0,y0,z0, x1,y1,z1)               casca de 6 faces
walls(bp, pal, role, x0,z0,x1,z1, y0,y1)               4 paredes, sem piso e teto
floor(bp, pal, role, x0,z0,x1,z1, y)
cylinderWalls(bp, pal, role, cx,cz,r, y0,y1)
disc(bp, pal, role, cx,cz,r, y)
gableRoofAlongX / gableRoofAlongZ / hipRoof / shedRoof / coneRoof / dome
portal(bp, pal, face, x,y,z, width, height)             vão arqueado + moldura
window(bp, pal, face, x,y,z, w,h, style)                vidraça recuada + moldura + peitoril (+ venezianas)
archOpening(bp, pal, face, x,y,z, width, height, kind)
timberFrame(bp, pal, box, floorHeight)                  pilares, vigas por andar, diagonais
jetty(bp, pal, box, face, fromY, depth)                 andar superior avança 'depth' blocos, com consoles
beltCourse(bp, pal, box, floorHeight)                   cinta de TRIM entre andares
cornice(bp, pal, box, y)
buttress(bp, pal, x,z, face, height)
battlements(bp, pal, role, x0,z0,x1,z1, y)
balcony(...)  bayWindow(...)  dormer(...)  chimney(bp, pal, x,z, y0,y1)  turret(...)
stairsStraight(...)  stairsSpiral(bp, pal, cx,cz, y0,y1)  bridge(...)  colonnade(...)
```

---

## 3. Ruído e paleta determinísticos (código)

```java
public final class Noise {
    /** Hash 3D → double em [0,1). Mesma seed e posição, mesmo valor. */
    public static double rand01(int x, int y, int z, long seed) {
        long h = seed;
        h ^= x * 0x9E3779B97F4A7C15L;
        h  = Long.rotateLeft(h, 27) * 0xC2B2AE3D27D4EB4FL;
        h ^= y * 0x165667B19E3779F9L;
        h  = Long.rotateLeft(h, 31) * 0x9E3779B97F4A7C15L;
        h ^= z * 0xD6E8FEB86659FD93L;
        h ^= (h >>> 32);
        h *= 0xFF51AFD7ED558CCDL;
        h ^= (h >>> 33);
        return (h >>> 11) * 0x1.0p-53;
    }

    /** Valor constante por célula de 'size' blocos: gera manchas em vez de ruído branco. */
    public static double patch01(int x, int y, int z, int size, long seed) {
        return rand01(Math.floorDiv(x, size), Math.floorDiv(y, size), Math.floorDiv(z, size), seed);
    }
}
```

```java
/** Escolha ponderada determinística por papel. 75% das escolhas seguem manchas de 2×2×2; 25% têm grão fino. */
public BlockState block(Role role, int x, int y, int z) {
    List<Entry> list = entries.get(role);            // pesos normalizados (soma 1.0)
    double r = Noise.rand01(x, y, z, seed ^ 0xA5L) < 0.25
             ? Noise.rand01(x, y, z, seed ^ role.ordinal())
             : Noise.patch01(x, y, z, 2, seed ^ role.ordinal());
    double acc = 0;
    for (Entry e : list) { acc += e.weight(); if (r < acc) return e.state(); }
    return list.get(list.size() - 1).state();
}
```

Para desgaste, mantenha por papel duas listas, `clean` e `worn` (musgosa, rachada), e uma sobrecarga `block(role, x, y, z, wear)` que escolhe `worn` com probabilidade `wear` (ver 4.13). Cada entrada de escada/laje/muro precisa ser derivada da **mesma família** do bloco sorteado (o `stair()` deve devolver a escada da família escolhida naquela posição, não de outra).

---

## 4. Receitas de forma

### 4.1 Telhado de duas águas

Inclinação 1:1; cumeeira ao longo de X; frente em +Z. A fiada de trás sobe para +Z (`SOUTH`) e a da frente sobe para −Z (`NORTH`). Com a cumeeira ao longo de Z, troque por `EAST`/`WEST`.

```java
/** x0..x1 e z0..z1 = pegada das paredes; yTop = última camada de parede; overhang = beiral (0 ou 1). */
public static void gableRoofAlongX(Blueprint bp, Palette pal, Role roof, Role gable,
                                   int x0, int x1, int z0, int z1, int yTop, int overhang) {
    int xl = x0 - overhang, xr = x1 + overhang;
    int zl = z0 - overhang, zr = z1 + overhang;
    int yBase = yTop + 1 - overhang;   // com beiral, a 1ª fiada fica ao lado da última camada de parede
    int i = 0;
    for (; zl + i < zr - i; i++) {
        int y = yBase + i;
        int zBack = zl + i, zFront = zr - i;
        for (int x = xl; x <= xr; x++) {
            bp.set(x, y, zBack,  pal.stair(roof, x, y, zBack,  Direction.SOUTH, Half.BOTTOM));
            bp.set(x, y, zFront, pal.stair(roof, x, y, zFront, Direction.NORTH, Half.BOTTOM));
        }
        // empenas: preenche entre as águas, nos planos das paredes de topo (x0 e x1)
        for (int z = zBack + 1; z <= zFront - 1; z++) {
            bp.setIfAir(x0, y, z, pal.block(gable, x0, y, z));
            bp.setIfAir(x1, y, z, pal.block(gable, x1, y, z));
        }
    }
    if (zl + i == zr - i) {            // largura ímpar: cumeeira com laje no centro
        int y = yBase + i;
        for (int x = xl; x <= xr; x++)
            bp.set(x, y, zl + i, pal.slab(roof, x, y, zl + i, SlabType.BOTTOM));
    }
}
```

Detalhes que fazem a diferença:
- **Largura par**: as duas últimas fiadas ficam costas com costas e formam a cumeeira com 1 bloco de largura; **largura ímpar**: laje `bottom` no centro (ou bloco cheio, para uma cumeeira robusta).
- **Beiral** de 1 bloco: a primeira fiada fica **ao lado** da última camada de parede, para fora dela. Sob o beiral, escadas `half=top` (voltadas para fora) formam cornija.
- **Empenas** (triângulos nas paredes de topo): material `WALL` com viga de `PILLAR` seguindo a inclinação e uma janelinha redonda ou veneziana no centro. Prolongue a fiada do telhado 1 bloco além da parede (beiral lateral) para dar sombra.
- **Cumeeira**: pináculos nas pontas (`stone_brick_wall` + laje + `lightning_rod`), cata-vento no centro.
- **Corpo do telhado oco** (sótão) é o padrão; para forro liso, escadas `half=top` sob as águas.

### 4.2 Telhados compostos e de quatro águas (mapa de alturas)

Para L, T, lucarnas e quatro águas, **não** empilhe telhados separados: as escadas de uma ala acabariam dentro do sótão da outra. Trate o telhado como mapa de alturas:

```java
// 1) Cada ala escreve um mapa de camadas: layer[x,z] = yBase + d(x,z)
//      duas águas (inclinação ao longo de Z): d = min(z - zl, zr - z)
//      quatro águas:                          d = min(x - xl, xr - x, z - zl, zr - z)
// 2) Une pelo máximo. Em empate mantém a ala já escrita (o fix-up transforma vales em cantos internos).
// 3) Emite por coluna: escada com facing para a cumeeira (oposto à borda mais próxima);
//    laje bottom onde a coluna é equidistante de duas bordas opostas (cumeeira em largura ímpar).
// 4) Empenas: no plano da parede de topo de cada ala, preenche de yTop+1 até layer-1 com WALL.
```

- Quatro águas: nos cantos a coluna é equidistante de duas bordas adjacentes; use o `facing` do eixo Z e deixe o **fix-up** (`Block.updateFromNeighbourShapes`) criar os cantos externos (`outer_left/right`) e internos (`inner_left/right`).
- Lucarna: mini-ala de ≈3×3 (parede de 1×2 com janela) cuja água cruza a principal; entra na união por máximo, com cumeeira mais baixa que a da água principal.
- Alas de larguras diferentes: a cumeeira da ala mais estreita fica mais baixa; a união por máximo resolve o encontro sem escadas “dentro” do outro telhado.

### 4.3 Telhado cônico e agulhas

Perfil de raio por camada, com queda de no máximo 1 por camada (senão abre um vão entre anéis). A camada que fecha um degrau de raio usa **escada**; as demais usam bloco cheio.

```java
public static void coneRoof(Blueprint bp, Palette pal, Role roof,
                            int cx, int cz, int yBase, int rBase, int height, double curve) {
    int n = height + rBase + 2;                                      // folga para o raio chegar a 0
    int[] r = new int[n];
    for (int l = 0; l < n; l++) {
        double t = Math.min(1.0, l / (double) height);
        r[l] = (int) Math.round(rBase * Math.pow(1.0 - t, curve));   // curve 1 = reto; 1.6–2.2 = côncavo ("chapéu de bruxo")
        if (l > 0) r[l] = Math.max(r[l], r[l - 1] - 1);              // nunca cai mais de 1 por camada
    }
    for (int l = 0; l < n - 1; l++) {
        int y = yBase + l;
        if (r[l] < 1) {                                              // fechou o cone: tampa central e fim
            bp.set(cx, y, cz, pal.block(roof, cx, y, cz));
            break;
        }
        boolean step = r[l + 1] < r[l];                              // fecha um degrau de raio: escada
        for (int[] c : ringCells(cx, cz, r[l])) {
            bp.set(c[0], y, c[1], step
                ? pal.stair(roof, c[0], y, c[1], inward(c[0] - cx, c[1] - cz), Half.BOTTOM)
                : pal.block(roof, c[0], y, c[1]));
        }
    }
    // A agulha (lightning_rod, end_rod, lantern) sobre a tampa central é do passe 5.
}

/** Anel de espessura 1 em torno de (cx, cz): células com distância em (r - 0.5, r + 0.5]. */
static List<int[]> ringCells(int cx, int cz, int r) {
    List<int[]> cells = new ArrayList<>();
    double outer = r + 0.5, inner = r - 0.5;
    for (int dx = -r; dx <= r; dx++)
        for (int dz = -r; dz <= r; dz++) {
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d <= outer && d > inner) cells.add(new int[]{cx + dx, cz + dz});
        }
    return cells;
}

/** Direção em que a escada sobe: para o centro, pelo eixo dominante. Diagonais viram cantos via fix-up. */
static Direction inward(int dx, int dz) {
    return Math.abs(dx) >= Math.abs(dz)
        ? (dx > 0 ? Direction.WEST : Direction.EAST)
        : (dz > 0 ? Direction.NORTH : Direction.SOUTH);
}
```

- O anel por distância é conectado em 8 vizinhanças; para uma casca mais “fechada” aumente a espessura (`inner = r - 1`).
- Agulha: `lightning_rod` (cobre) ou coluna de `end_rod`/`stone_brick_wall` + `lantern` no topo, com 3 a 6 blocos acima do cone.
- Torreões menores (raio 2 a 3): `curve` entre 1,0 e 1,4 e altura de 2 a 3 vezes o raio.

### 4.4 Cúpulas

- Hemisfério: `r(l) = round(sqrt(R² − l²))` para `l = 0..R`; achatada: `r(l) = round(R · sqrt(1 − (l/H)²))` com `H ≈ 0,6 R`. Casca de espessura 1 (anéis por `ringCells`), topo fechado com bloco ou laje e remate de agulha.
- Onde o raio cai 2 ou mais de uma camada para a outra (perto do topo), complete o degrau com `slab[type=bottom]` no anel externo para suavizar.
- Nervuras: a cada 45° (8 meridianos) troque o bloco do anel por `TRIM`/`PILLAR`; base da cúpula com tambor cilíndrico (4 a 6 de altura) com janelas altas entre pilastras.
- Claraboia: 1 em cada 2 setores do tambor com vidro (`glass`/`stained_glass`).

### 4.5 Portais, arcos e janelas

- **Portal arqueado 3×4 (padrão)**: vão de 3 de largura por 4 de altura. Nas duas células laterais da fileira de cima, coloque `stairs[half=top]` com `facing` apontando **para fora do vão**: o canto fica arredondado. Moldura: coluna de `PILLAR` de cada lado e, acima, uma arquivolta (fileira de escadas invertidas 1 bloco mais larga). Soleira: laje ou 2 a 3 degraus.
- **Portal ogival 5×6 (entrada nobre)**: vão de 5 de largura até a fileira 3; fileira 4 com 3 de largura livre e escadas invertidas nas pontas (para fora); fileira 5 com 1 de largura livre e escadas invertidas nos lados (para fora). Acima, rosácea ou brasão.
- **Janela alta 1×3 ou 2×3**: vidraça (`glass_pane`) em toda a altura; peitoril com `slab[type=top]` ou escada projetando 1 bloco; verga com `stairs[half=top]`; caixilhos com `iron_bars` ou alçapões em cruz nas áreas nobres; **venezianas** = alçapões abertos com `facing` para fora da parede, nas duas laterais.
- **Janela redonda ou rosácea**: círculo de raio 2 a 3 por `ringCells`; vidro colorido em fatias (cores alternadas por ângulo) e raios de `iron_bars` ou `PILLAR`.
- **Ritmo**: pilastras a cada 4 a 6 blocos e uma janela entre cada par; simetria em torno do eixo central da fachada; o que sobrar vai para os extremos.
- **Profundidade**: a vidraça fica **recuada 1 bloco** dentro do vão e a moldura avança; nunca vidro no mesmo plano da parede.

### 4.6 Torres

- **Quadrada** (N ímpar: 7, 9, 11): pilastras salientes nos cantos (+1), cintas a cada 5 a 6 blocos, janelas estreitas 1×2 alternando os lados, remate com consoles e ameias **ou** telhado piramidal ou cônico; torreões menores nos cantos.
- **Redonda** (raio 3 a 6): paredes por `cylinderWalls`; janelas em 4 ou 8 direções; reduza o raio em 1 a cada ~12 blocos, marcando com uma cinta; balcão em anel com lajes e guarda-corpo; telhado cônico (4.3).
- **Octogonal**: quadrado N×N com os cantos chanfrados (corte de 2×2 em cada canto para N=9, 3×3 para N=11); mesmos detalhes.
- **Torreão de escada**: cilindro ou quadrado anexado à fachada, mais alto que o telhado, com cone próprio.
- **Proporção**: altura de 3 vezes o lado ou mais para torres-marco; 1,5 a 2 vezes para torreões.

### 4.7 Contrafortes, cintas e cornijas

- **Contraforte**: 1 bloco saliente em cada divisão de baia, do embasamento até 2/3 da altura, afunilando com escadas voltadas **para a parede** (o capitel inclina para fora) e terminando em pináculo (parede + laje + `lightning_rod` ou `end_rod`).
- **Cinta** (belt course): fileira horizontal de `TRIM` (lajes `top` ou escadas invertidas) entre andares, avançando de meio bloco a 1.
- **Cornija**: escadas `half=top` sob o beiral (voltadas para fora) com laje; no topo de torres, consoles de escadas invertidas alternadas com blocos (machicolations).
- **Embasamento**: 1 a 2 fiadas avançando 1 bloco, em material mais escuro e pesado.

### 4.8 Ameias, muralhas e portões

- **Ameias**: no topo do muro, alternar merlão (bloco ou muro de 1) e vão a cada 1 bloco (período 2); cantos sempre com merlão; em torres, merlão de 2 de largura nos cantos.
- **Muralha**: espessura 3 a 4 com batente (base 1 bloco mais larga nas 2 a 3 primeiras fiadas, em escadas), altura 10 a 12, caminho de ronda de 2 a 3 de largura com parapeito externo de 1 bloco e ameias, torres a cada 14 a 20 blocos (5×5 ou mais, 3 a 4 acima do muro) com escada de acesso. Para seguir o terreno, use degraus de 1 bloco (suavizados) ou segmentos de altura constante entre torres. Musgo e hera na base.
- **Portão**: arco central de 5×7 ou maior, grade de `iron_bars` + `chain`, torres gêmeas, ponte ou escadaria, brasão acima.

### 4.9 Sacadas, janelas salientes, lucarnas e chaminés

- **Sacada**: laje de 1 a 2 de profundidade, consoles de escadas invertidas por baixo, guarda-corpo de `iron_bars` ou cerca, porta-janela e lanterna.
- **Janela saliente (erker)**: 3 de largura projetando 1 a 2 blocos, cantos chanfrados com escadas, mini-telhado de quatro águas.
- **Lucarna**: ver 4.2.
- **Chaminé**: coluna de 2×2 ou 1×1 de pedra (`stone_bricks` ou `bricks`) do chão da lareira até 2 a 3 blocos acima da cumeeira; remate com `stone_brick_slab` sobre pilares de `stone_brick_wall` nos cantos. Alinhe com a lareira do térreo.

### 4.10 Escadas

- **Reta**: largura 2 a 3, 1 degrau = 1 bloco de subida; patamar a cada 8 a 10 degraus; guarda-corpo; vão no piso de cima com 4 a 5 blocos de comprimento (andar de 5).
- **Monumental** (fachada): largura 5 a 9, degraus de `stairs` alternando com `slab`, mureta lateral com pináculos e lanternas, patamar antes do portal.
- **Espiral**: anel de 8 células (perímetro de 3×3) em volta de um pilar central 1×1. Ordem horária: `(−1,−1) (0,−1) (1,−1) (1,0) (1,1) (0,1) (−1,1) (−1,0)`; cada célula sobe 1 bloco e a escada tem `facing` na direção do próximo passo (nos cantos, a da nova direção). Vão livre de 3 blocos acima de cada degrau. Com andar de 5 e anel de 8, o ponto de chegada gira 5/8 de volta por andar: a abertura do andar `n` fica na célula de índice `(n × 5) mod 8`.

### 4.11 Pontes e galerias

- **Ponte de pedra**: tabuleiro de 3 a 5 de largura; arcos por baixo (vão de 5 a 7, circulares com escadas invertidas e lajes) e pilares; leve elevação central (1 a 2 blocos) com escadas; guarda-corpo em `stone_brick_wall` ou `iron_bars`; lanternas a cada 4 a 5 blocos.
- **Ponte coberta**: igual, com colunas de madeira e telhado leve de duas águas; ótima entre torres.
- **Galeria (claustro)**: colunas a cada 4 blocos (`PILLAR` com capitel em `chiseled_stone_bricks`), arcos entre colunas, telhado de uma água encostado na parede, piso de pedra com faixa central.

### 4.12 Terreno e região

- **`groundY(x, z)`**: topo do solo ignorando folhas, troncos e plantas (código em `blockstates-e-neoforge-1-21-1.md` §6).
- **Plataforma (pad)**: altura-alvo = percentil 60 das alturas sob a pegada; corta o que passa e aterra o que falta; talude de 4 a 8 blocos interpolando (smoothstep) até o terreno natural, com ruído de ±1; superfície com o bloco de topo do bioma, `dirt` embaixo, pedra mais fundo.
- **Remoção de árvores**: flood fill de troncos e folhas conectados que invadem a pegada; remova a árvore inteira, nunca meia copa.
- **Caminhos**: spline (Catmull-Rom) pelos pontos de controle, largura = base + ruído de ±1, `y = groundY`; o `dirt_path` ocupa o lugar do bloco de grama do topo do solo; misture `dirt_path` 50, cascalho 20, pedregulho 20, terra grossa 10; desníveis acima de 1 viram degraus de laje ou escada.
- **Praças**: círculo ou oval com padrão (anéis, rosa dos ventos, brasão) em 2 a 3 materiais; borda com meio-fio de lajes; postes a cada 8 a 10 blocos.
- **Água**: contorno por elipse com raio modulado por ruído; profundidade 2 a 3; borda de areia, argila ou pedra; bacia **fechada** (laterais e fundo sólidos); lírios e juncos; ponte arqueada se atravessar.
- **Vegetação**: Poisson disk (distância mínima: árvores 4 a 6, arbustos 2 a 3, flores 1) modulado por ruído de densidade; afaste 2 blocos de caminhos e paredes; espécies misturadas; sub-bosque (samambaia, flores, musgo). **Reuse features vanilla** (`TreeFeatures.CHERRY`, `TreeFeatures.OAK`, `TreeFeatures.BIRCH`, `TreeFeatures.AZALEA_TREE`) com um `RandomSource` semeado; confira os nomes no código do 1.21.1.
- **Trepadeiras**: `vine` em 10% a 20% das colunas de parede externa elegíveis, comprimento de 2 a 7 por ruído, só sobre face sólida.

### 4.13 Envelhecimento

`Weathering.apply(bp, pal, wear)`:
- Probabilidade de usar a variante gasta de um papel (musgosa ou rachada) = `wear × f`, com `f = 1` até 2 blocos acima do solo, caindo para 0,2 no alto; multiplique por 1,5 em cantos externos e junto à água. Use `Noise.patch01` para formar manchas, não pontos soltos.
- Prédios novos e nobres: `wear` de 0,05 a 0,12. Muralha: 0,25 a 0,35. Ruínas: 0,5 ou mais, removendo partes (colapsos) em manchas por ruído.
- Trepadeiras e musgo no topo de muros; poucas folhas nos beirais.
