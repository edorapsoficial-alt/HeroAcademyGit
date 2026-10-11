# Blockstates e NeoForge 1.21.1

## Índice
1. Versão e APIs que mudaram
2. Flags de `setBlock`
3. Armadilhas de BlockState
4. Fix-up de estados (código)
5. Aplicação por ticks: BuildJob (código)
6. Altura do solo (código)
7. BlockEntities e entidades
8. Desfazer e exportar NBT
9. Desempenho e luz
10. Comandos (esqueleto)
11. Tabuleiro de teste de orientação

Os trechos são ilustrativos e foram pensados para o mapeamento oficial da Mojang no 1.21.1. Compile e ajuste ao que o projeto aceitar.

---

## 1. Versão e APIs que mudaram

- Java 21, mapeamento oficial da Mojang, NeoForge 21.1.x (confirme em `gradle.properties`).
- `ResourceLocation` não tem construtor público: use `ResourceLocation.fromNamespaceAndPath("minecraft", "stone_bricks")` ou `ResourceLocation.parse("minecraft:stone_bricks")`.
- `BuiltInRegistries.BLOCK.get(id)` devolve `air` quando o ID não existe (não lança exceção). Use `getOptional(id)` e falhe de forma explícita, ou parseie com `BlockStateParser`:

```java
static BlockState parse(String id) {
    try {
        return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), id, false).blockState();
    } catch (CommandSyntaxException e) {
        throw new IllegalArgumentException("Bloco inválido para 1.21.1: " + id, e);
    }
}
```

- Altura do mundo: `level.getMinBuildHeight()` e `level.getMaxBuildHeight()` (Overworld: −64 e 320, exclusivo). Recuse posições fora com `level.isOutsideBuildHeight(pos)`.
- Eventos (registre em `NeoForge.EVENT_BUS`): `ServerTickEvent.Post` (`net.neoforged.neoforge.event.tick`) e `RegisterCommandsEvent` (`net.neoforged.neoforge.event`).
- Dados de itens, estandartes e placas usam *data components* (1.20.5 em diante), não NBT livre; tabelas de loot usam `ResourceKey<LootTable>`.
- Blocos de versões posteriores não existem no 1.21.1 (lista em `mc-planejamento-construcao/references/paletas-de-blocos.md` §5).

---

## 2. Flags de `setBlock`

`level.setBlock(pos, state, flags)`:

| Flag | Valor | Efeito |
|---|---|---|
| `UPDATE_NEIGHBORS` | 1 | notifica os vizinhos (`neighborChanged`) |
| `UPDATE_CLIENTS` | 2 | envia a mudança aos clientes |
| `UPDATE_INVISIBLE` | 4 | impede a renderização no cliente |
| `UPDATE_IMMEDIATE` | 8 | renderiza imediatamente |
| `UPDATE_KNOWN_SHAPE` | 16 | pula o `updateShape` dos vizinhos (você garante que o estado já está certo) |
| `UPDATE_SUPPRESS_DROPS` | 32 | não solta itens ao substituir |
| `UPDATE_MOVE_BY_PISTON` | 64 | marca movimento por pistão |
| `UPDATE_ALL` | 3 | = 1 + 2 |

Recomendação: use **`UPDATE_CLIENTS` (2)** na colocação em massa e corrija conexões no fix-up. `UPDATE_ALL` (3) dispara cascatas de atualização de vizinhos, derruba blocos sem suporte e causa lag. Combine com `UPDATE_KNOWN_SHAPE` (2 + 16 = 18) quando os estados já estiverem completos.

---

## 3. Armadilhas de BlockState

