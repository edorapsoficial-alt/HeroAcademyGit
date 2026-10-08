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
}
