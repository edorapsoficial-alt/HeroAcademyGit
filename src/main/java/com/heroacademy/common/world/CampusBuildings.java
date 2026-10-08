package com.heroacademy.common.world;

import com.heroacademy.common.academic.CampusWaypoint;
import com.heroacademy.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.Random;

import static com.heroacademy.common.world.CampusLayout.*;

/**
 * Geração procedural de todos os prédios, praça, estradas, muralha,
 * docas e áreas da Academia Imperial Destiny.
 */
public final class CampusBuildings {
    private CampusBuildings() {}

    // =========================================================================
    // 1. ESTRADAS E PRAÇA CENTRAL
    // =========================================================================

    public static void buildRoads(BlockKit kit) {
        for (Plot r : ROADS) {
            // Pavimenta o piso da estrada
            kit.fill(r.minX(), FLOOR_Y, r.minZ(), r.maxX(), FLOOR_Y, r.maxZ(), Blocks.STONE_BRICKS);
            // Bordas da estrada com andesito polido
            kit.fill(r.minX(), FLOOR_Y, r.minZ(), r.minX(), FLOOR_Y, r.maxZ(), Blocks.POLISHED_ANDESITE);
            kit.fill(r.maxX(), FLOOR_Y, r.minZ(), r.maxX(), FLOOR_Y, r.maxZ(), Blocks.POLISHED_ANDESITE);
            kit.fill(r.minX(), FLOOR_Y, r.minZ(), r.maxX(), FLOOR_Y, r.minZ(), Blocks.POLISHED_ANDESITE);
            kit.fill(r.minX(), FLOOR_Y, r.maxZ(), r.maxX(), FLOOR_Y, r.maxZ(), Blocks.POLISHED_ANDESITE);
            // Garante passagem livre de ar
            kit.clear(r.minX(), G, r.minZ(), r.maxX(), G + 3, r.maxZ());
        }

        // Postes de iluminação ao longo da Avenida Principal
        for (int z = -40; z <= 160; z += 20) {
            if (z >= -PLAZA_RADIUS && z <= PLAZA_RADIUS) continue; // Pula dentro da praça
            kit.lampPost(4, G, z);
            kit.lampPost(-4, G, z);
        }
    }

    public static void buildPlaza(BlockKit kit) {
        // Pavimentação circular da Praça Imperial
        kit.disc(0, FLOOR_Y, 0, PLAZA_RADIUS, Blocks.POLISHED_ANDESITE);
        kit.ring(0, FLOOR_Y, 0, PLAZA_RADIUS - 2, PLAZA_RADIUS, Blocks.STONE_BRICKS);
        kit.ring(0, FLOOR_Y, 0, 15, 17, Blocks.POLISHED_DIORITE);
        kit.clear(-PLAZA_RADIUS, G, -PLAZA_RADIUS, PLAZA_RADIUS, G + 16, PLAZA_RADIUS);

        // Fonte Central Monumental
        kit.ring(0, G, 0, 8, 8, Blocks.QUARTZ_BRICKS);
        kit.disc(0, FLOOR_Y, 0, 7, Blocks.SEA_LANTERN);
        kit.disc(0, G, 0, 7, Blocks.WATER);

        // A Esfera Celeste Arcana (Astrolábio/Armilar) no coração da academia
        kit.armillarySphere(0, G, 0);

        // 4 Estátuas Colossais dos Guardiões Fundadores nos vértices da praça
        kit.guardianStatue(-17, G, -17, Direction.SOUTH, true);  // Guardião do Norte-Oeste (Guerreiro)
        kit.guardianStatue(17, G, -17, Direction.SOUTH, false);  // Guardião do Norte-Leste (Arquimago)
        kit.guardianStatue(-17, G, 17, Direction.NORTH, false);  // Guardião do Sul-Oeste (Alquimista)
        kit.guardianStatue(17, G, 17, Direction.NORTH, true);   // Guardião do Sul-Leste (Paladino)

        // Bancos e Floreiras ao redor do anel externo
        int r = PLAZA_RADIUS - 2;
        int[][] angles = {
                {r, 0}, {-r, 0}, {0, r}, {0, -r},
                {(int)(r * 0.7), (int)(r * 0.7)}, {-(int)(r * 0.7), (int)(r * 0.7)},
                {(int)(r * 0.7), -(int)(r * 0.7)}, {-(int)(r * 0.7), -(int)(r * 0.7)}
        };
        for (int[] p : angles) {
            kit.lampPost(p[0], G, p[1]);
            // Floreiras com azaleias floridas ao lado dos postes
            kit.setIfAir(p[0] + 1, G, p[1], Blocks.FLOWERING_AZALEA);
            kit.setIfAir(p[0] - 1, G, p[1], Blocks.FLOWERING_AZALEA);
        }
    }

    // =========================================================================
    // 2. MURALHA PERIMETRAL E PORTÃO SUL
    // =========================================================================

