package com.heroacademy.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Utilitário de alto desempenho para geração procedural de estruturas.
 * Utiliza setBlock com flags 18 (2 | 16: sync para clientes, sem atualizações vizinhas em cascata).
 */
public class BlockKit {
    private final ServerLevel level;
    private static final int FLAGS = 18;

    public BlockKit(ServerLevel level) {
        this.level = level;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public void set(int x, int y, int z, BlockState state) {
        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) return;
        level.setBlock(new BlockPos(x, y, z), state, FLAGS);
    }

    public void set(int x, int y, int z, Block block) {
        set(x, y, z, block.defaultBlockState());
    }

    public void set(BlockPos pos, BlockState state) {
        set(pos.getX(), pos.getY(), pos.getZ(), state);
    }

    public void set(BlockPos pos, Block block) {
        set(pos.getX(), pos.getY(), pos.getZ(), block.defaultBlockState());
    }

    public void setIfAir(int x, int y, int z, BlockState state) {
        if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) return;
        BlockPos pos = new BlockPos(x, y, z);
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, state, FLAGS);
        }
    }

    public void setIfAir(int x, int y, int z, Block block) {
        setIfAir(x, y, z, block.defaultBlockState());
    }

    public void fill(int x1, int y1, int z1, int x2, int y2, int z2, BlockState state) {
        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minY = Math.max(level.getMinBuildHeight(), Math.min(y1, y2));
        int maxY = Math.min(level.getMaxBuildHeight() - 1, Math.max(y1, y2));
        int minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos.set(x, y, z);
                    level.setBlock(pos, state, FLAGS);
                }
            }
        }
    }

    public void fill(int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
        fill(x1, y1, z1, x2, y2, z2, block.defaultBlockState());
    }

    public void clear(int x1, int y1, int z1, int x2, int y2, int z2) {
        fill(x1, y1, z1, x2, y2, z2, Blocks.AIR.defaultBlockState());
    }

    public void walls(int x1, int y1, int z1, int x2, int y2, int z2, BlockState state) {
        int minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);
        fill(minX, y1, minZ, maxX, y2, minZ, state);
        fill(minX, y1, maxZ, maxX, y2, maxZ, state);
        fill(minX, y1, minZ, minX, y2, maxZ, state);
        fill(maxX, y1, minZ, maxX, y2, maxZ, state);
    }

    public void walls(int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
        walls(x1, y1, z1, x2, y2, z2, block.defaultBlockState());
    }

    public void pillar(int x, int y1, int y2, int z, BlockState state) {
        fill(x, y1, z, x, y2, z, state);
    }

    public void pillar(int x, int y1, int y2, int z, Block block) {
        fill(x, y1, z, x, y2, z, block.defaultBlockState());
    }

    public void disc(int cx, int cy, int cz, int radius, BlockState state) {
        int r2 = radius * radius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= r2) {
                    pos.set(cx + dx, cy, cz + dz);
                    level.setBlock(pos, state, FLAGS);
                }
            }
        }
    }

    public void disc(int cx, int cy, int cz, int radius, Block block) {
        disc(cx, cy, cz, radius, block.defaultBlockState());
    }

    public void ring(int cx, int cy, int cz, int rMin, int rMax, BlockState state) {
        int rMin2 = rMin * rMin;
        int rMax2 = rMax * rMax;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -rMax; dx <= rMax; dx++) {
            for (int dz = -rMax; dz <= rMax; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 >= rMin2 && d2 <= rMax2) {
                    pos.set(cx + dx, cy, cz + dz);
                    level.setBlock(pos, state, FLAGS);
                }
            }
        }
    }

    public void ring(int cx, int cy, int cz, int rMin, int rMax, Block block) {
        ring(cx, cy, cz, rMin, rMax, block.defaultBlockState());
    }

    public BlockState stair(Block block, Direction facing, Half half) {
        BlockState state = block.defaultBlockState();
        if (state.hasProperty(StairBlock.FACING)) state = state.setValue(StairBlock.FACING, facing);
        if (state.hasProperty(StairBlock.HALF)) state = state.setValue(StairBlock.HALF, half);
        return state;
    }

    public BlockState stair(Block block, Direction facing) {
        return stair(block, facing, Half.BOTTOM);
    }

    public BlockState slab(Block block, SlabType type) {
        BlockState state = block.defaultBlockState();
        if (state.hasProperty(SlabBlock.TYPE)) state = state.setValue(SlabBlock.TYPE, type);
        return state;
    }

    public BlockState slab(Block block) {
        return slab(block, SlabType.BOTTOM);
    }

    /** Telhado de duas águas com inclinação no eixo Z (cume ao longo de X). */
    public void gableAlongX(int minX, int maxX, int minZ, int maxZ, int startY, Block stairBlock, Block ridgeBlock, Block fillBlock) {
        int zN = minZ;
        int zS = maxZ;
        int y = startY;

        while (zN < zS) {
            // Beiral / escadas norte subindo para o sul
            fill(minX, y, zN, maxX, y, zN, stair(stairBlock, Direction.SOUTH));
            // Beiral / escadas sul subindo para o norte
            fill(minX, y, zS, maxX, y, zS, stair(stairBlock, Direction.NORTH));
            // Preenchimento interno
            if (zN + 1 <= zS - 1 && fillBlock != null) {
                fill(minX, y, zN + 1, maxX, y, zS - 1, fillBlock.defaultBlockState());
            }
            zN++;
            zS--;
            y++;
        }

        if (zN == zS) {
            fill(minX, y, zN, maxX, y, zS, ridgeBlock != null ? ridgeBlock.defaultBlockState() : stairBlock.defaultBlockState());
        }
    }

    /** Telhado de quatro águas em pirâmide/hip roof. */
    public void hipRoof(int minX, int minZ, int maxX, int maxZ, int startY, Block stairBlock, Block fillBlock, Block capBlock) {
        int x1 = minX, x2 = maxX;
        int z1 = minZ, z2 = maxZ;
        int y = startY;

        while (x1 < x2 && z1 < z2) {
            // Escadas nas quatro bordas
            fill(x1, y, z1, x2, y, z1, stair(stairBlock, Direction.SOUTH));
            fill(x1, y, z2, x2, y, z2, stair(stairBlock, Direction.NORTH));
            fill(x1, y, z1, x1, y, z2, stair(stairBlock, Direction.EAST));
            fill(x2, y, z1, x2, y, z2, stair(stairBlock, Direction.WEST));

            // Interior
            if (x1 + 1 <= x2 - 1 && z1 + 1 <= z2 - 1 && fillBlock != null) {
                fill(x1 + 1, y, z1 + 1, x2 - 1, y, z2 - 1, fillBlock.defaultBlockState());
            }

            x1++; x2--;
            z1++; z2--;
            y++;
        }

        if (x1 <= x2 && z1 <= z2 && capBlock != null) {
            fill(x1, y, z1, x2, y, z2, capBlock.defaultBlockState());
        }
    }

    /** Abóbada cilíndrica de vidro/pedra ao longo do eixo X. */
    public void vaultAlongX(int minX, int maxX, int minZ, int maxZ, int startY, Block block) {
        int width = maxZ - minZ;
        double radius = width / 2.0;
        double cz = (minZ + maxZ) / 2.0;

        for (int z = minZ; z <= maxZ; z++) {
            double dz = z - cz;
            double h = Math.sqrt(Math.max(0.0, radius * radius - dz * dz));
            int y = startY + (int) Math.round(h);
            fill(minX, y, z, maxX, y, z, block.defaultBlockState());
        }
    }

    /** Elipsoide preenchido. */
    public void ellipsoid(int cx, int cy, int cz, int rx, int ry, int rz, BlockState state) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    double norm = (dx * dx) / (double)(rx * rx) + (dy * dy) / (double)(ry * ry) + (dz * dz) / (double)(rz * rz);
                    if (norm <= 1.0) {
                        pos.set(cx + dx, cy + dy, cz + dz);
                        level.setBlock(pos, state, FLAGS);
                    }
                }
            }
        }
    }

    /** Cama completa (pé e cabeceira com orientação correta). */
    public void bed(int footX, int y, int footZ, Direction facing, Block bedBlock) {
        BlockState footState = bedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, BedPart.FOOT);
        BlockState headState = bedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, BedPart.HEAD);

        set(footX, y, footZ, footState);
        set(footX + facing.getStepX(), y, footZ + facing.getStepZ(), headState);
    }

    /** Poste de iluminação imperial elegante. */
    public void lampPost(int x, int y, int z) {
        pillar(x, y, y + 2, z, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        set(x, y + 3, z, Blocks.SEA_LANTERN);
        set(x, y + 4, z, slab(Blocks.POLISHED_BLACKSTONE_SLAB));
    }

    /** Lustre suspenso com correntes e lanterna. */
    public void chandelier(int x, int startY, int length, int z) {
        for (int i = 0; i < length; i++) {
            set(x, startY - i, z, Blocks.CHAIN);
        }
        BlockState lantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        set(x, startY - length, z, lantern);
    }

    /** Árvore ornamental com folhas persistentes. */
    public void tree(int x, int y, int z, int height, Block logBlock, Block leavesBlock) {
        pillar(x, y, y + height, z, logBlock);

        BlockState leafState = leavesBlock.defaultBlockState();
        if (leafState.hasProperty(LeavesBlock.PERSISTENT)) {
            leafState = leafState.setValue(LeavesBlock.PERSISTENT, true);
        }

        int canopyY = y + height - 1;
        for (int dy = -1; dy <= 2; dy++) {
            int radius = (dy == 2) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && dy != 0) continue;
                    int lx = x + dx;
                    int ly = canopyY + dy;
                    int lz = z + dz;
                    setIfAir(lx, ly, lz, leafState);
                }
            }
        }
    }

    /** Mostrador do relógio (círculo 5x5 com borda de ouro e ponteiros de concreto preto). */
    public void clockDisc(int cx, int cy, int cz, Direction face) {
        BlockState gold = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState white = Blocks.WHITE_CONCRETE.defaultBlockState();
        BlockState black = Blocks.BLACK_CONCRETE.defaultBlockState();

        for (int u = -2; u <= 2; u++) {
            for (int v = -2; v <= 2; v++) {
                int d2 = u * u + v * v;
                if (d2 <= 5) {
                    int x = cx + (face.getAxis() == Direction.Axis.Z ? u : 0);
                    int y = cy + v;
                    int z = cz + (face.getAxis() == Direction.Axis.X ? u : 0);

                    if (d2 >= 4) {
                        set(x, y, z, gold);
                    } else if (u == 0 || (v == 0 && u >= 0)) {
                        set(x, y, z, black);
                    } else {
                        set(x, y, z, white);
                    }
                }
            }
        }
    }

    /** Gera uma cordilheira montanhosa natural em uma faixa retangular em torno das muralhas. */
    public void mountainRidge(int minX, int minZ, int maxX, int maxZ, int refCoord, boolean isZAxis, boolean positiveSlope) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int dist = isZAxis
                        ? (positiveSlope ? (z - refCoord) : (refCoord - z))
                        : (positiveSlope ? (x - refCoord) : (refCoord - x));
                if (dist < 0) continue;

                double factor = Math.min(1.0, dist / 38.0);
                double wave = Math.sin(x * 0.09) * 7.0 + Math.cos(z * 0.09) * 7.0 + Math.sin((x + z) * 0.14) * 4.0;
                int peak = (int) (64 + (factor * 44.0) + wave);
                if (peak < 64) peak = 64;

                // Preenchimento de rocha
                for (int y = 64; y <= peak; y++) {
                    pos.set(x, y, z);
                    if (y == peak && peak >= 100) {
                        set(pos, Blocks.SNOW_BLOCK);
                    } else if (y == peak && peak >= 84) {
                        set(pos, (x + z) % 3 == 0 ? Blocks.CALCITE : Blocks.STONE);
                    } else if (y == peak) {
                        set(pos, Blocks.GRASS_BLOCK);
                    } else if (y >= peak - 3 && peak < 84) {
                        set(pos, Blocks.DIRT);
                    } else {
                        set(pos, (y + x) % 5 == 0 ? Blocks.ANDESITE : Blocks.STONE);
                    }
                }

                // Árvores de pinheiro nas encostas médias e baixas
                if (peak >= 66 && peak <= 82 && Math.abs(x * 37 + z * 19) % 38 == 0) {
                    tree(x, peak + 1, z, 5 + ((x + z) % 3), Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES);
                }
            }
        }
    }

    /** Monumento da Espada Colossal de Titã cravada na rocha sagrada (18 blocos de altura). */
    public void giantSwordMonument(int cx, int cy, int cz) {
        // Rochede de ancoragem em ardósia profunda e tufo
        for (int dy = 0; dy <= 4; dy++) {
            int rad = 5 - dy;
            for (int dx = -rad; dx <= rad; dx++) {
                for (int dz = -rad; dz <= rad; dz++) {
                    if (dx * dx + dz * dz <= rad * rad) {
                        set(cx + dx, cy + dy, cz + dz, (dx + dz) % 2 == 0 ? Blocks.DEEPSLATE : Blocks.TUFF);
                    }
                }
            }
        }

        // Lâmina de ferro e quartzo (cy + 2 a cy + 18)
        for (int y = cy + 2; y <= cy + 18; y++) {
            set(cx, y, cz, Blocks.IRON_BLOCK);
            set(cx - 1, y, cz, Blocks.SMOOTH_QUARTZ);
            set(cx + 1, y, cz, Blocks.SMOOTH_QUARTZ);
            // Runas arcanas luminosas na lâmina
            if (y % 4 == 0) {
                set(cx, y, cz, Blocks.AMETHYST_BLOCK);
            } else if (y == cy + 10) {
                set(cx, y, cz, Blocks.SEA_LANTERN);
            }
        }

        // Guarda-Mão da Espada (Crossguard em cy + 19)
        for (int dx = -3; dx <= 3; dx++) {
            set(cx + dx, cy + 19, cz, Blocks.GOLD_BLOCK);
        }
        set(cx - 3, cy + 20, cz, Blocks.REDSTONE_BLOCK); // Joia na ponta esquerda
        set(cx + 3, cy + 20, cz, Blocks.REDSTONE_BLOCK); // Joia na ponta direita
        set(cx, cy + 19, cz - 1, Blocks.GOLD_BLOCK);
        set(cx, cy + 19, cz + 1, Blocks.GOLD_BLOCK);

        // Empunhadura (Hilt em cy + 20 a cy + 24)
        for (int y = cy + 20; y <= cy + 24; y++) {
            set(cx, y, cz, Blocks.POLISHED_BASALT);
        }

        // Pomo Dourado (Pommel em cy + 25)
        set(cx, cy + 25, cz, Blocks.GOLD_BLOCK);
        set(cx, cy + 26, cz, Blocks.SEA_LANTERN);
    }

    /** A Esfera Celeste Arcana (Astrolábio/Armilar) no centro da praça. */
    public void armillarySphere(int cx, int cy, int cz) {
        // Pedestal de sustentação de quartzo
        pillar(cx, cy, cy + 4, cz, Blocks.QUARTZ_PILLAR);
        set(cx - 1, cy + 4, cz, Blocks.QUARTZ_BRICKS);
        set(cx + 1, cy + 4, cz, Blocks.QUARTZ_BRICKS);
        set(cx, cy + 4, cz - 1, Blocks.QUARTZ_BRICKS);
        set(cx, cy + 4, cz + 1, Blocks.QUARTZ_BRICKS);

        int coreY = cy + 8;
        // Núcleo de Cristal com Sinalizador
        set(cx, coreY - 1, cz, Blocks.NETHERITE_BLOCK);
        set(cx, coreY, cz, Blocks.BEACON);
        set(cx, coreY + 1, cz, Blocks.SEA_LANTERN);

        // Anel Orbital 1: Horizontal (Plano X-Z de Ouro, raio 4)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 >= 13 && d2 <= 17) {
                    set(cx + dx, coreY, cz + dz, Blocks.GOLD_BLOCK);
                }
            }
        }

        // Anel Orbital 2: Vertical X-Y (Prismarinho Escuro, raio 5)
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                int d2 = dx * dx + dy * dy;
                if (d2 >= 20 && d2 <= 26) {
                    set(cx + dx, coreY + dy, cz, Blocks.DARK_PRISMARINE);
                }
            }
        }

        // Anel Orbital 3: Vertical Z-Y (Cobre Lapidado, raio 5)
        for (int dz = -5; dz <= 5; dz++) {
            for (int dy = -5; dy <= 5; dy++) {
                int d2 = dz * dz + dy * dy;
                if (d2 >= 20 && d2 <= 26) {
                    set(cx, coreY + dy, cz + dz, Blocks.CUT_COPPER);
                }
            }
        }
    }

    /** Estátua colossal de guardião heróico construída com blocos (8 blocos de altura). */
    public void guardianStatue(int x, int y, int z, Direction facing, boolean isWarrior) {
        // Pedestal 3x3
        fill(x - 1, y, z - 1, x + 1, y, z + 1, Blocks.POLISHED_ANDESITE);

        // Pernas em ardósia polida
        pillar(x - 1, y + 1, y + 2, z, Blocks.POLISHED_DEEPSLATE);
        pillar(x + 1, y + 1, y + 2, z, Blocks.POLISHED_DEEPSLATE);

        // Torso / Armadura
        Block armorBlock = isWarrior ? Blocks.IRON_BLOCK : Blocks.LAPIS_BLOCK;
        fill(x - 1, y + 3, z, x + 1, y + 4, z, armorBlock);
        set(x, y + 3, z, Blocks.GOLD_BLOCK); // Brasão central dourado

        // Ombros e Braços
        set(x - 1, y + 4, z, Blocks.POLISHED_DEEPSLATE_WALL);
        set(x + 1, y + 4, z, Blocks.POLISHED_DEEPSLATE_WALL);

        // Cabeça e Elmo
        set(x, y + 5, z, Blocks.CHISELED_STONE_BRICKS);
        set(x, y + 6, z, Blocks.GOLD_BLOCK); // Coroa/Pluma

        // Arma do Guardião
        if (isWarrior) {
            // Grande Lança de Ferro erguida
            pillar(x + 2, y + 1, y + 6, z, Blocks.IRON_BARS);
            set(x + 2, y + 7, z, Blocks.IRON_BLOCK);
        } else {
            // Cajado Místico com Orbe de Ametista
            pillar(x + 2, y + 1, y + 5, z, Blocks.END_ROD);
            set(x + 2, y + 6, z, Blocks.AMETHYST_BLOCK);
            set(x + 2, y + 7, z, Blocks.SEA_LANTERN);
        }
    }

    /** Obelisco de Runa Arcana flutuante com cristal. */
    public void arcaneMonolith(int x, int y, int z) {
        set(x, y, z, Blocks.CRYING_OBSIDIAN);
        pillar(x, y + 2, y + 5, z, Blocks.CHISELED_DEEPSLATE);
        set(x, y + 3, z, Blocks.AMETHYST_BLOCK);
        set(x, y + 4, z, Blocks.SEA_LANTERN);
    }
}