| Bloco | Propriedades | Como acertar |
|---|---|---|
| `*_stairs` | `facing`, `half`, `shape`, `waterlogged` | `facing` = direção em que a escada **sobe** (o encosto fica desse lado); `half=top` inverte; `shape` (`straight`, `inner_*`, `outer_*`) deixe o fix-up calcular |
| `*_slab` | `type`, `waterlogged` | `bottom`, `top` ou `double` |
| `*_door` | `half`, `facing`, `hinge`, `open`, `powered` | **duas células**: `half=lower` e `half=upper` com as mesmas demais propriedades; sem a metade de cima a porta quebra |
| `*_bed` | `part`, `facing`, `occupied` | `part=foot` na célula-base e `part=head` na célula `foot + facing`; `facing` aponta do pé para a cabeceira; `occupied=false` |
| plantas altas (`tall_grass`, `large_fern`, `lilac`, `rose_bush`, `peony`, `sunflower`) | `half` | `lower` e `upper` |
| `*_trapdoor` | `facing`, `half`, `open`, `powered` | aberto: o painel fica no lado **oposto** a `facing`; venezianas coladas na parede usam `facing` apontando para fora da parede |
| `*_fence`, `*_wall`, `glass_pane`, `iron_bars` | conexões `north/east/south/west` (no muro: `up` e `none/low/tall`) | rode o fix-up |
| `*_fence_gate` | `facing`, `open`, `in_wall`, `powered` | `in_wall` sai do fix-up |
| `*_leaves` | `persistent`, `distance` | `persistent=true`, senão decaem sem tronco por perto |
| `lantern`, `soul_lantern` | `hanging`, `waterlogged` | `hanging=true` pendurada, `false` apoiada |
| `chain` | `axis`, `waterlogged` | `axis=y` para pendurar |
| troncos e pilares (`*_log`, `*_wood`, `quartz_pillar`, `purpur_pillar`, `hay_block`) | `axis` | `x`, `y` ou `z`; vigas horizontais precisam do eixo certo |
| `candle` | `candles`, `lit`, `waterlogged` | `candles` de 1 a 4; `lit=true` permanece aceso |
| `campfire` | `lit`, `facing`, `signal_fire`, `waterlogged` | `lit=true`, `signal_fire=false` |
| `chest` | `facing`, `type`, `waterlogged` | `type` = `single`, `left` ou `right`; baú duplo = duas células com `left`/`right` e o mesmo `facing` |
| `barrel` | `facing`, `open` | `facing` pode ser qualquer das 6 direções |
| `wall_torch` | `facing` | aponta para **longe** da parede de apoio |
| `vine` | `north`, `east`, `south`, `west`, `up` | booleanos; precisa de face sólida atrás |
| `glow_lichen` | `north`, `east`, `south`, `west`, `up`, `down` | booleanos por face |
| `bell` | `attachment`, `facing` | `floor`, `ceiling`, `single_wall` ou `double_wall` |
| `*_banner`, `*_wall_banner` | `rotation` (0 a 15) / `facing` | lisos funcionam sem BlockEntity configurado; padrões exigem dados |
| `chiseled_bookshelf` | `slot_N_occupied` | depende do inventário do BlockEntity; prefira `bookshelf` |
| `light` | `level`, `waterlogged` | luz invisível; só quando não houver como esconder uma fonte real |
| `water`, `lava` | — | sempre em bacia fechada: escorrem quando atualizados; para formas, use `waterlogged=true` em lajes e escadas |
| `sand`, `red_sand`, `gravel`, `*_concrete_powder`, `anvil` | — | só com suporte sólido embaixo (caem ao receber o primeiro tick) |

---

## 4. Fix-up de estados (código)

Com `UPDATE_CLIENTS`, o estado fica exatamente como foi escrito: escadas não formam cantos e cercas, muros e vidraças não se conectam. Depois de colocar tudo, recalcule os estados dependentes de vizinhança:

```java
static boolean needsShapeFix(BlockState s) {
    Block b = s.getBlock();
    return b instanceof StairBlock || b instanceof FenceBlock || b instanceof WallBlock
        || b instanceof IronBarsBlock          // inclui vidraças
        || b instanceof FenceGateBlock;
}

static void fixup(ServerLevel level, Iterable<BlockPos> positions) {
    for (BlockPos pos : positions) {
        BlockState cur = level.getBlockState(pos);
        if (!needsShapeFix(cur)) continue;
        BlockState upd = Block.updateFromNeighbourShapes(cur, level, pos);
        if (upd != cur) level.setBlock(pos, upd, Block.UPDATE_CLIENTS);
    }
}
```

