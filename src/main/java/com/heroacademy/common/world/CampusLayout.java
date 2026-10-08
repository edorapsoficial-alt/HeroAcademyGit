package com.heroacademy.common.world;

import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Planta oficial do Campus da Academia Imperial Destiny.
 * Todas as coordenadas do campus (construção, zonas, teleportes, spawn, relevos e montanhas) partem daqui.
 * Eixos: +X leste, +Z sul. O Hall fica ao norte e as Docas ao sul.
 */
public final class CampusLayout {
    private CampusLayout() {}

    /** Y do bloco de piso (a grama do mundo plano). */
    public static final int FLOOR_Y = 63;
    /** Y onde o jogador caminha. */
    public static final int G = 64;

    public static final double SPAWN_X = 0.5;
    public static final double SPAWN_Z = 14.5;

    public static final int PORTAL_X = 0;
    public static final int PORTAL_Z = 196;

    public static final int PLAZA_RADIUS = 24;
    public static final int DOCKS_CENTER_Z = 186;
    public static final int DOCKS_RADIUS = 18;

    /** Borda do mundo configurada no cume das cordilheiras externas (impede travessia ao infinito). */
    public static final int BORDER_CENTER_X = 0;
    public static final int BORDER_CENTER_Z = 40;
    public static final int BORDER_SIZE = 520;

    /** Posições de monumentos construídos com blocos */
    public static final int SWORD_MONUMENT_X = 0;
    public static final int SWORD_MONUMENT_Z = 88;

    public record Plot(int minX, int minZ, int maxX, int maxZ) {
        public int centerX() { return (minX + maxX) / 2; }
        public int centerZ() { return (minZ + maxZ) / 2; }

        public boolean contains(int x, int z, int margin) {
            return x >= minX - margin && x <= maxX + margin && z >= minZ - margin && z <= maxZ + margin;
        }

        public AABB aabb(double yMin, double yMax) {
            return new AABB(minX, yMin, minZ, maxX + 1, yMax, maxZ + 1);
        }
    }

    // ---------------- Construções ----------------
    public static final Plot WALL = new Plot(-210, -170, 210, 250);

    public static final Plot GRAND_HALL = new Plot(-32, -80, 32, -44);
    public static final Plot CLOCK_TOWER = new Plot(-6, -92, 6, -80);

    public static final Plot LIBRARY = new Plot(-100, -28, -56, 6);
    public static final Plot FOOD_HALL = new Plot(56, -28, 100, 6);

    public static final Plot LAB = new Plot(-96, 24, -60, 52);
    public static final Plot GREENHOUSE = new Plot(60, 24, 96, 52);

    public static final Plot HISTORY = new Plot(-56, 58, -32, 80);
    public static final Plot ZOOLOGY = new Plot(-31, 58, -7, 80);
    public static final Plot PHILOSOPHY = new Plot(7, 58, 31, 80);
    public static final Plot TACTICS = new Plot(32, 58, 56, 80);

    public static final Plot DOJO = new Plot(48, 92, 108, 138);
    public static final Plot SPORTS = new Plot(-140, 92, -50, 152);

    public static final Plot DORM_MALE = new Plot(-112, -84, -78, -52);
    public static final Plot DORM_FEMALE = new Plot(78, -84, 112, -52);

    public static final Plot DOCKS = new Plot(-26, 168, 26, 204);

    public static final List<Plot> BUILDINGS = List.of(
            GRAND_HALL, CLOCK_TOWER, LIBRARY, FOOD_HALL, LAB, GREENHOUSE,
            HISTORY, ZOOLOGY, PHILOSOPHY, TACTICS, DOJO, SPORTS,
            DORM_MALE, DORM_FEMALE, DOCKS
    );

    // ---------------- Estradas (largura 5 / ruas principais) ----------------
    public static final List<Plot> ROADS = List.of(
            new Plot(-2, -43, 2, 167),      // Avenida Imperial (norte-sul)
            new Plot(-55, -2, 55, 2),       // Alameda Central (biblioteca <-> refeitório)
            new Plot(-100, 16, 100, 20),    // Via das Ciências
            new Plot(-79, 21, -77, 23),     // acesso ao laboratório
            new Plot(77, 21, 79, 23),       // acesso à estufa
            new Plot(-58, 53, 58, 57),      // Via Acadêmica
            new Plot(-77, -62, -33, -58),   // Hall <-> dormitório masculino
            new Plot(33, -62, 77, -58),     // Hall <-> dormitório feminino
            new Plot(3, 113, 47, 117),      // acesso ao dojo
            new Plot(-49, 113, -3, 117)     // acesso à arena esportiva
    );

    /** Verdadeiro se (x,z) está dentro de um prédio, estrada, praça, doca ou monumento (com folga). */
    public static boolean isReserved(int x, int z) {
        for (Plot b : BUILDINGS) {
            if (b.contains(x, z, 6)) return true;
        }
        for (Plot r : ROADS) {
            if (r.contains(x, z, 3)) return true;
        }
        if (x * x + z * z <= (PLAZA_RADIUS + 4) * (PLAZA_RADIUS + 4)) return true;
        int dz = z - DOCKS_CENTER_Z;
        if (x * x + dz * dz <= (DOCKS_RADIUS + 4) * (DOCKS_RADIUS + 4)) return true;
        // Reserva para o Monumento da Espada Gigante
        if (Math.abs(x - SWORD_MONUMENT_X) <= 12 && Math.abs(z - SWORD_MONUMENT_Z) <= 12) return true;
        return false;
    }
}