    public static void buildPerimeterWall(BlockKit kit) {
        Plot w = WALL;
        // Muros de tijolos de pedra
        kit.walls(w.minX(), G, w.minZ(), w.maxX(), G + 4, w.maxZ(), Blocks.STONE_BRICKS);
        // Beiral superior de laje de tijolos de pedra
        BlockState slab = kit.slab(Blocks.STONE_BRICK_SLAB);
        for (int x = w.minX(); x <= w.maxX(); x++) {
            kit.set(x, G + 5, w.minZ(), slab);
            kit.set(x, G + 5, w.maxZ(), slab);
        }
        for (int z = w.minZ(); z <= w.maxZ(); z++) {
            kit.set(w.minX(), G + 5, z, slab);
            kit.set(w.maxX(), G + 5, z, slab);
        }

        // Pilares de pedra negra a cada 10 blocos
        for (int x = w.minX(); x <= w.maxX(); x += 10) {
            kit.pillar(x, G, G + 5, w.minZ(), Blocks.POLISHED_BLACKSTONE_BRICKS);
            kit.set(x, G + 6, w.minZ(), Blocks.LANTERN);
            kit.pillar(x, G, G + 5, w.maxZ(), Blocks.POLISHED_BLACKSTONE_BRICKS);
            kit.set(x, G + 6, w.maxZ(), Blocks.LANTERN);
        }
        for (int z = w.minZ(); z <= w.maxZ(); z += 10) {
            kit.pillar(w.minX(), G, G + 5, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            kit.set(w.minX(), G + 6, z, Blocks.LANTERN);
            kit.pillar(w.maxX(), G, G + 5, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
            kit.set(w.maxX(), G + 6, z, Blocks.LANTERN);
        }

        // Portão Sul Imperial (abertura em z = 250)
        kit.clear(-5, G, w.maxZ(), 5, G + 5, w.maxZ());
        kit.fill(-5, G + 6, w.maxZ(), 5, G + 7, w.maxZ(), Blocks.QUARTZ_BRICKS);

        // Torres do Portão Sul
        kit.walls(-10, G, w.maxZ() - 2, -6, G + 10, w.maxZ() + 2, Blocks.STONE_BRICKS);
        kit.hipRoof(-11, w.maxZ() - 3, -5, w.maxZ() + 3, G + 11, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);

        kit.walls(6, G, w.maxZ() - 2, 10, G + 10, w.maxZ() + 2, Blocks.STONE_BRICKS);
        kit.hipRoof(5, w.maxZ() - 3, 11, w.maxZ() + 3, G + 11, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);
    }

    // =========================================================================
    // 3. GRANDE HALL IMPERIAL
    // =========================================================================

    public static void buildGrandHall(BlockKit kit) {
        Plot h = GRAND_HALL;
        kit.fill(h.minX(), FLOOR_Y, h.minZ(), h.maxX(), FLOOR_Y, h.maxZ(), Blocks.POLISHED_DEEPSLATE);
        kit.clear(h.minX() + 1, G, h.minZ() + 1, h.maxX() - 1, G + 14, h.maxZ() - 1);
        kit.walls(h.minX(), G, h.minZ(), h.maxX(), G + 14, h.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilastras de quartzo a cada 8 blocos
        for (int x = h.minX(); x <= h.maxX(); x += 8) {
            kit.pillar(x, G, G + 14, h.maxZ(), Blocks.QUARTZ_PILLAR);
            kit.pillar(x, G, G + 14, h.minZ(), Blocks.QUARTZ_PILLAR);
        }

        // Janelas altas de vidro azul claro
        for (int x = h.minX() + 4; x <= h.maxX() - 4; x += 8) {
            if (x >= -6 && x <= 6) continue; // Pula entrada
            kit.fill(x, G + 2, h.maxZ(), x + 1, G + 10, h.maxZ(), Blocks.LIGHT_BLUE_STAINED_GLASS);
            kit.fill(x, G + 2, h.minZ(), x + 1, G + 10, h.minZ(), Blocks.LIGHT_BLUE_STAINED_GLASS);
        }

        // Portal de Entrada Sul (voltado para a praça)
        kit.clear(-4, G, h.maxZ(), 4, G + 7, h.maxZ());
        kit.pillar(-5, G, G + 7, h.maxZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(5, G, G + 7, h.maxZ(), Blocks.QUARTZ_PILLAR);
        kit.fill(-5, G + 8, h.maxZ(), 5, G + 8, h.maxZ(), Blocks.GOLD_BLOCK);

        // Portais laterais (para os caminhos dos dormitórios)
        kit.clear(h.minX(), G, -62, h.minX(), G + 4, -58);
        kit.clear(h.maxX(), G, -62, h.maxX(), G + 4, -58);

        // Passagem ao fundo para a Torre do Relógio
        kit.clear(-3, G, h.minZ(), 3, G + 5, h.minZ());

        // Telhado de duas águas
        kit.gableAlongX(h.minX() - 1, h.maxX() + 1, h.minZ() - 1, h.maxZ() + 1, G + 15,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILES);

        // 4 Torres de Canto
        int[][] corners = {
                {h.minX() - 2, h.minZ() - 2}, {h.maxX() - 4, h.minZ() - 2},
                {h.minX() - 2, h.maxZ() - 4}, {h.maxX() - 4, h.maxZ() - 4}
        };
        for (int[] c : corners) {
            kit.walls(c[0], G, c[1], c[0] + 6, G + 22, c[1] + 6, Blocks.QUARTZ_BRICKS);
            kit.hipRoof(c[0] - 1, c[1] - 1, c[0] + 7, c[1] + 7, G + 23,
                    Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);
            kit.set(c[0] + 3, G + 28, c[1] + 3, Blocks.LIGHTNING_ROD);
        }

        // Interior: Colunatas e Tapete Vermelho
        for (int z = h.minZ() + 4; z <= h.maxZ() - 4; z += 6) {
            kit.pillar(-8, G, G + 13, z, Blocks.QUARTZ_PILLAR);
            kit.pillar(8, G, G + 13, z, Blocks.QUARTZ_PILLAR);
            kit.chandelier(-8, G + 13, 2, z);
            kit.chandelier(8, G + 13, 2, z);
        }

        // Tapete Vermelho central com bordas douradas
        kit.fill(-2, G, h.minZ() + 6, 2, G, h.maxZ() - 1, Blocks.RED_CARPET);
        kit.fill(-3, G, h.minZ() + 6, -3, G, h.maxZ() - 1, Blocks.YELLOW_CARPET);
        kit.fill(3, G, h.minZ() + 6, 3, G, h.maxZ() - 1, Blocks.YELLOW_CARPET);

        // Trono Imperial ao fundo
        kit.fill(-4, G, h.minZ() + 2, 4, G, h.minZ() + 5, Blocks.SMOOTH_QUARTZ);
        kit.set(0, G + 1, h.minZ() + 3, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH));
        kit.fill(-1, G + 1, h.minZ() + 2, 1, G + 4, h.minZ() + 2, Blocks.GOLD_BLOCK);
        kit.set(-3, G + 1, h.minZ() + 3, Blocks.ENCHANTING_TABLE);
        kit.set(3, G + 1, h.minZ() + 3, Blocks.ENCHANTING_TABLE);
    }

    // =========================================================================
    // 4. TORRE DO RELÓGIO IMPERIAL
    // =========================================================================

    public static void buildClockTower(BlockKit kit) {
        Plot t = CLOCK_TOWER;
        kit.fill(t.minX(), FLOOR_Y, t.minZ(), t.maxX(), FLOOR_Y, t.maxZ(), Blocks.POLISHED_DEEPSLATE);
        kit.clear(t.minX() + 1, G, t.minZ() + 1, t.maxX() - 1, G + 50, t.maxZ() - 1);
        kit.walls(t.minX(), G, t.minZ(), t.maxX(), G + 46, t.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilastras nos 4 cantos da torre
        kit.pillar(t.minX(), G, G + 46, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G, G + 46, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.minX(), G, G + 46, t.maxZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G, G + 46, t.maxZ(), Blocks.QUARTZ_PILLAR);

        // Escada de mão interna subindo toda a torre na parede norte
        int ladderZ = t.minZ() + 1;
        BlockState ladderState = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
        for (int y = G; y <= G + 46; y++) {
            kit.set(0, y, ladderZ, ladderState);
        }

        // Decks intermediários
        int[] decks = {G + 14, G + 28, G + 42};
        for (int dy : decks) {
            kit.fill(t.minX() + 1, dy, t.minZ() + 1, t.maxX() - 1, dy, t.maxZ() - 1, Blocks.SMOOTH_QUARTZ);
            kit.clear(-1, dy, ladderZ, 1, dy, ladderZ + 1); // Abertura para a escada
        }

        // Mostradores do Relógio nas 4 faces no nível y = 104
        int clockY = G + 40;
        int cx = t.centerX();
        int cz = t.centerZ();
        kit.clockDisc(cx, clockY, t.minZ(), Direction.SOUTH);
        kit.clockDisc(cx, clockY, t.maxZ(), Direction.NORTH);
        kit.clockDisc(t.minX(), clockY, cz, Direction.EAST);
        kit.clockDisc(t.maxX(), clockY, cz, Direction.WEST);

        // Campanário aberto no topo (y = G + 47 a G + 54)
        kit.fill(t.minX(), G + 47, t.minZ(), t.maxX(), G + 47, t.maxZ(), Blocks.SMOOTH_QUARTZ);
        kit.pillar(t.minX(), G + 48, G + 53, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G + 48, G + 53, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.minX(), G + 48, G + 53, t.maxZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G + 48, G + 53, t.maxZ(), Blocks.QUARTZ_PILLAR);

        // Sino Imperial no centro
        kit.set(cx, G + 48, cz, Blocks.BELL);

        // Telhado em pirâmide e pináculo de ouro com para-raios
        kit.hipRoof(t.minX() - 1, t.minZ() - 1, t.maxX() + 1, t.maxZ() + 1, G + 54,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);
        kit.pillar(cx, G + 61, G + 63, cz, Blocks.GOLD_BLOCK);
        kit.set(cx, G + 64, cz, Blocks.LIGHTNING_ROD);
    }

    // =========================================================================
    // 5. GRANDE BIBLIOTECA IMPERIAL
    // =========================================================================

    public static void buildLibrary(BlockKit kit) {
        Plot l = LIBRARY;
        kit.fill(l.minX(), FLOOR_Y, l.minZ(), l.maxX(), FLOOR_Y, l.maxZ(), Blocks.SPRUCE_PLANKS);
        kit.clear(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 1, G + 10, l.maxZ() - 1);
        kit.walls(l.minX(), G, l.minZ(), l.maxX(), G + 10, l.maxZ(), Blocks.STONE_BRICKS);

        // Portal de entrada na parede leste voltado para a Alameda Central
        kit.clear(l.maxX(), G, -2, l.maxX(), G + 4, 2);
        kit.pillar(l.maxX(), G, G + 5, -3, Blocks.DARK_OAK_LOG);
        kit.pillar(l.maxX(), G, G + 5, 3, Blocks.DARK_OAK_LOG);

        // Telhado de duas águas
        kit.gableAlongX(l.minX() - 1, l.maxX() + 1, l.minZ() - 1, l.maxZ() + 1, G + 11,
                Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_PLANKS);

        // Estantes pelas paredes
        kit.fill(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 1, G + 4, l.minZ() + 1, Blocks.BOOKSHELF);
        kit.fill(l.minX() + 1, G, l.maxZ() - 1, l.maxX() - 1, G + 4, l.maxZ() - 1, Blocks.BOOKSHELF);
        kit.fill(l.minX() + 1, G, l.minZ() + 1, l.minX() + 1, G + 4, l.maxZ() - 1, Blocks.BOOKSHELF);

        // Fileiras de estantes centrais
        for (int z = l.minZ() + 6; z <= l.maxZ() - 6; z += 6) {
            kit.fill(l.minX() + 4, G, z, l.maxX() - 8, G + 3, z, Blocks.BOOKSHELF);
        }

        // Mesas de leitura e cadeiras
        for (int x = l.minX() + 6; x <= l.maxX() - 10; x += 6) {
            kit.set(x, G, 0, Blocks.DARK_OAK_FENCE);
            kit.set(x, G + 1, 0, Blocks.SPRUCE_PRESSURE_PLATE);
            kit.set(x, G, 1, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.NORTH));
            kit.set(x, G, -1, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.SOUTH));
        }

        // Seção arcana com mesa de encantamentos
        kit.set(l.minX() + 4, G, -10, Blocks.ENCHANTING_TABLE);
        kit.set(l.minX() + 4, G, 10, Blocks.ENCHANTING_TABLE);

        // Lustres no teto
        for (int x = l.minX() + 8; x <= l.maxX() - 8; x += 10) {
            kit.chandelier(x, G + 10, 2, -10);
            kit.chandelier(x, G + 10, 2, 10);
        }
    }

    // =========================================================================
    // 6. GRANDE REFEITÓRIO IMPERIAL (FOOD HALL)
    // =========================================================================

    public static void buildFoodHall(BlockKit kit) {
        Plot f = FOOD_HALL;
        kit.fill(f.minX(), FLOOR_Y, f.minZ(), f.maxX(), FLOOR_Y, f.maxZ(), Blocks.SMOOTH_SANDSTONE);
        kit.clear(f.minX() + 1, G, f.minZ() + 1, f.maxX() - 1, G + 10, f.maxZ() - 1);
        kit.walls(f.minX(), G, f.minZ(), f.maxX(), G + 10, f.maxZ(), Blocks.CUT_SANDSTONE);

        // Entrada na parede oeste voltada para a Alameda Central
        kit.clear(f.minX(), G, -2, f.minX(), G + 4, 2);
        kit.pillar(f.minX(), G, G + 5, -3, Blocks.BRICKS);
        kit.pillar(f.minX(), G, G + 5, 3, Blocks.BRICKS);

        // Telhado de cobre lapidado
        kit.gableAlongX(f.minX() - 1, f.maxX() + 1, f.minZ() - 1, f.maxZ() + 1, G + 11,
                Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER, Blocks.CUT_COPPER);

        // 3 Grandes mesas de banquete corridas
        int[] tableZ = {-18, -11, -4};
        for (int tz : tableZ) {
            kit.fill(f.minX() + 6, G, tz, f.maxX() - 14, G, tz, kit.slab(Blocks.SPRUCE_SLAB, net.minecraft.world.level.block.state.properties.SlabType.TOP));
            // Cadeiras
            for (int x = f.minX() + 6; x <= f.maxX() - 14; x += 2) {
                kit.set(x, G, tz + 1, kit.stair(Blocks.SPRUCE_STAIRS, Direction.NORTH));
                kit.set(x, G, tz - 1, kit.stair(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
            }
            // Comidas, bolos, velas e louças decorativas nas mesas
            for (int x = f.minX() + 8; x <= f.maxX() - 16; x += 4) {
                if ((x + tz) % 4 == 0) {
                    kit.set(x, G + 1, tz, Blocks.CAKE);
                } else if ((x + tz) % 4 == 1) {
                    kit.set(x, G + 1, tz, Blocks.FLOWER_POT);
                } else if ((x + tz) % 4 == 2) {
                    kit.set(x, G + 1, tz, Blocks.CANDLE);
                }
            }
        }

        // Cozinha e balcão na ala leste
        int kx = f.maxX() - 8;
        kit.fill(kx, G, f.minZ() + 2, kx, G, f.maxZ() - 2, Blocks.POLISHED_ANDESITE);
        for (int z = f.minZ() + 3; z <= f.maxZ() - 3; z += 2) {
            kit.set(f.maxX() - 1, G, z, Blocks.SMOKER);
            kit.set(f.maxX() - 1, G + 1, z, Blocks.BARREL);
            kit.set(f.maxX() - 1, G + 2, z, Blocks.CHEST);
        }
        kit.set(f.maxX() - 2, G, f.minZ() + 2, Blocks.WATER_CAULDRON);
        kit.set(f.maxX() - 2, G, f.maxZ() - 2, Blocks.WATER_CAULDRON);
        kit.set(kx, G + 1, f.minZ() + 4, Blocks.CAKE);

        // Lustres
        for (int x = f.minX() + 8; x <= f.maxX() - 12; x += 10) {
            kit.chandelier(x, G + 10, 2, -15);
            kit.chandelier(x, G + 10, 2, -8);
        }
    }

    // =========================================================================
    // 7. LABORATÓRIO DE CIÊNCIA MÁGICA & ALQUIMIA
    // =========================================================================

    public static void buildLab(BlockKit kit) {
        Plot l = LAB;
        kit.fill(l.minX(), FLOOR_Y, l.minZ(), l.maxX(), FLOOR_Y, l.maxZ(), Blocks.POLISHED_DIORITE);
        kit.clear(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 1, G + 9, l.maxZ() - 1);
        kit.walls(l.minX(), G, l.minZ(), l.maxX(), G + 8, l.maxZ(), Blocks.SMOOTH_QUARTZ);

        // Entrada na parede norte
        kit.clear(l.centerX() - 2, G, l.minZ(), l.centerX() + 2, G + 4, l.minZ());

        // Teto plano com parapeito
        kit.fill(l.minX(), G + 9, l.minZ(), l.maxX(), G + 9, l.maxZ(), Blocks.POLISHED_ANDESITE);
        kit.walls(l.minX(), G + 10, l.minZ(), l.maxX(), G + 10, l.maxZ(), Blocks.STONE_BRICK_WALL);

        // Cúpula central de vidro
        int cx = l.centerX();
        int cz = l.centerZ();
        kit.clear(cx - 5, G + 9, cz - 5, cx + 5, G + 9, cz + 5);
        kit.disc(cx, G + 9, cz, 5, Blocks.GLASS);
        kit.disc(cx, G + 10, cz, 4, Blocks.GLASS);
        kit.disc(cx, G + 11, cz, 3, Blocks.GLASS);
        kit.disc(cx, G + 12, cz, 2, Blocks.SEA_LANTERN);

        // Bancadas de alquimia com suportes de poções e caldeirões
        for (int x = l.minX() + 4; x <= l.maxX() - 4; x += 4) {
            kit.set(x, G, l.minZ() + 3, Blocks.SMOOTH_STONE_SLAB);
            kit.set(x, G + 1, l.minZ() + 3, Blocks.BREWING_STAND);

            kit.set(x, G, l.maxZ() - 3, Blocks.SMOOTH_STONE_SLAB);
            kit.set(x, G + 1, l.maxZ() - 3, Blocks.CAULDRON);
        }

        kit.set(cx - 6, G, cz, Blocks.ENCHANTING_TABLE);
        kit.set(cx + 6, G, cz, Blocks.ENCHANTING_TABLE);

        // Tanques de contenção de vidro com água
        kit.walls(cx - 3, G, cz - 3, cx + 3, G + 3, cz + 3, Blocks.GLASS);
        kit.fill(cx - 2, G, cz - 2, cx + 2, G + 2, cz + 2, Blocks.WATER);
        kit.set(cx, G, cz, Blocks.SEA_LANTERN);
    }

    // =========================================================================
    // 8. ESTUFA BOTÂNICA ARCANA
    // =========================================================================

    public static void buildGreenhouse(BlockKit kit) {
        Plot g = GREENHOUSE;
        kit.fill(g.minX(), FLOOR_Y, g.minZ(), g.maxX(), FLOOR_Y, g.maxZ(), Blocks.GRASS_BLOCK);
        kit.clear(g.minX() + 1, G, g.minZ() + 1, g.maxX() - 1, G + 12, g.maxZ() - 1);

        // Plinto de tijolos de pedra e paredes de vidro com armação de carvalho
        kit.walls(g.minX(), G, g.minZ(), g.maxX(), G, g.maxZ(), Blocks.STONE_BRICKS);
        kit.walls(g.minX(), G + 1, g.minZ(), g.maxX(), G + 8, g.maxZ(), Blocks.GLASS);

        for (int x = g.minX(); x <= g.maxX(); x += 6) {
            kit.pillar(x, G, G + 8, g.minZ(), Blocks.OAK_LOG);
            kit.pillar(x, G, G + 8, g.maxZ(), Blocks.OAK_LOG);
        }

        // Entrada na parede norte
        kit.clear(g.centerX() - 2, G, g.minZ(), g.centerX() + 2, G + 4, g.minZ());

        // Abóbada cilíndrica de vidro
        kit.vaultAlongX(g.minX(), g.maxX(), g.minZ(), g.maxZ(), G + 9, Blocks.GLASS);

        // Caminho central de pedra
        kit.fill(g.centerX() - 1, G, g.minZ() + 1, g.centerX() + 1, G, g.maxZ() - 1, Blocks.STONE_BRICKS);

        // Canteiros de flores raras
        BlockState[] flowers = {
                Blocks.FLOWERING_AZALEA.defaultBlockState(),
                Blocks.PEONY.defaultBlockState(),
                Blocks.ROSE_BUSH.defaultBlockState(),
                Blocks.ALLIUM.defaultBlockState(),
                Blocks.CORNFLOWER.defaultBlockState()
        };

        for (int x = g.minX() + 3; x <= g.maxX() - 3; x += 3) {
            if (x >= g.centerX() - 2 && x <= g.centerX() + 2) continue;
            kit.set(x, G, g.minZ() + 4, flowers[(x * 7) % flowers.length]);
            kit.set(x, G, g.maxZ() - 4, flowers[(x * 11) % flowers.length]);
        }

        // Pequeno lago central com vitória-régia
        kit.fill(g.centerX() - 3, G, g.centerZ() - 3, g.centerX() + 3, G, g.centerZ() + 3, Blocks.WATER);
        kit.set(g.centerX(), G + 1, g.centerZ(), Blocks.LILY_PAD);
    }

    // =========================================================================
    // 9. BLOCO ACADÊMICO (4 SALAS DE AULA TEMÁTICAS)
    // =========================================================================

    public static void buildAcademicBlock(BlockKit kit) {
        buildClassroom(kit, HISTORY, "História", Blocks.SMOOTH_SANDSTONE, Blocks.SPRUCE_STAIRS, 0);
        buildClassroom(kit, ZOOLOGY, "Zoologia", Blocks.MOSSY_STONE_BRICKS, Blocks.OAK_STAIRS, 1);
        buildClassroom(kit, PHILOSOPHY, "Filosofia", Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_STAIRS, 2);
        buildClassroom(kit, TACTICS, "Tática", Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, 3);

        // Ponte/Portal Imperial sobre a Avenida Central entre as salas
        kit.clear(-6, G, 67, 6, G + 5, 71);
        kit.fill(-6, G + 6, 67, 6, G + 7, 71, Blocks.QUARTZ_BRICKS);
        kit.walls(-6, G + 8, 67, 6, G + 8, 71, Blocks.QUARTZ_PILLAR);
        kit.lampPost(-5, G + 9, 69);
        kit.lampPost(5, G + 9, 69);
    }

    private static void buildClassroom(BlockKit kit, Plot plot, String theme, Block wallBlock, Block roofStair, int type) {
        kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.POLISHED_ANDESITE);
        kit.clear(plot.minX() + 1, G, plot.minZ() + 1, plot.maxX() - 1, G + 8, plot.maxZ() - 1);
        kit.walls(plot.minX(), G, plot.minZ(), plot.maxX(), G + 7, plot.maxZ(), wallBlock);

        // Porta na parede norte
        kit.clear(plot.centerX() - 1, G, plot.minZ(), plot.centerX() + 1, G + 3, plot.minZ());

        // Telhado de duas águas
        kit.gableAlongX(plot.minX() - 1, plot.maxX() + 1, plot.minZ() - 1, plot.maxZ() + 1, G + 8,
                roofStair, wallBlock, wallBlock);

        // Lousa negra na parede sul
        kit.fill(plot.centerX() - 4, G + 1, plot.maxZ(), plot.centerX() + 4, G + 3, plot.maxZ(), Blocks.BLACK_CONCRETE);

        // Mesa e Púlpito do Professor
        int teacherZ = plot.maxZ() - 3;
        kit.set(plot.centerX(), G, teacherZ, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.NORTH));
        kit.set(plot.centerX() + 1, G, teacherZ, ModBlocks.CLASSROOM_DESK.get());

        // Carteiras dos Alunos (ModBlocks.CLASSROOM_DESK)
        // 3 fileiras de 4 carteiras voltadas para a lousa
        for (int z = plot.minZ() + 4; z <= plot.minZ() + 12; z += 4) {
            for (int dx = -6; dx <= 6; dx += 4) {
                kit.set(plot.centerX() + dx, G, z, ModBlocks.CLASSROOM_DESK.get());
            }
        }

        // Iluminação no teto
        kit.set(plot.centerX() - 4, G + 7, plot.centerZ(), Blocks.SEA_LANTERN);
        kit.set(plot.centerX() + 4, G + 7, plot.centerZ(), Blocks.SEA_LANTERN);

        // Decoração temática por matéria
        if (type == 0) { // História
            kit.set(plot.minX() + 2, G, teacherZ, Blocks.CARTOGRAPHY_TABLE);
            kit.fill(plot.minX() + 1, G, plot.minZ() + 2, plot.minX() + 1, G + 3, plot.minZ() + 6, Blocks.BOOKSHELF);
        } else if (type == 1) { // Zoologia
            kit.walls(plot.minX() + 2, G, plot.minZ() + 2, plot.minX() + 5, G + 2, plot.minZ() + 5, Blocks.GLASS);
            kit.set(plot.minX() + 3, G, plot.minZ() + 3, Blocks.MOSS_BLOCK);
            kit.set(plot.minX() + 4, G, plot.minZ() + 4, Blocks.FERN);
        } else if (type == 2) { // Filosofia
            kit.disc(plot.centerX(), G, plot.centerZ(), 2, Blocks.WHITE_CARPET);
            kit.set(plot.centerX(), G, plot.centerZ(), Blocks.AMETHYST_BLOCK);
        } else if (type == 3) { // Tática
            // Mesa de relevo 3D militar
            int mx = plot.maxX() - 4;
            kit.fill(mx - 1, G, teacherZ - 1, mx + 1, G, teacherZ + 1, Blocks.DEEPSLATE_BRICKS);
            kit.set(mx, G + 1, teacherZ, Blocks.MOSS_BLOCK);
            kit.set(mx + 1, G + 1, teacherZ, Blocks.WATER);
        }
    }

    // =========================================================================
    // 10. DOJO & DEPARTAMENTO MARCIAL
    // =========================================================================

    public static void buildDojo(BlockKit kit) {
        Plot d = DOJO;
        kit.fill(d.minX(), FLOOR_Y, d.minZ(), d.maxX(), FLOOR_Y, d.maxZ(), Blocks.BAMBOO_PLANKS);
        kit.clear(d.minX() + 1, G, d.minZ() + 1, d.maxX() - 1, G + 12, d.maxZ() - 1);
        kit.walls(d.minX(), G, d.minZ(), d.maxX(), G + 10, d.maxZ(), Blocks.WHITE_CONCRETE);

        // Vigas de carvalho escuro nos cantos
        kit.pillar(d.minX(), G, G + 10, d.minZ(), Blocks.DARK_OAK_LOG);
        kit.pillar(d.maxX(), G, G + 10, d.minZ(), Blocks.DARK_OAK_LOG);
        kit.pillar(d.minX(), G, G + 10, d.maxZ(), Blocks.DARK_OAK_LOG);
        kit.pillar(d.maxX(), G, G + 10, d.maxZ(), Blocks.DARK_OAK_LOG);

        // Entrada na parede oeste
        kit.clear(d.minX(), G, d.centerZ() - 2, d.minX(), G + 4, d.centerZ() + 2);

        // Portão Torii vermelho na entrada externa
        kit.pillar(d.minX() - 3, G, G + 5, d.centerZ() - 3, Blocks.RED_NETHER_BRICKS);
        kit.pillar(d.minX() - 3, G, G + 5, d.centerZ() + 3, Blocks.RED_NETHER_BRICKS);
        kit.fill(d.minX() - 3, G + 6, d.centerZ() - 4, d.minX() - 3, G + 6, d.centerZ() + 4, Blocks.RED_NETHER_BRICK_SLAB);
        kit.fill(d.minX() - 3, G + 5, d.centerZ() - 3, d.minX() - 3, G + 5, d.centerZ() + 3, Blocks.RED_NETHER_BRICKS);

        // Telhado Pagoda em 2 níveis
        kit.hipRoof(d.minX() - 1, d.minZ() - 1, d.maxX() + 1, d.maxZ() + 1, G + 11,
                Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS, Blocks.GOLD_BLOCK);

        // Ringue de Sparring no centro
        int cx = d.centerX();
        int cz = d.centerZ();
        kit.fill(cx - 8, G, cz - 8, cx + 8, G, cz + 8, Blocks.RED_CONCRETE);
        kit.fill(cx - 6, G, cz - 6, cx + 6, G, cz + 6, Blocks.WHITE_CONCRETE);

        // Bonecos de treino ao longo da parede leste
        for (int z = d.minZ() + 6; z <= d.maxZ() - 6; z += 6) {
            int tx = d.maxX() - 3;
            kit.set(tx, G, z, Blocks.OAK_FENCE);
            kit.set(tx, G + 1, z, Blocks.HAY_BLOCK);
            kit.set(tx, G + 2, z, Blocks.CARVED_PUMPKIN);
        }

        // Suportes de armas e bigornas
        kit.set(d.minX() + 3, G, d.minZ() + 3, Blocks.ANVIL);
        kit.set(d.minX() + 3, G, d.maxZ() - 3, Blocks.GRINDSTONE);

        // Lanternas de teto
        kit.chandelier(cx - 8, G + 10, 2, cz - 8);
        kit.chandelier(cx + 8, G + 10, 2, cz - 8);
        kit.chandelier(cx - 8, G + 10, 2, cz + 8);
        kit.chandelier(cx + 8, G + 10, 2, cz + 8);
    }

    // =========================================================================
    // 11. COMPLEXO ESPORTIVO & ARENA
    // =========================================================================

    public static void buildSportsArena(BlockKit kit) {
        Plot s = SPORTS;
        kit.fill(s.minX(), FLOOR_Y, s.minZ(), s.maxX(), FLOOR_Y, s.maxZ(), Blocks.POLISHED_ANDESITE);
        kit.clear(s.minX(), G, s.minZ(), s.maxX(), G + 12, s.maxZ());

        // Muros externos do estádio
        kit.walls(s.minX(), G, s.minZ(), s.maxX(), G + 4, s.maxZ(), Blocks.STONE_BRICKS);

        // Entrada na parede leste
        kit.clear(s.maxX(), G, s.centerZ() - 2, s.maxX(), G + 4, s.centerZ() + 2);

        // Campo gramado central
        int fx1 = s.minX() + 20, fx2 = s.maxX() - 20;
        int fz1 = s.minZ() + 18, fz2 = s.maxZ() - 18;
        kit.fill(fx1, FLOOR_Y, fz1, fx2, FLOOR_Y, fz2, Blocks.GRASS_BLOCK);

        // Pista de Atletismo de terracota laranja ao redor do campo
        kit.ring(s.centerX(), FLOOR_Y, s.centerZ(), 24, 30, Blocks.ORANGE_TERRACOTTA);

        // Traves de gol / postes esportivos
        kit.pillar(fx1 + 2, G, G + 3, s.centerZ() - 3, Blocks.WHITE_CONCRETE);
        kit.pillar(fx1 + 2, G, G + 3, s.centerZ() + 3, Blocks.WHITE_CONCRETE);
        kit.fill(fx1 + 2, G + 4, s.centerZ() - 3, fx1 + 2, G + 4, s.centerZ() + 3, Blocks.WHITE_CONCRETE);

        kit.pillar(fx2 - 2, G, G + 3, s.centerZ() - 3, Blocks.WHITE_CONCRETE);
        kit.pillar(fx2 - 2, G, G + 3, s.centerZ() + 3, Blocks.WHITE_CONCRETE);
        kit.fill(fx2 - 2, G + 4, s.centerZ() - 3, fx2 - 2, G + 4, s.centerZ() + 3, Blocks.WHITE_CONCRETE);

        // Arquibancadas norte e sul
        for (int row = 0; row < 4; row++) {
            kit.fill(s.minX() + 10, G + row, s.minZ() + 4 + row, s.maxX() - 10, G + row, s.minZ() + 4 + row,
                    kit.stair(Blocks.SMOOTH_QUARTZ_STAIRS, Direction.SOUTH));
            kit.fill(s.minX() + 10, G + row, s.maxZ() - 4 - row, s.maxX() - 10, G + row, s.maxZ() - 4 - row,
                    kit.stair(Blocks.SMOOTH_QUARTZ_STAIRS, Direction.NORTH));
        }

        // 4 Torres de Refletores nos cantos
        int[][] lightPillars = {
                {s.minX() + 4, s.minZ() + 4}, {s.maxX() - 4, s.minZ() + 4},
                {s.minX() + 4, s.maxZ() - 4}, {s.maxX() - 4, s.maxZ() - 4}
        };
        for (int[] lp : lightPillars) {
            kit.pillar(lp[0], G, G + 10, lp[1], Blocks.POLISHED_BLACKSTONE_WALL);
            kit.fill(lp[0] - 1, G + 11, lp[1] - 1, lp[0] + 1, G + 11, lp[1] + 1, Blocks.SEA_LANTERN);
        }
    }

    // =========================================================================
    // 12. ALOJAMENTOS & DORMITÓRIOS (MASCULINO E FEMININO)
    // =========================================================================

    public static void buildDorms(BlockKit kit) {
        // Dormitório Masculino (Residência Oeste)
        buildDormBuilding(kit, DORM_MALE, false);
        // Dormitório Feminino (Residência Leste)
        buildDormBuilding(kit, DORM_FEMALE, true);
    }

    private static void buildDormBuilding(BlockKit kit, Plot plot, boolean female) {
        Block wallBlock = female ? Blocks.QUARTZ_BRICKS : Blocks.STONE_BRICKS;
        Block stairBlock = female ? Blocks.CHERRY_STAIRS : Blocks.DARK_OAK_STAIRS;
        Block bedBlock = female ? Blocks.PINK_BED : Blocks.BLUE_BED;

        kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.OAK_PLANKS);
        kit.clear(plot.minX() + 1, G, plot.minZ() + 1, plot.maxX() - 1, G + 14, plot.maxZ() - 1);
        kit.walls(plot.minX(), G, plot.minZ(), plot.maxX(), G + 12, plot.maxZ(), wallBlock);

        // Decks dos andares superiores (3 andares)
        int deck2 = G + 4;
        int deck3 = G + 8;
        kit.fill(plot.minX() + 1, deck2, plot.minZ() + 1, plot.maxX() - 1, deck2, plot.maxZ() - 1, Blocks.OAK_PLANKS);
        kit.fill(plot.minX() + 1, deck3, plot.minZ() + 1, plot.maxX() - 1, deck3, plot.maxZ() - 1, Blocks.OAK_PLANKS);

        // Porta voltada para o Hall
        int doorX = female ? plot.minX() : plot.maxX();
        kit.clear(doorX, G, plot.centerZ() - 1, doorX, G + 3, plot.centerZ() + 1);

        // Telhado de duas águas
        kit.gableAlongX(plot.minX() - 1, plot.maxX() + 1, plot.minZ() - 1, plot.maxZ() + 1, G + 13,
                stairBlock, wallBlock, wallBlock);

        // Quartos com camas, baús, guarda-roupas, mesas e iluminação em cada andar
        int[] floorLevels = {G, deck2 + 1, deck3 + 1};
        Block carpetBlock = female ? Blocks.PINK_CARPET : Blocks.LIGHT_BLUE_CARPET;
        for (int fy : floorLevels) {
            // Abertura para escada nos andares superiores
            if (fy > G) {
                kit.clear(plot.centerX() - 2, fy - 1, plot.minZ() + 2, plot.centerX() + 2, fy - 1, plot.minZ() + 5);
            }
            // Camas e mobília de cada quarto
            for (int z = plot.minZ() + 5; z <= plot.maxZ() - 5; z += 6) {
                // Quarto Lado Oeste
                kit.bed(plot.minX() + 2, fy, z, Direction.EAST, bedBlock);
                kit.set(plot.minX() + 1, fy, z, Blocks.CHEST); // Baú aos pés
                kit.set(plot.minX() + 4, fy, z, ModBlocks.CLASSROOM_DESK.get());
                kit.set(plot.minX() + 1, fy, z + 1, Blocks.BARREL); // Guarda-roupa
                kit.set(plot.minX() + 3, fy, z, carpetBlock);

                // Quarto Lado Leste
                kit.bed(plot.maxX() - 3, fy, z, Direction.WEST, bedBlock);
                kit.set(plot.maxX() - 1, fy, z, Blocks.CHEST); // Baú aos pés
                kit.set(plot.maxX() - 5, fy, z, ModBlocks.CLASSROOM_DESK.get());
                kit.set(plot.maxX() - 1, fy, z + 1, Blocks.BARREL); // Guarda-roupa
                kit.set(plot.maxX() - 3, fy, z, carpetBlock);
            }
            kit.set(plot.centerX(), fy + 3, plot.centerZ(), Blocks.LANTERN);
        }

        // Escadarias conectando os andares
        for (int step = 0; step < 4; step++) {
            kit.set(plot.centerX(), G + step, plot.minZ() + 2 + step, kit.stair(Blocks.OAK_STAIRS, Direction.SOUTH));
            kit.set(plot.centerX(), deck2 + 1 + step, plot.minZ() + 2 + step, kit.stair(Blocks.OAK_STAIRS, Direction.SOUTH));
        }

        // Lounge no piso térreo
        if (!female) {
            // Lareira com fogueira
            kit.set(plot.centerX(), G, plot.maxZ() - 2, Blocks.CAMPFIRE);
            kit.set(plot.centerX() - 1, G, plot.maxZ() - 2, Blocks.BRICKS);
            kit.set(plot.centerX() + 1, G, plot.maxZ() - 2, Blocks.BRICKS);
        } else {
            // Salão de chá com flores
            kit.set(plot.centerX(), G, plot.maxZ() - 2, Blocks.CHERRY_SLAB);
            kit.set(plot.centerX(), G + 1, plot.maxZ() - 2, Blocks.FLOWER_POT);
        }
    }

    // =========================================================================
    // 13. DOCAS DO DIRIGÍVEL & PORTAL DE RETORNO
    // =========================================================================

    public static void buildDocks(BlockKit kit) {
        Plot d = DOCKS;
        // Plataforma circular das docas
        kit.disc(0, FLOOR_Y, DOCKS_CENTER_Z, DOCKS_RADIUS, Blocks.POLISHED_DEEPSLATE);
        kit.ring(0, FLOOR_Y, DOCKS_CENTER_Z, DOCKS_RADIUS - 1, DOCKS_RADIUS, Blocks.DARK_OAK_PLANKS);
        kit.clear(-DOCKS_RADIUS, G, DOCKS_CENTER_Z - DOCKS_RADIUS, DOCKS_RADIUS, G + 30, DOCKS_CENTER_Z + DOCKS_RADIUS);

        // Mureta de proteção com aberturas de amarração
        kit.ring(0, G, DOCKS_CENTER_Z, DOCKS_RADIUS, DOCKS_RADIUS, Blocks.POLISHED_DEEPSLATE_WALL);

        // Plataforma do Portal de Retorno em (PORTAL_X=0, PORTAL_Z=196)
        kit.disc(PORTAL_X, FLOOR_Y, PORTAL_Z, 4, Blocks.LIGHT_BLUE_CONCRETE);
        kit.disc(PORTAL_X, FLOOR_Y, PORTAL_Z, 2, Blocks.SEA_LANTERN);
        kit.set(PORTAL_X, FLOOR_Y, PORTAL_Z, Blocks.GOLD_BLOCK);

        // 4 Pilares Arcanos ao redor do portal
        int[][] obelisks = {{3, 3}, {-3, 3}, {3, -3}, {-3, -3}};
        for (int[] ob : obelisks) {
            int ox = PORTAL_X + ob[0];
            int oz = PORTAL_Z + ob[1];
            kit.pillar(ox, G, G + 3, oz, Blocks.CHISELED_QUARTZ_BLOCK);
            kit.set(ox, G + 4, oz, Blocks.SEA_LANTERN);
        }

        // Dirigível Imperial Flutuante amarrado acima das docas (y = 80 a 95)
        int shipZ = 184;
        int shipY = G + 18;

        // Casco de madeira
        kit.fill(-3, shipY, shipZ - 10, 3, shipY, shipZ + 10, Blocks.OAK_PLANKS);
        kit.walls(-3, shipY + 1, shipZ - 10, 3, shipY + 1, shipZ + 10, Blocks.OAK_FENCE);

        // Balão de gás listrado azul e branco
        int balloonY = shipY + 8;
        kit.ellipsoid(0, balloonY, shipZ, 5, 4, 12, Blocks.WHITE_WOOL.defaultBlockState());
        // Faixas azuis decorativas no balão
        for (int bz = shipZ - 8; bz <= shipZ + 8; bz += 4) {
            kit.ring(0, balloonY, bz, 4, 5, Blocks.BLUE_WOOL);
        }

        // Correntes amarrando o balão ao casco
        kit.pillar(-3, shipY + 2, balloonY - 2, shipZ - 8, Blocks.CHAIN);
        kit.pillar(3, shipY + 2, balloonY - 2, shipZ - 8, Blocks.CHAIN);
        kit.pillar(-3, shipY + 2, balloonY - 2, shipZ + 8, Blocks.CHAIN);
        kit.pillar(3, shipY + 2, balloonY - 2, shipZ + 8, Blocks.CHAIN);
    }

    // =========================================================================
    // 14. PAISAGISMO PROCEDURAL (ÁRVORES E JARDINS)
    // =========================================================================

    public static void buildLandscaping(BlockKit kit, int zMin, int zMax) {
        Plot w = WALL;
        for (int x = w.minX() + 15; x <= w.maxX() - 15; x += 14) {
            for (int z = zMin; z <= zMax; z += 14) {
                // Jitter pseudo-aleatório determinístico baseado em x e z
                int jx = x + ((Math.abs(x * 31 + z * 17) % 5) - 2);
                int jz = z + ((Math.abs(x * 19 + z * 37) % 5) - 2);

                if (CampusLayout.isReserved(jx, jz)) continue;
                if (!w.contains(jx, jz, -12)) continue;

                // Tipo de árvore
                int seed = Math.abs(jx * 73 + jz * 97);
                if (seed % 10 < 5) {
                    // Carvalho
                    kit.tree(jx, G, jz, 4 + (seed % 2), Blocks.OAK_LOG, Blocks.OAK_LEAVES);
                } else if (seed % 10 < 8) {
                    // Bétula
                    kit.tree(jx, G, jz, 5 + (seed % 2), Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
                } else {
                    // Cerejeira
                    kit.tree(jx, G, jz, 4, Blocks.CHERRY_LOG, Blocks.CHERRY_LEAVES);
                }

                // Canteiro de flores perto do tronco
                kit.setIfAir(jx + 1, G, jz, Blocks.DANDELION);
                kit.setIfAir(jx - 1, G, jz, Blocks.POPPY);
            }
        }
    }

    // =========================================================================
    // 15. INSTALAÇÃO DOS TERMINAIS DE TRÂNSITO IMPERIAL
    // =========================================================================

    public static void placeTransitTerminals(BlockKit kit) {
        for (CampusWaypoint wp : CampusWaypoint.values()) {
            BlockPos termPos = wp.getTerminalPos();
            int x = termPos.getX();
            int y = termPos.getY();
            int z = termPos.getZ();

            // Base de pedra negra polida
            kit.set(x, y - 1, z, Blocks.POLISHED_BLACKSTONE);
            // Bloco do Terminal Tecnológico de Créditos
            kit.set(x, y, z, ModBlocks.IMPERIAL_TRANSIT_TERMINAL.get());
            // Espaço livre acima do terminal
            kit.clear(x, y + 1, z, x, y + 2, z);
        }
    }

    // =========================================================================
    // 16. GRANDES MONUMENTOS & SÍMBOLOS DE PODER (ESTÁTUAS E ESPADA SAGRADA)
    // =========================================================================

    public static void buildMonuments(BlockKit kit) {
        // Monumento Colossal da Espada Imperial de Titã cravada na rocha
        kit.giantSwordMonument(SWORD_MONUMENT_X, G, SWORD_MONUMENT_Z);

        // Mureta circular sagrada e tochas das almas em torno da Espada
        kit.ring(SWORD_MONUMENT_X, G, SWORD_MONUMENT_Z, 6, 6, Blocks.POLISHED_DEEPSLATE_WALL);
        kit.set(SWORD_MONUMENT_X + 6, G + 1, SWORD_MONUMENT_Z, Blocks.SOUL_TORCH);
        kit.set(SWORD_MONUMENT_X - 6, G + 1, SWORD_MONUMENT_Z, Blocks.SOUL_TORCH);
        kit.set(SWORD_MONUMENT_X, G + 1, SWORD_MONUMENT_Z + 6, Blocks.SOUL_TORCH);
        kit.set(SWORD_MONUMENT_X, G + 1, SWORD_MONUMENT_Z - 6, Blocks.SOUL_TORCH);

        // 4 Grandes Monólitos Rúnicos Flutuantes nas vias principais
        kit.arcaneMonolith(-40, G, 0);   // Alameda da Biblioteca
        kit.arcaneMonolith(40, G, 0);    // Alameda do Refeitório
        kit.arcaneMonolith(0, G, 140);   // Avenida das Docas
        kit.arcaneMonolith(0, G, -25);   // Avenida do Palácio Central
    }

    // =========================================================================
    // 17. RELEVO E TERRAÇOS AJARDINADOS (DESNÍVEIS E VIDA NO CAMPUS)
    // =========================================================================

    public static void buildTerraces(BlockKit kit) {
        // Terraço elevado do Jardim Oeste (entre Biblioteca e Laboratório)
        kit.fill(-80, G, 8, -60, G, 14, Blocks.GRASS_BLOCK);
        kit.walls(-80, G, 8, -60, G, 14, Blocks.STONE_BRICK_SLAB);
        for (int x = -78; x <= -62; x += 4) {
            kit.setIfAir(x, G + 1, 10, Blocks.ROSE_BUSH);
            kit.setIfAir(x, G + 1, 12, Blocks.LILAC);
        }

        // Terraço elevado do Jardim Leste (entre Refeitório e Estufa)
        kit.fill(60, G, 8, 80, G, 14, Blocks.GRASS_BLOCK);
        kit.walls(60, G, 8, 80, G, 14, Blocks.STONE_BRICK_SLAB);
        for (int x = 62; x <= 78; x += 4) {
            kit.setIfAir(x, G + 1, 10, Blocks.PEONY);
            kit.setIfAir(x, G + 1, 12, Blocks.FLOWERING_AZALEA);
        }
    }

    // =========================================================================
    // 18. CORDILHEIRA MONTANHOSA EXTERNA DO VALE IMPERIAL (BARREIRA CÊNICA)
    // =========================================================================

    public static void buildMountains(BlockKit kit, int zone) {
        if (zone == 0) {
            // Cordilheira Norte (atrás do Grand Hall e da Torre do Relógio)
            kit.mountainRidge(-260, -225, 260, -171, -171, true, false);
        } else if (zone == 1) {
            // Cordilheira Sul (ao lado do Portão Sul e atrás das Docas)
            // Deixa uma fenda/canyon no meio para o portão e estrada imperial (x = -15 a 15)
            kit.mountainRidge(-260, 251, -16, 305, 251, true, true);
            kit.mountainRidge(16, 251, 260, 305, 251, true, true);
        } else if (zone == 2) {
            // Cordilheira Oeste (ao lado da Biblioteca, Lab e Arena)
            kit.mountainRidge(-260, -175, -211, 255, -211, false, false);
        } else if (zone == 3) {
            // Cordilheira Leste (ao lado do Refeitório, Estufa e Dojo)
            kit.mountainRidge(211, -175, 260, 255, 211, false, true);
        }
    }

    // =========================================================================
    // 19. RELEVOS E COLINAS SUAVES INTERNAS DO CAMPUS
    // =========================================================================

    public static void buildRelief(BlockKit kit, int zMin, int zMax) {
        Plot w = WALL;
        for (int x = w.minX() + 10; x <= w.maxX() - 10; x += 3) {
            for (int z = zMin; z <= zMax; z += 3) {
                if (z < w.minZ() + 10 || z > w.maxZ() - 10) continue;
                if (CampusLayout.isReserved(x, z)) continue;

                int seed = Math.abs(x * 37 + z * 19);
                if (seed % 10 < 3) {
                    // Elevação suave de 1 bloco
                    kit.disc(x, G, z, 3, Blocks.GRASS_BLOCK);
                    kit.disc(x, FLOOR_Y, z, 3, Blocks.DIRT);
                    kit.setIfAir(x, G + 1, z, (seed % 2 == 0) ? Blocks.DANDELION : Blocks.POPPY);
                }
            }
        }
    }

    // =========================================================================
    // 20. JARDINS VIVOS, GAZEBOS, LAGOS E PIQUENIQUES
    // =========================================================================

    public static void buildGardensAndPonds(BlockKit kit) {
        // 3 Lagos Cenográficos com vitórias-régias e cana-de-açúcar
        createPond(kit, -150, 40);
        createPond(kit, 150, 40);
        createPond(kit, -40, 145);

        // 2 Gazebos de descanso com lanternas suspensas
        createGazebo(kit, 155, -20);
        createGazebo(kit, -155, -20);

        // Áreas de piquenique com toalhas xadrez e bolos
        createPicnic(kit, 35, -30);
        createPicnic(kit, -35, 130);
    }

    private static void createPond(BlockKit kit, int cx, int cz) {
        kit.disc(cx, FLOOR_Y, cz, 4, Blocks.CLAY);
        kit.disc(cx, FLOOR_Y, cz, 3, Blocks.WATER);
        kit.ring(cx, FLOOR_Y, cz, 4, 5, Blocks.SAND);
        kit.setIfAir(cx - 1, G, cz, Blocks.LILY_PAD);
        kit.setIfAir(cx + 1, G, cz + 1, Blocks.LILY_PAD);
        kit.setIfAir(cx + 4, G, cz, Blocks.SUGAR_CANE);
        kit.setIfAir(cx - 4, G, cz, Blocks.SUGAR_CANE);
        kit.setIfAir(cx + 5, G, cz + 2, Blocks.MOSSY_COBBLESTONE);
    }

    private static void createGazebo(BlockKit kit, int cx, int cz) {
        kit.disc(cx, FLOOR_Y, cz, 3, Blocks.STONE_BRICKS);
        for (int dx : new int[]{-3, 3}) {
            for (int dz : new int[]{-3, 3}) {
                kit.pillar(cx + dx, G, G + 3, cz + dz, Blocks.DARK_OAK_LOG);
            }
        }
        kit.hipRoof(cx - 4, cz - 4, cx + 4, cz + 4, G + 4, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS, Blocks.GLOWSTONE);
        kit.set(cx, G, cz, Blocks.OAK_FENCE);
        kit.set(cx, G + 1, cz, Blocks.SPRUCE_PRESSURE_PLATE);
        kit.set(cx - 1, G, cz + 1, kit.stair(Blocks.OAK_STAIRS, Direction.NORTH));
        kit.set(cx + 1, G, cz + 1, kit.stair(Blocks.OAK_STAIRS, Direction.NORTH));
        kit.set(cx, G + 3, cz, Blocks.LANTERN.defaultBlockState().setValue(net.minecraft.world.level.block.LanternBlock.HANGING, true));
    }

    private static void createPicnic(BlockKit kit, int cx, int cz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                kit.set(cx + dx, G, cz + dz, ((dx + dz) % 2 == 0) ? Blocks.RED_CARPET : Blocks.WHITE_CARPET);
            }
        }
        kit.set(cx + 2, G, cz, Blocks.BARREL);
        kit.set(cx, G, cz, Blocks.CAKE);
    }