Uma passada basta: a forma de uma escada depende só de `facing` e `half` dos vizinhos, não da forma deles. É o fix-up que transforma os cantos de um telhado de quatro águas em `outer_*` e os vales em `inner_*`.

---

## 5. Aplicação por ticks: BuildJob (código)

```java
public record Placement(BlockPos pos, BlockState state) {}

public final class BuildJob {
    private static final long BUDGET_NANOS = 8_000_000L;          // ≈8 ms por tick

    private final ServerLevel level;
    private final List<Placement> queue;                          // já transformada para o mundo
    private final List<BlockPos> fixups = new ArrayList<>();      // escadas, cercas, muros, vidraças, grades, portões
    private int cursor, fixCursor;

    public BuildJob(ServerLevel level, List<Placement> placements) {
        this.level = level;
        this.queue = new ArrayList<>(placements);
        // chunk → dependentes de suporte por último → Y crescente
        queue.sort(Comparator
            .comparingLong((Placement p) -> ChunkPos.asLong(p.pos()))
            .thenComparingInt(p -> needsSupport(p.state()) ? 1 : 0)
            .thenComparingInt(p -> p.pos().getY()));
        for (Placement p : queue) if (needsShapeFix(p.state())) fixups.add(p.pos());
    }

    /** Chame uma vez por tick (ServerTickEvent.Post). Devolve true quando terminou. */
    public boolean tick() {
        long end = System.nanoTime() + BUDGET_NANOS;
        while (cursor < queue.size()) {
            Placement p = queue.get(cursor++);
            level.setBlock(p.pos(), p.state(), Block.UPDATE_CLIENTS);
            if ((cursor & 63) == 0 && System.nanoTime() > end) return false;
        }
        while (fixCursor < fixups.size()) {
            BlockPos pos = fixups.get(fixCursor++);
            BlockState cur = level.getBlockState(pos);
            BlockState upd = Block.updateFromNeighbourShapes(cur, level, pos);
            if (upd != cur) level.setBlock(pos, upd, Block.UPDATE_CLIENTS);
            if ((fixCursor & 63) == 0 && System.nanoTime() > end) return false;
        }
        return true;
    }
}
```

- `needsSupport(state)` classifica o que depende de apoio (lanternas, tochas, trepadeiras, tapetes, placas, botões, velas, flores): use tags (`BlockTags.FLOWERS`, `BlockTags.WOOL_CARPETS`, `BlockTags.BUTTONS`...) ou `instanceof`.
- Posições no `Placement` devem ser imutáveis (`pos.immutable()` se vierem de um `MutableBlockPos`).
- Chunks não carregados são carregados de forma síncrona no primeiro `setBlock`. Em regiões grandes, peça os chunks com antecedência (um por tick) para não travar o tick.
- Mantenha uma lista de jobs ativos e chame `tick()` de cada um em `ServerTickEvent.Post`; ao terminar, avise quem rodou o comando (blocos, tempo, bounding box).

---

## 6. Altura do solo (código)

Medir o solo sobre folhagem faz o prédio flutuar sobre a copa das árvores. Desça ignorando folhas, troncos e plantas:

```java
static int groundY(ServerLevel level, int x, int z) {
    BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(
        x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
    while (p.getY() > level.getMinBuildHeight()) {
        BlockState s = level.getBlockState(p);
        boolean foliage = s.isAir() || s.is(BlockTags.LEAVES) || s.is(BlockTags.LOGS)
                       || s.is(BlockTags.REPLACEABLE) || s.is(BlockTags.FLOWERS);
        if (!foliage) return p.getY();       // se for água, decida: aterrar, ponte ou ilha
        p.move(Direction.DOWN);
    }
    return level.getMinBuildHeight();
}
```

Meça a pegada inteira (todas as colunas) e use um percentil (o 60 funciona bem) como altura do pad. Troncos de construções de jogadores também são ignorados por `LOGS`: confira a área antes de terraplanar.

---

