package com.heroacademy.common.academic;

import com.heroacademy.common.world.CampusLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public enum CampusWaypoint {
    COURTYARD(
            "courtyard",
            "Pátio Monumental & Jardins",
            "Praça Imperial • Fonte Central e Alameda Principal",
            "⛲",
            "Convivência",
            new BlockPos(0, CampusLayout.G, 14),
            180.0f,
            new BlockPos(5, CampusLayout.G, 14)
    ),
    GRAND_HALL(
            "grand_hall",
            "Grande Hall Imperial",
            "Salão Nobre de Recepção • Escadarias e Honras",
            "🏛️",
            "Convivência",
            new BlockPos(0, CampusLayout.G, -38),
            0.0f,
            new BlockPos(4, CampusLayout.G, -38)
    ),
    FOOD_HALL(
            "food_hall",
            "Grande Refeitório Imperial",
            "Salão de Banquetes Comunitário & Cozinha dos Alunos",
            "🍲",
            "Convivência",
            new BlockPos(52, CampusLayout.G, 0),
            90.0f,
            new BlockPos(52, CampusLayout.G, 4)
    ),
    ARCANE_LIBRARY(
            "library",
            "Grande Biblioteca Imperial",
            "Ala Oeste • Acervo de Tomos Mágicos e Encantamentos",
            "📚",
            "Estudos",
            new BlockPos(-52, CampusLayout.G, 0),
            -90.0f,
            new BlockPos(-52, CampusLayout.G, 4)
    ),
    MARTIAL_WING(
            "martial_wing",
            "Dojo & Departamento Marcial",
            "Anfiteatro de Duelos • Tatames e Treinamento de Combate",
            "⚔️",
            "Treino",
            new BlockPos(45, CampusLayout.G, 115),
            90.0f,
            new BlockPos(45, CampusLayout.G, 119)
    ),
    HISTORY_CLASSROOM(
            "history_class",
            "Sala de História & Lore",
            "Ala de Humanidades • Relíquias e Cronologia Antiga",
            "📜",
            "Aulas",
            new BlockPos(-44, CampusLayout.G, 55),
            180.0f,
            new BlockPos(-41, CampusLayout.G, 51)
    ),
    MAGIC_SCIENCE_LAB(
            "science_lab",
            "Ciência Mágica & Alquimia",
            "Ala de Ciências • Teoria da Mana, Poções e Transmutação",
            "🧪",
            "Aulas",
            new BlockPos(-78, CampusLayout.G, 20),
            0.0f,
            new BlockPos(-75, CampusLayout.G, 22)
    ),
    BOTANY_GREENHOUSE(
            "botany_greenhouse",
            "Botânica & Estufa Arcana",
            "Ala de Ciências • Herbologia e Cultivo de Flores Raras",
            "🌿",
            "Aulas",
            new BlockPos(78, CampusLayout.G, 20),
            0.0f,
            new BlockPos(75, CampusLayout.G, 22)
    ),
    ZOOLOGY_PAVILION(
            "zoology_pavilion",
            "Zoologia & Fauna Mística",
            "Ala de Ciências • Anatomia de Bestas e Criaturas Mágicas",
            "🐾",
            "Aulas",
            new BlockPos(-19, CampusLayout.G, 55),
            180.0f,
            new BlockPos(-16, CampusLayout.G, 51)
    ),
    THEORY_CLASSROOM_1(
            "philosophy_class",
            "Sala de Filosofia & Mente",
            "Ala de Humanidades • Meditação Arcana e Controle de Foco",
            "🧠",
            "Aulas",
            new BlockPos(19, CampusLayout.G, 55),
            180.0f,
            new BlockPos(22, CampusLayout.G, 51)
    ),
    THEORY_CLASSROOM_2(
            "tactics_class",
            "Sala de Estratégia Arcana",
            "Ala Militar • Táticas de Guerra Mágica e Relevo 3D",
            "🔮",
            "Aulas",
            new BlockPos(44, CampusLayout.G, 55),
            180.0f,
            new BlockPos(47, CampusLayout.G, 51)
    ),
    MALE_DORMITORY(
            "male_dorm",
            "Dormitórios Masculinos",
            "Residência Oeste • Quartos de Descanso e Lounge",
            "🛏️",
            "Alojamento",
            new BlockPos(-75, CampusLayout.G, -60),
            -90.0f,
            new BlockPos(-75, CampusLayout.G, -56)
    ),
    FEMALE_DORMITORY(
            "female_dorm",
            "Dormitórios Femininos",
            "Residência Leste • Quartos de Descanso e Salão de Chá",
            "🌸",
            "Alojamento",
            new BlockPos(75, CampusLayout.G, -60),
            90.0f,
            new BlockPos(75, CampusLayout.G, -56)
    ),
    SPORTS_ARENA(
            "sports_arena",
            "Complexo Esportivo & Arena",
            "Pátio de Festivais • Pista de Atletismo e Competições",
            "🏆",
            "Treino",
            new BlockPos(-47, CampusLayout.G, 115),
            -90.0f,
            new BlockPos(-47, CampusLayout.G, 119)
    ),
    AIRSHIP_DOCKS(
            "airship_docks",
            "Docks do Dirigível (Portal)",
            "Plataforma de Saída • Portal de Viagem ao Mundo Exterior",
            "🚢",
            "Serviços",
            new BlockPos(0, CampusLayout.G, 176),
            180.0f,
            new BlockPos(4, CampusLayout.G, 176)
    ),
    CLOCK_TOWER(
            "clock_tower",
            "Torre do Relógio Imperial",
            "Pináculo Nobre da Academia • Observatório Central",
            "⏳",
            "Serviços",
            new BlockPos(0, CampusLayout.G, -84),
            0.0f,
            new BlockPos(3, CampusLayout.G, -87)
    );

    private final String id;
    private final String name;
    private final String description;
    private final String icon;
    private final String category;
    private final BlockPos targetPos;
    private final float targetYaw;
    private final BlockPos terminalPos;

    CampusWaypoint(String id, String name, String description, String icon, String category, BlockPos targetPos, float targetYaw, BlockPos terminalPos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.targetPos = targetPos;
        this.targetYaw = targetYaw;
        this.terminalPos = terminalPos;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getCategory() {
        return category;
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public float getTargetYaw() {
        return targetYaw;
    }

    public BlockPos getTerminalPos() {
        return terminalPos;
    }

    public double getDistanceTo(Vec3 pos) {
        double dx = pos.x - (targetPos.getX() + 0.5);
        double dz = pos.z - (targetPos.getZ() + 0.5);
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static CampusWaypoint byId(String id) {
        if (id == null) return null;
        for (CampusWaypoint wp : values()) {
            if (wp.id.equalsIgnoreCase(id) || wp.name().equalsIgnoreCase(id)) {
                return wp;
            }
        }
        return null;
    }

    public static CampusWaypoint byNameOrId(String query) {
        if (query == null || query.isBlank()) return null;
        String clean = query.trim().toLowerCase();
        for (CampusWaypoint wp : values()) {
            if (wp.id.equalsIgnoreCase(clean)
                    || wp.name().equalsIgnoreCase(clean)
                    || wp.name.toLowerCase().contains(clean)) {
                return wp;
            }
        }
        return null;
    }

    public static List<CampusWaypoint> getAll() {
        return Arrays.asList(values());
    }
}