    // =========================================================================
    // 21. TRILHA DO PORTÃO SUL E MIRANTE DO FIM DO VALE
    // =========================================================================

    public static void buildSouthPass(BlockKit kit) {
        // Trilha de pedra e cascalho
        kit.fill(-2, FLOOR_Y, 251, 2, FLOOR_Y, 296, Blocks.STONE_BRICKS);
        kit.fill(-3, FLOOR_Y, 251, -3, FLOOR_Y, 296, Blocks.GRAVEL);
        kit.fill(3, FLOOR_Y, 251, 3, FLOOR_Y, 296, Blocks.GRAVEL);
        for (int z = 258; z <= 294; z += 12) {
            kit.lampPost(-4, G, z);
            kit.lampPost(4, G, z);
        }

        // Arco de boas-vindas
        kit.pillar(-4, G, G + 5, 254, Blocks.POLISHED_BLACKSTONE_BRICKS);
        kit.pillar(4, G, G + 5, 254, Blocks.POLISHED_BLACKSTONE_BRICKS);
        kit.fill(-4, G + 6, 254, 4, G + 6, 254, Blocks.POLISHED_BLACKSTONE_BRICKS);
        kit.set(0, G + 7, 254, Blocks.SEA_LANTERN);

        // Mirante circular
        kit.disc(0, FLOOR_Y, 274, 8, Blocks.POLISHED_DEEPSLATE);
        kit.ring(0, FLOOR_Y, 274, 7, 8, Blocks.STONE_BRICKS);
        kit.ring(0, G, 274, 7, 7, Blocks.POLISHED_DEEPSLATE_WALL);
        kit.clear(-1, G, 266, 1, G, 267);

        // Bancos e floreiras
        kit.set(-2, G, 278, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.SOUTH));
        kit.set(2, G, 278, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.SOUTH));
        kit.lampPost(-5, G, 272);
        kit.lampPost(5, G, 272);
        kit.setIfAir(-4, G, 276, Blocks.POTTED_FLOWERING_AZALEA);
        kit.setIfAir(4, G, 276, Blocks.POTTED_FLOWERING_AZALEA);
    }

    // =========================================================================
    // 22. BARREIRA FÍSICA INVISÍVEL NO LIMITE DAS MONTANHAS
    // =========================================================================

    public static void buildBarrier(BlockKit kit) {
        int half = BORDER_SIZE / 2;
        int minZ = BORDER_CENTER_Z - half;
        int maxZ = BORDER_CENTER_Z + half;
        int minX = BORDER_CENTER_X - half;
        int maxX = BORDER_CENTER_X + half;

        // Paredes de BARRIER bloqueando a saída da dimensão do chão ao céu
        kit.fill(minX, 62, minZ, maxX, 150, minZ, Blocks.BARRIER);
        kit.fill(minX, 62, maxZ, maxX, 150, maxZ, Blocks.BARRIER);
        kit.fill(minX, 62, minZ, minX, 150, maxZ, Blocks.BARRIER);
        kit.fill(maxX, 62, minZ, maxX, 150, maxZ, Blocks.BARRIER);

        // Pedras de fronteira luminosas na trilha do sul
        kit.set(0, FLOOR_Y, maxZ - 4, Blocks.POLISHED_BLACKSTONE);
        kit.set(0, G, maxZ - 4, Blocks.SEA_LANTERN);
    }
}