## 7. BlockEntities e entidades

- Baús com conteúdo, placas, estandartes com padrões, cabeças e potes decorados são BlockEntities: coloque o bloco com `setBlock` e depois configure o BlockEntity (`level.getBlockEntity(pos)`). Prefira não depender disso no kit básico; para padrões complexos, exporte um template NBT feito no jogo.
- Quadros, pinturas e suportes de armadura são **entidades**: crie-os depois dos blocos, com uma tag de job (para removê-los ao reconstruir) e sem duplicar em re-execuções.
- Substituir um bloco com inventário solta os itens. Limpe antes com `Clearable.tryClear(level.getBlockEntity(pos))`.

---

## 8. Desfazer e exportar NBT

Capture a pegada **antes** de aplicar:

```java
StructureTemplateManager mgr = level.getStructureManager();
ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, "undo/" + jobId);
StructureTemplate snap = mgr.getOrCreate(id);
snap.fillFromWorld(level, origin, size, false, Blocks.STRUCTURE_VOID);   // captura blocos e BlockEntities
mgr.save(id);                                                             // grava em generated/<modid>/structures/undo/<jobId>.nbt

// desfazer:
snap.placeInWorld(level, origin, origin, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_CLIENTS);
```

Exporte o resultado final do mesmo jeito (depois da obra) para ter um `.nbt` reutilizável com `/place template`. Confira as assinaturas no código do projeto.

---

## 9. Desempenho e luz

- Orçamento de tempo por tick (≈8 ms), não contagem fixa de blocos; cheque o relógio a cada ~64 blocos.
- Ordem por chunk e Y crescente reduz recargas de chunk e recálculo de luz.
- Evite `UPDATE_ALL` na colocação em massa, evite líquidos fora de bacia e evite redstone.
- Regiões acima de ≈2 milhões de blocos: divida em jobs menores.
- Luz: cada `setBlock` agenda atualização do motor de luz; terminar um prédio por vez e mover para o próximo mantém o custo previsível.
- No 1.21.1, monstros comuns só nascem com luz de bloco 0; garanta emissores (G9) para nenhum piso caminhável ficar em 0.

---

## 10. Comandos (esqueleto)

```java
@SubscribeEvent
public static void onRegisterCommands(RegisterCommandsEvent e) {
    e.getDispatcher().register(
        Commands.literal("campus").requires(src -> src.hasPermission(2))
            .then(Commands.literal("dryrun").then(Commands.argument("id", StringArgumentType.word())
                .executes(ctx -> dryRun(ctx.getSource(), StringArgumentType.getString(ctx, "id")))))
            .then(Commands.literal("build")      /* id, rotação, seed, confirm */ )
            .then(Commands.literal("undo").executes(ctx -> undo(ctx.getSource())))
            .then(Commands.literal("test").then(Commands.literal("orientation")
                .executes(ctx -> orientationBoard(ctx.getSource()))))
    );
}
```

`dryrun` constrói o Blueprint, roda o `Validator` e imprime o `Preview` (planta por andar, elevações, estatísticas) sem tocar o mundo. `build` exige `confirm` quando a área tiver construções não naturais.

---

## 11. Tabuleiro de teste de orientação

Sempre que criar ou alterar os helpers de orientação (escadas, portas, alçapões, camas, baús, lanternas, trepadeiras, portões), gere um tabuleiro e confira em jogo:

- Escadas nas 4 direções, `half=bottom` e `half=top`, em fileiras, com um canto interno e um externo.
- Portas com `hinge` esquerda e direita, abertas e fechadas, nas 4 direções.
- Alçapões fechados, abertos e `half=top`, nas 4 direções.
- Camas, baús (simples e duplos), barris, lanternas (`hanging` verdadeiro e falso), trepadeiras, portões de cerca (em muro e livres) e um sino.
- Repita o tabuleiro com rotação 90°, 180°, 270° e com espelho.

Compare com a intenção declarada no código (“escada sobe para a cumeeira”, “porta abre para dentro”). Qualquer divergência é bug do `Transform` ou dos helpers, não do prédio.
