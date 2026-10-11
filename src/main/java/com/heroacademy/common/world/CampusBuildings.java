package com.heroacademy.common.world;

import com.heroacademy.common.academic.CampusWaypoint;
import com.heroacademy.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

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

        // 1. Piso monumental xadrez imperial
        kit.fill(h.minX(), FLOOR_Y, h.minZ(), h.maxX(), FLOOR_Y, h.maxZ(), Blocks.POLISHED_DEEPSLATE);
        for (int x = h.minX() + 1; x <= h.maxX() - 1; x++) {
            for (int z = h.minZ() + 1; z <= h.maxZ() - 1; z++) {
                if ((x + z) % 2 == 0) {
                    kit.set(x, FLOOR_Y, z, Blocks.SMOOTH_QUARTZ);
                }
            }
        }

        kit.clear(h.minX() + 1, G, h.minZ() + 1, h.maxX() - 1, G + 14, h.maxZ() - 1);

        // 2. Paredes imperiais com plinto de embasamento de ardósia
        kit.walls(h.minX(), G, h.minZ(), h.maxX(), G, h.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(h.minX(), G + 1, h.minZ(), h.maxX(), G + 14, h.maxZ(), Blocks.QUARTZ_BRICKS);

        // 3. Pilastras e contrafortes salientes (avançando 1 bloco para fora) para profundidade 3D real
        for (int x = h.minX(); x <= h.maxX(); x += 8) {
            // Pilastras sul salientes
            kit.pillar(x, G, G + 14, h.maxZ() + 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 14, h.maxZ() + 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH, Half.TOP));
            // Pilastras norte salientes
            kit.pillar(x, G, G + 14, h.minZ() - 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 14, h.minZ() - 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH, Half.TOP));
        }

        // 4. Janelas altas emolduradas com peitoril e floreiras de azaleias floridas
        for (int x = h.minX() + 4; x <= h.maxX() - 4; x += 8) {
            if (x >= -6 && x <= 6) continue; // Pula a área do portal central
            kit.framedWindow(x, G + 2, h.maxZ(), 7, Direction.SOUTH, Blocks.QUARTZ_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
            kit.setIfAir(x, G + 1, h.maxZ() + 1, Blocks.FLOWERING_AZALEA);

            kit.framedWindow(x, G + 2, h.minZ(), 7, Direction.NORTH, Blocks.QUARTZ_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
            kit.setIfAir(x, G + 1, h.minZ() - 1, Blocks.FLOWERING_AZALEA);
        }

        // 5. Pórtico Monumental de Entrada Sul (saliente para a praça)
        kit.clear(-4, G, h.maxZ(), 4, G + 7, h.maxZ());
        kit.entryPortico(0, G, h.maxZ(), Direction.SOUTH, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);
        // Degraus nobres descendo para a avenida
        kit.fill(-3, G, h.maxZ() + 2, 3, G, h.maxZ() + 2, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH));

        // Portais laterais (para os caminhos dos dormitórios)
        kit.clear(h.minX(), G, -62, h.minX(), G + 4, -58);
        kit.clear(h.maxX(), G, -62, h.maxX(), G + 4, -58);

        // Passagem ao fundo para a Torre do Relógio
        kit.clear(-3, G, h.minZ(), 3, G + 5, h.minZ());

        // 6. Telhado de duas águas com beirais pronunciados e escadas invertidas
        kit.overhangGableRoofAlongX(h.minX() - 1, h.maxX() + 1, h.minZ() - 1, h.maxZ() + 1, G + 15,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_TILES);

        // Lucarnas (dormers) no telhado voltadas para o sul
        kit.dormerWindow(-16, G + 16, h.maxZ() - 2, Direction.SOUTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        kit.dormerWindow(-8, G + 17, h.maxZ() - 3, Direction.SOUTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        kit.dormerWindow(8, G + 17, h.maxZ() - 3, Direction.SOUTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        kit.dormerWindow(16, G + 16, h.maxZ() - 2, Direction.SOUTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);

        // 7. 4 Torres de Canto Esbeltas com Sacadas e Para-raios
        int[][] corners = {
                {h.minX() - 2, h.minZ() - 2}, {h.maxX() - 4, h.minZ() - 2},
                {h.minX() - 2, h.maxZ() - 4}, {h.maxX() - 4, h.maxZ() - 4}
        };
        for (int[] c : corners) {
            kit.walls(c[0], G, c[1], c[0] + 6, G + 22, c[1] + 6, Blocks.QUARTZ_BRICKS);
            // Cintas horizontais e sacadas
            kit.walls(c[0] - 1, G + 14, c[1] - 1, c[0] + 7, G + 14, c[1] + 7, Blocks.STONE_BRICK_SLAB);
            kit.hipRoof(c[0] - 1, c[1] - 1, c[0] + 7, c[1] + 7, G + 23,
                    Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);
            kit.set(c[0] + 3, G + 28, c[1] + 3, Blocks.LIGHTNING_ROD);
        }

        // 8. Interior Nobre: Colunatas duplas, Lustres e Tapete Imperial
        for (int z = h.minZ() + 4; z <= h.maxZ() - 4; z += 6) {
            kit.pillar(-8, G, G + 13, z, Blocks.QUARTZ_PILLAR);
            kit.pillar(8, G, G + 13, z, Blocks.QUARTZ_PILLAR);
            kit.set(-8, G + 13, z, kit.stair(Blocks.QUARTZ_STAIRS, Direction.EAST, Half.TOP));
            kit.set(8, G + 13, z, kit.stair(Blocks.QUARTZ_STAIRS, Direction.WEST, Half.TOP));
            kit.chandelier(-8, G + 13, 2, z);
            kit.chandelier(8, G + 13, 2, z);
        }

        // Tapete Vermelho central com bordas amarelas
        kit.fill(-2, G, h.minZ() + 6, 2, G, h.maxZ() - 1, Blocks.RED_CARPET);
        kit.fill(-3, G, h.minZ() + 6, -3, G, h.maxZ() - 1, Blocks.YELLOW_CARPET);
        kit.fill(3, G, h.minZ() + 6, 3, G, h.maxZ() - 1, Blocks.YELLOW_CARPET);

        // Trono Imperial elevado ao fundo
        kit.fill(-4, G, h.minZ() + 2, 4, G, h.minZ() + 5, Blocks.SMOOTH_QUARTZ);
        kit.fill(-3, G + 1, h.minZ() + 2, 3, G + 1, h.minZ() + 4, Blocks.SMOOTH_QUARTZ);
        kit.set(0, G + 2, h.minZ() + 3, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH));
        kit.fill(-1, G + 1, h.minZ() + 2, 1, G + 5, h.minZ() + 2, Blocks.GOLD_BLOCK);
        kit.set(-3, G + 2, h.minZ() + 3, Blocks.ENCHANTING_TABLE);
        kit.set(3, G + 2, h.minZ() + 3, Blocks.ENCHANTING_TABLE);
    }

    // =========================================================================
    // 4. TORRE DO RELÓGIO IMPERIAL
    // =========================================================================

    public static void buildClockTower(BlockKit kit) {
        Plot t = CLOCK_TOWER;
        kit.fill(t.minX(), FLOOR_Y, t.minZ(), t.maxX(), FLOOR_Y, t.maxZ(), Blocks.POLISHED_DEEPSLATE);
        kit.clear(t.minX() + 1, G, t.minZ() + 1, t.maxX() - 1, G + 50, t.maxZ() - 1);

        // Embasamento mais largo de ardósia polida
        kit.walls(t.minX() - 1, G, t.minZ() - 1, t.maxX() + 1, G + 3, t.maxZ() + 1, Blocks.DEEPSLATE_BRICKS);
        kit.walls(t.minX(), G + 4, t.minZ(), t.maxX(), G + 46, t.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilastras salientes nos 4 cantos
        kit.pillar(t.minX(), G, G + 46, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G, G + 46, t.minZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.minX(), G, G + 46, t.maxZ(), Blocks.QUARTZ_PILLAR);
        kit.pillar(t.maxX(), G, G + 46, t.maxZ(), Blocks.QUARTZ_PILLAR);

        // Cintas horizontais a cada 14 blocos
        for (int cy : new int[]{G + 14, G + 28, G + 42}) {
            kit.walls(t.minX() - 1, cy, t.minZ() - 1, t.maxX() + 1, cy, t.maxZ() + 1, Blocks.STONE_BRICK_SLAB);
        }

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
            kit.clear(-1, dy, ladderZ, 1, dy, ladderZ + 1);
        }

        // Mostradores do Relógio nas 4 faces com moldura dourada no nível y = 104
        int clockY = G + 40;
        int cx = t.centerX();
        int cz = t.centerZ();
        kit.clockDisc(cx, clockY, t.minZ(), Direction.SOUTH);
        kit.clockDisc(cx, clockY, t.maxZ(), Direction.NORTH);
        kit.clockDisc(t.minX(), clockY, cz, Direction.EAST);
        kit.clockDisc(t.maxX(), clockY, cz, Direction.WEST);

        // Campanário aberto no topo (y = G + 47 a G + 54) com arcos
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

        // 1. Piso de madeira nobre para estudo e silêncio
        kit.fill(l.minX(), FLOOR_Y, l.minZ(), l.maxX(), FLOOR_Y, l.maxZ(), Blocks.SPRUCE_PLANKS);
        // Moldura perimetral de carvalho escuro
        kit.walls(l.minX(), FLOOR_Y, l.minZ(), l.maxX(), FLOOR_Y, l.maxZ(), Blocks.DARK_OAK_PLANKS);

        kit.clear(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 1, G + 14, l.maxZ() - 1);

        // 2. Paredes imperiais com plinto de embasamento de ardósia
        kit.walls(l.minX(), G, l.minZ(), l.maxX(), G, l.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(l.minX(), G + 1, l.minZ(), l.maxX(), G + 12, l.maxZ(), Blocks.QUARTZ_BRICKS);

        // 3. Pilastras salientes (profundidade 3D) a cada 6 blocos
        for (int x = l.minX(); x <= l.maxX(); x += 6) {
            kit.pillar(x, G, G + 12, l.minZ() - 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, l.minZ() - 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH, Half.TOP));
            kit.pillar(x, G, G + 12, l.maxZ() + 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, l.maxZ() + 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH, Half.TOP));
        }

        // 4. Janelas altas emolduradas com floreiras de azaleias floridas
        for (int z = l.minZ() + 6; z <= l.maxZ() - 6; z += 6) {
            kit.framedWindow(l.minX(), G + 2, z, 5, Direction.WEST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
            kit.setIfAir(l.minX() - 1, G + 1, z, Blocks.FLOWERING_AZALEA);

            if (z < -4 || z > 4) { // Evita sobrepor a entrada
                kit.framedWindow(l.maxX(), G + 2, z, 5, Direction.EAST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
                kit.setIfAir(l.maxX() + 1, G + 1, z, Blocks.FLOWERING_AZALEA);
            }
        }

        // 5. Pórtico de Entrada na parede leste voltado para a Alameda Central
        kit.clear(l.maxX(), G, -2, l.maxX(), G + 5, 2);
        kit.entryPortico(0, G, l.maxX(), Direction.EAST, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);
        kit.fill(l.maxX() + 2, G, -2, l.maxX() + 2, G, 2, kit.stair(Blocks.QUARTZ_STAIRS, Direction.EAST));

        // 6. Telhado de duas águas com beirais pronunciados e claraboia central envidraçada
        kit.overhangGableRoofAlongX(l.minX() - 1, l.maxX() + 1, l.minZ() - 1, l.maxZ() + 1, G + 13,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_TILES);

        // Claraboia envidraçada no átrio central
        int cx = l.centerX();
        int cz = l.centerZ();
        kit.clear(cx - 6, G + 15, cz - 4, cx + 6, G + 15, cz + 4);
        kit.fill(cx - 6, G + 15, cz - 4, cx + 6, G + 15, cz + 4, Blocks.GLASS);
        kit.walls(cx - 7, G + 15, cz - 5, cx + 7, G + 15, cz + 5, Blocks.QUARTZ_SLAB);

        // 7. Mezanino do Segundo Andar (galeria perimetral de leitura)
        int mezY = G + 5;
        kit.fill(l.minX() + 1, mezY, l.minZ() + 1, l.minX() + 4, mezY, l.maxZ() - 1, kit.slab(Blocks.SPRUCE_SLAB, SlabType.TOP));
        kit.fill(l.minX() + 1, mezY, l.minZ() + 1, l.maxX() - 1, mezY, l.minZ() + 4, kit.slab(Blocks.SPRUCE_SLAB, SlabType.TOP));
        kit.fill(l.minX() + 1, mezY, l.maxZ() - 4, l.maxX() - 1, mezY, l.maxZ() - 1, kit.slab(Blocks.SPRUCE_SLAB, SlabType.TOP));
        // Guarda-corpo do mezanino com barras de ferro
        kit.fill(l.minX() + 4, mezY + 1, l.minZ() + 4, l.minX() + 4, mezY + 1, l.maxZ() - 4, Blocks.IRON_BARS);
        kit.fill(l.minX() + 4, mezY + 1, l.minZ() + 4, l.maxX() - 4, mezY + 1, l.minZ() + 4, Blocks.IRON_BARS);
        kit.fill(l.minX() + 4, mezY + 1, l.maxZ() - 4, l.maxX() - 4, mezY + 1, l.maxZ() - 4, Blocks.IRON_BARS);

        // Escadaria de madeira para o mezanino
        for (int step = 0; step < 5; step++) {
            kit.set(l.maxX() - 4, G + step, l.maxZ() - 5 - step, kit.stair(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
        }

        // 8. Estantes de livros em 2 andares
        kit.fill(l.minX() + 1, G, l.minZ() + 1, l.minX() + 1, G + 4, l.maxZ() - 1, Blocks.BOOKSHELF);
        kit.fill(l.minX() + 1, mezY + 1, l.minZ() + 1, l.minX() + 1, mezY + 4, l.maxZ() - 1, Blocks.BOOKSHELF);
        kit.fill(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 5, G + 4, l.minZ() + 1, Blocks.BOOKSHELF);
        kit.fill(l.minX() + 1, G, l.maxZ() - 1, l.maxX() - 5, G + 4, l.maxZ() - 1, Blocks.BOOKSHELF);

        // Estantes duplas centrais formando corredores de estudo
        for (int z = l.minZ() + 8; z <= l.maxZ() - 8; z += 6) {
            kit.fill(l.minX() + 6, G, z, l.maxX() - 8, G + 3, z, Blocks.BOOKSHELF);
            kit.set(l.minX() + 6, G + 4, z, Blocks.LANTERN);
            kit.set(l.maxX() - 8, G + 4, z, Blocks.LANTERN);
        }

        // 9. Mesas de leitura compridas no térreo com cadeiras, púlpitos e lanternas
        for (int x = l.minX() + 8; x <= l.maxX() - 10; x += 6) {
            kit.set(x, G, 0, Blocks.DARK_OAK_FENCE);
            kit.set(x, G + 1, 0, Blocks.SPRUCE_PRESSURE_PLATE);
            kit.set(x, G, 1, kit.stair(Blocks.SPRUCE_STAIRS, Direction.NORTH));
            kit.set(x, G, -1, kit.stair(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
            kit.set(x + 1, G, 0, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.EAST));
        }

        // Tapete-corredor central azul e dourado
        kit.fill(l.minX() + 6, G, 0, l.maxX() - 2, G, 0, Blocks.BLUE_CARPET);

        // Seção arcana com tomos antigos e mesas de encantamento
        kit.set(l.minX() + 2, G, -8, Blocks.ENCHANTING_TABLE);
        kit.set(l.minX() + 2, G, 8, Blocks.ENCHANTING_TABLE);
        kit.set(l.minX() + 2, G + 1, -8, Blocks.AMETHYST_CLUSTER);
        kit.set(l.minX() + 2, G + 1, 8, Blocks.AMETHYST_CLUSTER);
        kit.set(l.minX() + 1, G, -8, Blocks.CHISELED_BOOKSHELF);
        kit.set(l.minX() + 1, G, 8, Blocks.CHISELED_BOOKSHELF);

        // Lustres suspensos do átrio
        kit.chandelier(cx - 4, G + 13, 3, cz);
        kit.chandelier(cx + 4, G + 13, 3, cz);
    }

    // =========================================================================
    // 6. GRANDE REFEITÓRIO IMPERIAL (FOOD HALL)
    // =========================================================================

    public static void buildFoodHall(BlockKit kit) {
        Plot f = FOOD_HALL;

        // 1. Piso nobre de tábuas de carvalho escuro e carvalho
        kit.fill(f.minX(), FLOOR_Y, f.minZ(), f.maxX(), FLOOR_Y, f.maxZ(), Blocks.DARK_OAK_PLANKS);
        for (int x = f.minX() + 1; x <= f.maxX() - 1; x += 2) {
            for (int z = f.minZ() + 1; z <= f.maxZ() - 1; z += 2) {
                kit.set(x, FLOOR_Y, z, Blocks.OAK_PLANKS);
            }
        }

        kit.clear(f.minX() + 1, G, f.minZ() + 1, f.maxX() - 1, G + 14, f.maxZ() - 1);

        // 2. Paredes imperiais com plinto de embasamento de ardósia
        kit.walls(f.minX(), G, f.minZ(), f.maxX(), G, f.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(f.minX(), G + 1, f.minZ(), f.maxX(), G + 12, f.maxZ(), Blocks.QUARTZ_BRICKS);

        // 3. Pilastras salientes (profundidade 3D) a cada 6 blocos
        for (int x = f.minX(); x <= f.maxX(); x += 6) {
            kit.pillar(x, G, G + 12, f.minZ() - 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, f.minZ() - 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH, Half.TOP));
            kit.pillar(x, G, G + 12, f.maxZ() + 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, f.maxZ() + 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH, Half.TOP));
        }

        // 4. Janelas altas emolduradas com peitoris e floreiras
        for (int z = f.minZ() + 6; z <= f.maxZ() - 6; z += 6) {
            kit.framedWindow(f.maxX(), G + 2, z, 5, Direction.EAST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
            kit.setIfAir(f.maxX() + 1, G + 1, z, Blocks.FLOWERING_AZALEA);

            if (z < -4 || z > 4) { // Evita sobrepor a entrada
                kit.framedWindow(f.minX(), G + 2, z, 5, Direction.WEST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
                kit.setIfAir(f.minX() - 1, G + 1, z, Blocks.FLOWERING_AZALEA);
            }
        }

        // 5. Pórtico de Entrada na parede oeste voltado para a Alameda Central
        kit.clear(f.minX(), G, -2, f.minX(), G + 5, 2);
        kit.entryPortico(0, G, f.minX(), Direction.WEST, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);
        kit.fill(f.minX() - 2, G, -2, f.minX() - 2, G, 2, kit.stair(Blocks.QUARTZ_STAIRS, Direction.WEST));

        // 6. Telhado de telhas de ardósia com beirais pronunciados
        kit.overhangGableRoofAlongX(f.minX() - 1, f.maxX() + 1, f.minZ() - 1, f.maxZ() + 1, G + 13,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_TILES);

        // 7. Chaminé alta de pedra na parede norte (atravessando o telhado)
        int chimX = f.maxX() - 6;
        kit.pillar(chimX - 1, G, G + 18, f.minZ(), Blocks.STONE_BRICKS);
        kit.pillar(chimX + 1, G, G + 18, f.minZ(), Blocks.STONE_BRICKS);
        kit.pillar(chimX, G, G + 18, f.minZ() - 1, Blocks.STONE_BRICKS);
        kit.set(chimX, G + 19, f.minZ(), Blocks.CAMPFIRE);
        kit.set(chimX, G + 20, f.minZ(), kit.slab(Blocks.STONE_BRICK_SLAB));

        // Lareira monumental na parede norte interna
        kit.clear(chimX - 1, G + 1, f.minZ() + 1, chimX + 1, G + 3, f.minZ() + 1);
        kit.set(chimX, G, f.minZ() + 1, Blocks.CAMPFIRE);
        kit.set(chimX, G + 1, f.minZ() + 1, Blocks.IRON_BARS);
        kit.set(chimX - 1, G + 3, f.minZ() + 1, kit.stair(Blocks.STONE_BRICK_STAIRS, Direction.EAST, Half.TOP));
        kit.set(chimX + 1, G + 3, f.minZ() + 1, kit.stair(Blocks.STONE_BRICK_STAIRS, Direction.WEST, Half.TOP));
        kit.set(chimX, G + 4, f.minZ() + 1, Blocks.LANTERN);

        // 8. Teto da nave com vigas expostas de madeira
        for (int x = f.minX() + 4; x <= f.maxX() - 4; x += 6) {
            kit.fill(x, G + 12, f.minZ() + 1, x, G + 12, f.maxZ() - 1, Blocks.STRIPPED_DARK_OAK_LOG);
            kit.chandelier(x, G + 12, 2, f.centerZ() - 6);
            kit.chandelier(x, G + 12, 2, f.centerZ() + 6);
        }

        // 9. 3 Grandes mesas de banquete com bancos, toalhas, bolos, velas e flores
        int[] tableZ = {-18, -11, -4};
        for (int tz : tableZ) {
            // Mesa de banquete elevada
            kit.fill(f.minX() + 6, G, tz, f.maxX() - 14, G, tz, kit.slab(Blocks.SPRUCE_SLAB, SlabType.TOP));
            // Bancos de escada em ambos os lados
            for (int x = f.minX() + 6; x <= f.maxX() - 14; x += 2) {
                kit.set(x, G, tz + 1, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.NORTH));
                kit.set(x, G, tz - 1, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.SOUTH));
            }
            // Toalha de mesa em carpete, bolos, velas e floreiras
            for (int x = f.minX() + 8; x <= f.maxX() - 16; x += 3) {
                if ((x + tz) % 3 == 0) {
                    kit.set(x, G + 1, tz, Blocks.CAKE);
                } else if ((x + tz) % 3 == 1) {
                    kit.set(x, G + 1, tz, Blocks.CANDLE);
                } else {
                    kit.set(x, G + 1, tz, Blocks.FLOWER_POT);
                }
            }
        }

        // 10. Cozinha completa e despensa na ala leste
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
    }

    // =========================================================================
    // 7. LABORATÓRIO DE CIÊNCIA MÁGICA & ALQUIMIA
    // =========================================================================

    public static void buildLab(BlockKit kit) {
        Plot l = LAB;

        // 1. Piso técnico e místico de pedra negra e ardósia polida
        kit.fill(l.minX(), FLOOR_Y, l.minZ(), l.maxX(), FLOOR_Y, l.maxZ(), Blocks.POLISHED_DEEPSLATE);
        for (int x = l.minX() + 2; x <= l.maxX() - 2; x += 4) {
            for (int z = l.minZ() + 2; z <= l.maxZ() - 2; z += 4) {
                kit.set(x, FLOOR_Y, z, Blocks.POLISHED_BLACKSTONE);
            }
        }

        kit.clear(l.minX() + 1, G, l.minZ() + 1, l.maxX() - 1, G + 12, l.maxZ() - 1);

        // 2. Paredes imperiais com plinto de ardósia
        kit.walls(l.minX(), G, l.minZ(), l.maxX(), G, l.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(l.minX(), G + 1, l.minZ(), l.maxX(), G + 10, l.maxZ(), Blocks.QUARTZ_BRICKS);

        // 3. Pilastras salientes (profundidade 3D) a cada 6 blocos
        for (int x = l.minX(); x <= l.maxX(); x += 6) {
            kit.pillar(x, G, G + 10, l.minZ() - 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 10, l.minZ() - 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH, Half.TOP));
            kit.pillar(x, G, G + 10, l.maxZ() + 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 10, l.maxZ() + 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH, Half.TOP));
        }

        // 4. Janelas emolduradas com floreiras
        for (int z = l.minZ() + 6; z <= l.maxZ() - 6; z += 6) {
            kit.framedWindow(l.minX(), G + 2, z, 5, Direction.WEST, Blocks.QUARTZ_STAIRS, Blocks.PURPLE_STAINED_GLASS_PANE);
            kit.framedWindow(l.maxX(), G + 2, z, 5, Direction.EAST, Blocks.QUARTZ_STAIRS, Blocks.PURPLE_STAINED_GLASS_PANE);
        }

        // 5. Entrada na parede norte com pórtico
        kit.clear(l.centerX() - 2, G, l.minZ(), l.centerX() + 2, G + 4, l.minZ());
        kit.entryPortico(l.centerX(), G, l.minZ(), Direction.NORTH, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);
        kit.fill(l.centerX() - 2, G, l.minZ() - 2, l.centerX() + 2, G, l.minZ() - 2, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH));

        // 6. Cúpula e Observatório de Vidro nervurado no topo
        int cx = l.centerX();
        int cz = l.centerZ();
        kit.fill(l.minX(), G + 11, l.minZ(), l.maxX(), G + 11, l.maxZ(), Blocks.SMOOTH_QUARTZ);
        kit.walls(l.minX() - 1, G + 12, l.minZ() - 1, l.maxX() + 1, G + 12, l.maxZ() + 1, Blocks.STONE_BRICK_WALL);

        kit.clear(cx - 5, G + 11, cz - 5, cx + 5, G + 11, cz + 5);
        kit.disc(cx, G + 11, cz, 5, Blocks.GLASS);
        kit.disc(cx, G + 12, cz, 4, Blocks.GLASS);
        kit.disc(cx, G + 13, cz, 3, Blocks.GLASS);
        kit.disc(cx, G + 14, cz, 2, Blocks.SEA_LANTERN);

        // 7. Bancadas de Alquimia em U com suportes de poções, caldeirões e ervas
        for (int x = l.minX() + 4; x <= l.maxX() - 4; x += 4) {
            kit.set(x, G, l.minZ() + 3, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
            kit.set(x, G + 1, l.minZ() + 3, Blocks.BREWING_STAND);
            kit.set(x, G, l.maxZ() - 3, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
            kit.set(x, G + 1, l.maxZ() - 3, Blocks.WATER_CAULDRON);
        }

        // Estantes de reagentes com potes decorados e aglomerados de ametista
        kit.fill(l.minX() + 1, G, l.centerZ() - 3, l.minX() + 1, G + 3, l.centerZ() + 3, Blocks.BOOKSHELF);
        kit.set(l.minX() + 2, G, l.centerZ(), Blocks.AMETHYST_CLUSTER);
        kit.set(l.maxX() - 2, G, l.centerZ(), Blocks.AMETHYST_CLUSTER);

        kit.set(cx - 6, G, cz, Blocks.ENCHANTING_TABLE);
        kit.set(cx + 6, G, cz, Blocks.ENCHANTING_TABLE);

        // Tanque de contenção central com iluminação das almas
        kit.walls(cx - 3, G, cz - 3, cx + 3, G + 3, cz + 3, Blocks.GLASS);
        kit.fill(cx - 2, G, cz - 2, cx + 2, G + 2, cz + 2, Blocks.WATER);
        kit.set(cx, G, cz, Blocks.SEA_LANTERN);
        kit.set(cx, G + 4, cz, Blocks.SOUL_LANTERN);
    }

    // =========================================================================
    // 8. ESTUFA BOTÂNICA ARCANA
    // =========================================================================

    public static void buildGreenhouse(BlockKit kit) {
        Plot g = GREENHOUSE;

        // 1. Piso natural de grama fértil
        kit.fill(g.minX(), FLOOR_Y, g.minZ(), g.maxX(), FLOOR_Y, g.maxZ(), Blocks.GRASS_BLOCK);
        kit.clear(g.minX() + 1, G, g.minZ() + 1, g.maxX() - 1, G + 14, g.maxZ() - 1);

        // 2. Plinto de tijolos de pedra e arcada de quartzo e carvalho
        kit.walls(g.minX(), G, g.minZ(), g.maxX(), G, g.maxZ(), Blocks.STONE_BRICKS);
        kit.walls(g.minX(), G + 1, g.minZ(), g.maxX(), G + 8, g.maxZ(), Blocks.GLASS);

        for (int x = g.minX(); x <= g.maxX(); x += 6) {
            kit.pillar(x, G, G + 8, g.minZ(), Blocks.QUARTZ_PILLAR);
            kit.pillar(x, G, G + 8, g.maxZ(), Blocks.QUARTZ_PILLAR);
        }

        // 3. Entrada na parede norte com arco
        kit.clear(g.centerX() - 2, G, g.minZ(), g.centerX() + 2, G + 4, g.minZ());
        kit.entryPortico(g.centerX(), G, g.minZ(), Direction.NORTH, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);

        // 4. Abóbada cilíndrica de vidro com nervuras
        kit.vaultAlongX(g.minX(), g.maxX(), g.minZ(), g.maxZ(), G + 9, Blocks.GLASS);
        for (int x = g.minX(); x <= g.maxX(); x += 6) {
            kit.vaultAlongX(x, x, g.minZ(), g.maxZ(), G + 9, Blocks.DARK_OAK_WOOD);
        }

        // 5. Caminhos centrais de pedra e cascalho
        kit.fill(g.centerX() - 1, G, g.minZ() + 1, g.centerX() + 1, G, g.maxZ() - 1, Blocks.STONE_BRICKS);

        // 6. Canteiros elevados de flores raras e medicinais
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

        // 7. Fonte central com bacia de pedra e vitória-régia
        kit.disc(g.centerX(), G, g.centerZ(), 3, Blocks.STONE_BRICKS);
        kit.fill(g.centerX() - 2, G, g.centerZ() - 2, g.centerX() + 2, G, g.centerZ() + 2, Blocks.WATER);
        kit.set(g.centerX(), G + 1, g.centerZ(), Blocks.LILY_PAD);
    }

    // =========================================================================
    // 9. BLOCO ACADÊMICO (4 SALAS DE AULA TEMÁTICAS)
    // =========================================================================

    public static void buildAcademicBlock(BlockKit kit) {
        // Fachada imperial nobre UNIFICADA com telhado de ardósia nobre e beirais salientes.
        // O interior e o piso mudam por disciplina para máxima personalidade e vivacidade.
        buildClassroom(kit, HISTORY, "História", 0);
        buildClassroom(kit, ZOOLOGY, "Zoologia", 1);
        buildClassroom(kit, PHILOSOPHY, "Filosofia", 2);
        buildClassroom(kit, TACTICS, "Tática", 3);

        // Ponte e Portal Imperial monumental sobre a Avenida Central entre as salas
        kit.clear(-6, G, 67, 6, G + 5, 71);
        kit.fill(-6, G + 6, 67, 6, G + 7, 71, Blocks.QUARTZ_BRICKS);
        kit.walls(-6, G + 8, 67, 6, G + 8, 71, Blocks.QUARTZ_PILLAR);
        kit.lampPost(-5, G + 9, 69);
        kit.lampPost(5, G + 9, 69);
    }

    private static void buildClassroom(BlockKit kit, Plot plot, String theme, int type) {
        // 1. Fachada Imperial nobre padronizada (sem pedras aleatórias no exterior!)
        kit.clear(plot.minX() + 1, G, plot.minZ() + 1, plot.maxX() - 1, G + 9, plot.maxZ() - 1);
        kit.walls(plot.minX(), G, plot.minZ(), plot.maxX(), G, plot.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(plot.minX(), G + 1, plot.minZ(), plot.maxX(), G + 7, plot.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilastras salientes nos cantos e centro da sala
        kit.pillar(plot.minX() - 1, G, G + 7, plot.minZ() - 1, Blocks.QUARTZ_PILLAR);
        kit.pillar(plot.maxX() + 1, G, G + 7, plot.minZ() - 1, Blocks.QUARTZ_PILLAR);
        kit.pillar(plot.minX() - 1, G, G + 7, plot.maxZ() + 1, Blocks.QUARTZ_PILLAR);
        kit.pillar(plot.maxX() + 1, G, G + 7, plot.maxZ() + 1, Blocks.QUARTZ_PILLAR);

        // Janelas emolduradas com floreiras de azaleia
        kit.framedWindow(plot.minX(), G + 2, plot.centerZ(), 3, Direction.WEST, Blocks.QUARTZ_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        kit.setIfAir(plot.minX() - 1, G + 1, plot.centerZ(), Blocks.FLOWERING_AZALEA);
        kit.framedWindow(plot.maxX(), G + 2, plot.centerZ(), 3, Direction.EAST, Blocks.QUARTZ_STAIRS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        kit.setIfAir(plot.maxX() + 1, G + 1, plot.centerZ(), Blocks.FLOWERING_AZALEA);

        // Pórtico de entrada na parede norte
        kit.clear(plot.centerX() - 1, G, plot.minZ(), plot.centerX() + 1, G + 3, plot.minZ());
        kit.entryPortico(plot.centerX(), G, plot.minZ(), Direction.NORTH, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);

        // Telhado de ardósia nobre com beirais pronunciados
        kit.overhangGableRoofAlongX(plot.minX() - 1, plot.maxX() + 1, plot.minZ() - 1, plot.maxZ() + 1, G + 8,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_TILES);

        // 2. Lousa negra na parede sul com moldura de madeira
        kit.fill(plot.centerX() - 4, G + 1, plot.maxZ(), plot.centerX() + 4, G + 3, plot.maxZ(), Blocks.POLISHED_BLACKSTONE);
        kit.fill(plot.centerX() - 4, G, plot.maxZ(), plot.centerX() + 4, G, plot.maxZ(), kit.slab(Blocks.SPRUCE_SLAB, SlabType.TOP));

        // Mesa e Púlpito do Professor
        int teacherZ = plot.maxZ() - 3;
        kit.set(plot.centerX(), G, teacherZ, Blocks.LECTERN.defaultBlockState().setValue(LecternBlock.FACING, Direction.NORTH));
        kit.set(plot.centerX() + 1, G, teacherZ, ModBlocks.CLASSROOM_DESK.get());

        // Carteiras dos Alunos
        for (int z = plot.minZ() + 4; z <= plot.minZ() + 12; z += 4) {
            for (int dx = -6; dx <= 6; dx += 4) {
                kit.set(plot.centerX() + dx, G, z, ModBlocks.CLASSROOM_DESK.get());
            }
        }

        // Iluminação no teto com lanternas penduradas
        kit.chandelier(plot.centerX() - 4, G + 7, 2, plot.centerZ());
        kit.chandelier(plot.centerX() + 4, G + 7, 2, plot.centerZ());

        // 3. Piso e Decoração Temática por Matéria
        if (type == 0) {
            // HISTÓRIA: Piso de tábuas de pinheiro envelhecido
            kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.SPRUCE_PLANKS);
            kit.set(plot.minX() + 2, G, teacherZ, Blocks.CARTOGRAPHY_TABLE);
            kit.fill(plot.minX() + 1, G, plot.minZ() + 2, plot.minX() + 1, G + 3, plot.minZ() + 6, Blocks.BOOKSHELF);
            kit.set(plot.minX() + 2, G + 1, teacherZ, Blocks.CANDLE);
        } else if (type == 1) {
            // ZOOLOGIA: Piso de tábuas de carvalho e terracota
            kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.OAK_PLANKS);
            kit.walls(plot.minX() + 2, G, plot.minZ() + 2, plot.minX() + 5, G + 2, plot.minZ() + 5, Blocks.GLASS);
            kit.set(plot.minX() + 3, G, plot.minZ() + 3, Blocks.MOSS_BLOCK);
            kit.set(plot.minX() + 4, G, plot.minZ() + 4, Blocks.FERN);
            kit.set(plot.minX() + 2, G, teacherZ, Blocks.DECORATED_POT);
        } else if (type == 2) {
            // FILOSOFIA: Piso em círculos concêntricos de ardósia polida e quartzo
            kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.POLISHED_DEEPSLATE);
            kit.disc(plot.centerX(), FLOOR_Y, plot.centerZ(), 4, Blocks.SMOOTH_QUARTZ);
            kit.disc(plot.centerX(), FLOOR_Y, plot.centerZ(), 2, Blocks.POLISHED_DEEPSLATE);
            kit.set(plot.centerX(), G, plot.centerZ(), Blocks.AMETHYST_BLOCK);
        } else if (type == 3) {
            // TÁTICA: Piso de pedra negra polida com grande mesa de relevo 3D militar
            kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.POLISHED_BLACKSTONE);
            int mx = plot.maxX() - 4;
            kit.fill(mx - 1, G, teacherZ - 1, mx + 1, G, teacherZ + 1, Blocks.DEEPSLATE_BRICKS);
            kit.set(mx, G + 1, teacherZ, Blocks.MOSS_BLOCK);
            kit.set(mx + 1, G + 1, teacherZ, Blocks.WATER);
            kit.set(mx - 1, G + 1, teacherZ, Blocks.STONE);
        }
    }

    // =========================================================================
    // 10. DOJO & DEPARTAMENTO MARCIAL
    // =========================================================================

    public static void buildDojo(BlockKit kit) {
        Plot d = DOJO;

        // 1. Piso tradicional de tatame de mosaico de bambu com moldura de carvalho escuro
        kit.fill(d.minX(), FLOOR_Y, d.minZ(), d.maxX(), FLOOR_Y, d.maxZ(), Blocks.BAMBOO_MOSAIC);
        kit.walls(d.minX(), FLOOR_Y, d.minZ(), d.maxX(), FLOOR_Y, d.maxZ(), Blocks.DARK_OAK_PLANKS);

        kit.clear(d.minX() + 1, G, d.minZ() + 1, d.maxX() - 1, G + 14, d.maxZ() - 1);

        // 2. Paredes com plinto de ardósia e corpo de quartzo/concreto branco com pilares de carvalho escuro
        kit.walls(d.minX(), G, d.minZ(), d.maxX(), G, d.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(d.minX(), G + 1, d.minZ(), d.maxX(), G + 10, d.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilares de madeira nobre a cada 6 blocos
        for (int x = d.minX(); x <= d.maxX(); x += 6) {
            kit.pillar(x, G, G + 10, d.minZ() - 1, Blocks.DARK_OAK_LOG);
            kit.pillar(x, G, G + 10, d.maxZ() + 1, Blocks.DARK_OAK_LOG);
        }
        for (int z = d.minZ(); z <= d.maxZ(); z += 6) {
            kit.pillar(d.minX() - 1, G, G + 10, z, Blocks.DARK_OAK_LOG);
            kit.pillar(d.maxX() + 1, G, G + 10, z, Blocks.DARK_OAK_LOG);
        }

        // 3. Entrada na parede oeste com pórtico Torii cerimonial
        kit.clear(d.minX(), G, d.centerZ() - 2, d.minX(), G + 5, d.centerZ() + 2);
        kit.pillar(d.minX() - 3, G, G + 6, d.centerZ() - 3, Blocks.RED_NETHER_BRICKS);
        kit.pillar(d.minX() - 3, G, G + 6, d.centerZ() + 3, Blocks.RED_NETHER_BRICKS);
        kit.fill(d.minX() - 3, G + 7, d.centerZ() - 4, d.minX() - 3, G + 7, d.centerZ() + 4, Blocks.RED_NETHER_BRICK_SLAB);
        kit.fill(d.minX() - 3, G + 6, d.centerZ() - 3, d.minX() - 3, G + 6, d.centerZ() + 3, Blocks.RED_NETHER_BRICKS);
        kit.set(d.minX() - 3, G + 5, d.centerZ() - 3, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        kit.set(d.minX() - 3, G + 5, d.centerZ() + 3, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        kit.fill(d.minX() - 2, G, d.centerZ() - 2, d.minX() - 1, G, d.centerZ() + 2, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.WEST));

        // 4. Telhado Pagoda imperial em ardósia com beirais curvados e lanternas suspensas nos cantos
        kit.hipRoof(d.minX() - 1, d.minZ() - 1, d.maxX() + 1, d.maxZ() + 1, G + 11,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES, Blocks.GOLD_BLOCK);
        // Lanternas suspensas nos 4 cantos do beiral
        kit.set(d.minX() - 2, G + 10, d.minZ() - 2, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        kit.set(d.maxX() + 2, G + 10, d.minZ() - 2, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        kit.set(d.minX() - 2, G + 10, d.maxZ() + 2, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        kit.set(d.maxX() + 2, G + 10, d.maxZ() + 2, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));

        // 5. Ringue de Sparring central demarcado com esteiras
        int cx = d.centerX();
        int cz = d.centerZ();
        kit.ring(cx, G, cz, 6, 7, Blocks.RED_CARPET);
        kit.disc(cx, G, cz, 5, Blocks.WHITE_CARPET);

        // 6. Bonecos de Treino e Espantalhos ao longo da parede leste
        for (int z = d.minZ() + 6; z <= d.maxZ() - 6; z += 6) {
            int tx = d.maxX() - 3;
            kit.set(tx, G, z, Blocks.DARK_OAK_FENCE);
            kit.set(tx, G + 1, z, Blocks.HAY_BLOCK);
            kit.set(tx, G + 2, z, Blocks.CARVED_PUMPKIN);
            kit.set(tx, G, z - 1, Blocks.TARGET);
        }

        // Racks de armas, bigornas e pedras de amolar
        kit.set(d.minX() + 3, G, d.minZ() + 3, Blocks.ANVIL);
        kit.set(d.minX() + 3, G, d.maxZ() - 3, Blocks.GRINDSTONE);
        kit.fill(d.minX() + 1, G + 1, d.minZ() + 4, d.minX() + 1, G + 2, d.minZ() + 8, Blocks.IRON_BARS);

        // Lustres e iluminação
        kit.chandelier(cx - 8, G + 11, 2, cz - 8);
        kit.chandelier(cx + 8, G + 11, 2, cz - 8);
        kit.chandelier(cx - 8, G + 11, 2, cz + 8);
        kit.chandelier(cx + 8, G + 11, 2, cz + 8);
    }

    // =========================================================================
    // 11. COMPLEXO ESPORTIVO & ARENA
    // =========================================================================

    public static void buildSportsArena(BlockKit kit) {
        Plot s = SPORTS;
        kit.fill(s.minX(), FLOOR_Y, s.minZ(), s.maxX(), FLOOR_Y, s.maxZ(), Blocks.POLISHED_ANDESITE);
        kit.clear(s.minX(), G, s.minZ(), s.maxX(), G + 12, s.maxZ());

        // Muros externos com contrafortes a cada 8 blocos
        kit.walls(s.minX(), G, s.minZ(), s.maxX(), G + 4, s.maxZ(), Blocks.STONE_BRICKS);
        for (int x = s.minX(); x <= s.maxX(); x += 8) {
            kit.pillar(x, G, G + 5, s.minZ() - 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
            kit.pillar(x, G, G + 5, s.maxZ() + 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
        }

        // Entrada monumental na parede leste
        kit.clear(s.maxX(), G, s.centerZ() - 3, s.maxX(), G + 5, s.centerZ() + 3);
        kit.entryPortico(s.centerX(), G, s.maxX(), Direction.EAST, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.STONE_BRICK_STAIRS, Blocks.GOLD_BLOCK);

        // Campo gramado central
        int fx1 = s.minX() + 20, fx2 = s.maxX() - 20;
        int fz1 = s.minZ() + 18, fz2 = s.maxZ() - 18;
        kit.fill(fx1, FLOOR_Y, fz1, fx2, FLOOR_Y, fz2, Blocks.GRASS_BLOCK);

        // Pista de Atletismo de terracota laranja ao redor do campo
        kit.ring(s.centerX(), FLOOR_Y, s.centerZ(), 24, 30, Blocks.ORANGE_TERRACOTTA);

        // Traves brancas
        kit.pillar(fx1 + 2, G, G + 3, s.centerZ() - 3, Blocks.WHITE_CONCRETE);
        kit.pillar(fx1 + 2, G, G + 3, s.centerZ() + 3, Blocks.WHITE_CONCRETE);
        kit.fill(fx1 + 2, G + 4, s.centerZ() - 3, fx1 + 2, G + 4, s.centerZ() + 3, Blocks.WHITE_CONCRETE);

        kit.pillar(fx2 - 2, G, G + 3, s.centerZ() - 3, Blocks.WHITE_CONCRETE);
        kit.pillar(fx2 - 2, G, G + 3, s.centerZ() + 3, Blocks.WHITE_CONCRETE);
        kit.fill(fx2 - 2, G + 4, s.centerZ() - 3, fx2 - 2, G + 4, s.centerZ() + 3, Blocks.WHITE_CONCRETE);

        // Arquibancadas norte e sul escalonadas
        for (int row = 0; row < 4; row++) {
            kit.fill(s.minX() + 10, G + row, s.minZ() + 4 + row, s.maxX() - 10, G + row, s.minZ() + 4 + row,
                    kit.stair(Blocks.SMOOTH_QUARTZ_STAIRS, Direction.SOUTH));
            kit.fill(s.minX() + 10, G + row, s.maxZ() - 4 - row, s.maxX() - 10, G + row, s.maxZ() - 4 - row,
                    kit.stair(Blocks.SMOOTH_QUARTZ_STAIRS, Direction.NORTH));
        }

        // 4 Torres de Refletores nos cantos com tochas das almas e lanternas marítimas
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
        buildDormBuilding(kit, DORM_MALE, false);
        buildDormBuilding(kit, DORM_FEMALE, true);
    }

    private static void buildDormBuilding(BlockKit kit, Plot plot, boolean female) {
        // Fachada Imperial Nobre UNIFICADA com plinto de ardósia e pilastras salientes
        Block accentWood = female ? Blocks.CHERRY_PLANKS : Blocks.DARK_OAK_PLANKS;
        Block bedBlock = female ? Blocks.PINK_BED : Blocks.BLUE_BED;
        Block carpetBlock = female ? Blocks.PINK_CARPET : Blocks.LIGHT_BLUE_CARPET;

        // 1. Piso acolhedor de madeira de pinheiro
        kit.fill(plot.minX(), FLOOR_Y, plot.minZ(), plot.maxX(), FLOOR_Y, plot.maxZ(), Blocks.SPRUCE_PLANKS);
        kit.clear(plot.minX() + 1, G, plot.minZ() + 1, plot.maxX() - 1, G + 14, plot.maxZ() - 1);

        // 2. Paredes imperiais com plinto de ardósia
        kit.walls(plot.minX(), G, plot.minZ(), plot.maxX(), G, plot.maxZ(), Blocks.DEEPSLATE_BRICKS);
        kit.walls(plot.minX(), G + 1, plot.minZ(), plot.maxX(), G + 12, plot.maxZ(), Blocks.QUARTZ_BRICKS);

        // Pilastras salientes (profundidade 3D) a cada 6 blocos
        for (int x = plot.minX(); x <= plot.maxX(); x += 6) {
            kit.pillar(x, G, G + 12, plot.minZ() - 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, plot.minZ() - 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.NORTH, Half.TOP));
            kit.pillar(x, G, G + 12, plot.maxZ() + 1, Blocks.QUARTZ_PILLAR);
            kit.set(x, G + 12, plot.maxZ() + 1, kit.stair(Blocks.QUARTZ_STAIRS, Direction.SOUTH, Half.TOP));
        }

        // 3. Decks dos andares superiores (3 andares de alojamento)
        int deck2 = G + 4;
        int deck3 = G + 8;
        kit.fill(plot.minX() + 1, deck2, plot.minZ() + 1, plot.maxX() - 1, deck2, plot.maxZ() - 1, Blocks.SPRUCE_PLANKS);
        kit.fill(plot.minX() + 1, deck3, plot.minZ() + 1, plot.maxX() - 1, deck3, plot.maxZ() - 1, Blocks.SPRUCE_PLANKS);

        // Porta voltada para o Hall com pórtico
        int doorX = female ? plot.minX() : plot.maxX();
        kit.clear(doorX, G, plot.centerZ() - 1, doorX, G + 4, plot.centerZ() + 1);
        kit.entryPortico(plot.centerX(), G, doorX, female ? Direction.WEST : Direction.EAST,
                Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_STAIRS, Blocks.GOLD_BLOCK);

        // 4. Telhado com beirais pronunciados e lucarnas (dormers) de ambos os lados
        kit.overhangGableRoofAlongX(plot.minX() - 1, plot.maxX() + 1, plot.minZ() - 1, plot.maxZ() + 1, G + 13,
                Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GOLD_BLOCK, Blocks.DEEPSLATE_TILES);

        // 3 Lucarnas salientes no telhado na face norte e sul
        for (int lx = plot.minX() + 6; lx <= plot.maxX() - 6; lx += 8) {
            kit.dormerWindow(lx, G + 14, plot.minZ() + 2, Direction.NORTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GLASS_PANE);
            kit.dormerWindow(lx, G + 14, plot.maxZ() - 2, Direction.SOUTH, Blocks.QUARTZ_BRICKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.GLASS_PANE);
        }

        // 5. Janelas dos andares com venezianas e floreiras de azaleias
        for (int fy : new int[]{G + 1, deck2 + 1, deck3 + 1}) {
            for (int z = plot.minZ() + 5; z <= plot.maxZ() - 5; z += 6) {
                kit.framedWindow(plot.minX(), fy + 1, z, 2, Direction.WEST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
                kit.setIfAir(plot.minX() - 1, fy, z, Blocks.FLOWERING_AZALEA);
                kit.framedWindow(plot.maxX(), fy + 1, z, 2, Direction.EAST, Blocks.QUARTZ_STAIRS, Blocks.GLASS_PANE);
                kit.setIfAir(plot.maxX() + 1, fy, z, Blocks.FLOWERING_AZALEA);
            }
        }

        // 6. Quartos completos em cada andar
        int[] floorLevels = {G, deck2 + 1, deck3 + 1};
        for (int fy : floorLevels) {
            if (fy > G) {
                kit.clear(plot.centerX() - 2, fy - 1, plot.minZ() + 2, plot.centerX() + 2, fy - 1, plot.minZ() + 5);
            }
            for (int z = plot.minZ() + 5; z <= plot.maxZ() - 5; z += 6) {
                // Quarto Ala Oeste
                kit.bed(plot.minX() + 2, fy, z, Direction.EAST, bedBlock);
                kit.set(plot.minX() + 1, fy, z, Blocks.CHEST);
                kit.set(plot.minX() + 4, fy, z, ModBlocks.CLASSROOM_DESK.get());
                kit.set(plot.minX() + 1, fy, z + 1, Blocks.BARREL);
                kit.set(plot.minX() + 3, fy, z, carpetBlock);

                // Quarto Ala Leste
                kit.bed(plot.maxX() - 3, fy, z, Direction.WEST, bedBlock);
                kit.set(plot.maxX() - 1, fy, z, Blocks.CHEST);
                kit.set(plot.maxX() - 5, fy, z, ModBlocks.CLASSROOM_DESK.get());
                kit.set(plot.maxX() - 1, fy, z + 1, Blocks.BARREL);
                kit.set(plot.maxX() - 3, fy, z, carpetBlock);
            }
            kit.set(plot.centerX(), fy + 3, plot.centerZ(), Blocks.LANTERN);
        }

        // Escadarias conectando os andares
        for (int step = 0; step < 4; step++) {
            kit.set(plot.centerX(), G + step, plot.minZ() + 2 + step, kit.stair(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
            kit.set(plot.centerX(), deck2 + 1 + step, plot.minZ() + 2 + step, kit.stair(Blocks.SPRUCE_STAIRS, Direction.SOUTH));
        }

        // 7. Lounge e Salão Comum no térreo com lareira e sofás
        int lz = plot.maxZ() - 3;
        kit.set(plot.centerX(), G, lz, Blocks.CAMPFIRE);
        kit.set(plot.centerX(), G + 1, lz, Blocks.IRON_BARS);
        kit.set(plot.centerX() - 1, G, lz, Blocks.STONE_BRICKS);
        kit.set(plot.centerX() + 1, G, lz, Blocks.STONE_BRICKS);
        kit.set(plot.centerX() - 1, G + 1, lz, kit.stair(Blocks.STONE_BRICK_STAIRS, Direction.EAST, Half.TOP));
        kit.set(plot.centerX() + 1, G + 1, lz, kit.stair(Blocks.STONE_BRICK_STAIRS, Direction.WEST, Half.TOP));
        kit.set(plot.centerX(), G + 2, lz, Blocks.LANTERN);

        // Sofás em semicírculo voltados para a lareira
        kit.set(plot.centerX() - 2, G, lz - 2, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.EAST));
        kit.set(plot.centerX() + 2, G, lz - 2, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.WEST));
        kit.set(plot.centerX(), G, lz - 3, kit.stair(Blocks.DARK_OAK_STAIRS, Direction.NORTH));
        kit.set(plot.centerX(), G, lz - 2, carpetBlock);
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
