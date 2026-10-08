package com.heroacademy.common.academic;

import com.heroacademy.common.world.CampusLayout;
import net.minecraft.world.phys.AABB;

public enum CampusZone {
    COURTYARD_GARDENS(
            "§e§lPÁTIO MONUMENTAL & JARDINS",
            "§7Praça Imperial Destiny • Fonte Central e Jardins Floridos",
            "⛲",
            new AABB(-CampusLayout.PLAZA_RADIUS, 55, -CampusLayout.PLAZA_RADIUS, CampusLayout.PLAZA_RADIUS + 1, 90, CampusLayout.PLAZA_RADIUS + 1)
    ),
    GRAND_HALL(
            "§6§lGRANDE HALL IMPERIAL",
            "§7Palácio Central • Salão Nobre de Recepção & Honra",
            "🏛️",
            CampusLayout.GRAND_HALL.aabb(55, 105)
    ),
    FOOD_HALL(
            "§6§lGRANDE REFEITÓRIO IMPERIAL",
            "§7Palácio Central • Salão Comunitário de Banquetes & Cozinha",
            "🍲",
            CampusLayout.FOOD_HALL.aabb(55, 95)
    ),
    ARCANE_LIBRARY(
            "§3§lGRANDE BIBLIOTECA IMPERIAL",
            "§7Ala Oeste • Tomos Históricos, Encantamentos e Manuscritos",
            "📚",
            CampusLayout.LIBRARY.aabb(55, 95)
    ),
    HISTORY_CLASSROOM(
            "§e§lSALA DE HISTÓRIA DO MUNDO & LORE",
            "§7Ala de Humanidades • Relíquias Antigas, Mapas e Cronologia",
            "📜",
            CampusLayout.HISTORY.aabb(55, 90)
    ),
    ZOOLOGY_PAVILION(
            "§6§lZOOLOGIA & FAUNA MÍSTICA",
            "§7Ala de Ciências • Fósseis, Anatomia de Criaturas e Bestiário",
            "🐾",
            CampusLayout.ZOOLOGY.aabb(55, 90)
    ),
    BOTANY_GREENHOUSE(
            "§a§lBOTÂNICA & FLORA ARCANA",
            "§7Ala de Ciências • Estufa de Herbologia e Flores Raras",
            "🌿",
            CampusLayout.GREENHOUSE.aabb(55, 95)
    ),
    MAGIC_SCIENCE_LAB(
            "§9§lCIÊNCIA MÁGICA & ALQUIMIA",
            "§7Ala de Ciências • Teoria da Mana, Transmutação e Poções",
            "🧪",
            CampusLayout.LAB.aabb(55, 95)
    ),
    THEORY_CLASSROOM_1(
            "§b§lSALA DE FILOSOFIA & MENTE",
            "§7Ala de Humanidades • Meditação, Reserva de Mana e Foco",
            "🧠",
            CampusLayout.PHILOSOPHY.aabb(55, 90)
    ),
    THEORY_CLASSROOM_2(
            "§d§lSALA DE ESTRATÉGIA ARCANA & TÁTICA",
            "§7Ala Militar • Ciências Táticas, Guerra Mágica e Relevo 3D",
            "🔮",
            CampusLayout.TACTICS.aabb(55, 90)
    ),
    MARTIAL_WING(
            "§c§lDOJO & DEPARTAMENTO MARCIAL",
            "§7Anfiteatro de Duelos • Tatames, Ringue de Sparring e Armas",
            "⚔️",
            CampusLayout.DOJO.aabb(55, 95)
    ),
    MALE_DORMITORY(
            "§9§lPAVILHÃO DOS DORMITÓRIOS MASCULINOS",
            "§7Residência Oeste • Lounge com Sinuca, Lareira e Quartos",
            "🛏️",
            CampusLayout.DORM_MALE.aabb(55, 95)
    ),
    FEMALE_DORMITORY(
            "§5§lPAVILHÃO DOS DORMITÓRIOS FEMININOS",
            "§7Residência Leste • Lounge com Piano, Chá e Quartos",
            "🌸",
            CampusLayout.DORM_FEMALE.aabb(55, 95)
    ),
    SPORTS_ARENA(
            "§a§lCOMPLEXO ESPORTIVO & ARENA",
            "§7Pátio de Festivais • Atletismo, Desafios de Força e Treinos",
            "🏆",
            CampusLayout.SPORTS.aabb(55, 95)
    ),
    CLOCK_TOWER(
            "§6§lTORRE DO RELÓGIO IMPERIAL",
            "§7Pináculo Nobre • Gabinete Central e Comunicações",
            "⏳",
            CampusLayout.CLOCK_TOWER.aabb(55, 145)
    );

    private final String title;
    private final String subtitle;
    private final String icon;
    private final AABB bounds;

    CampusZone(String title, String subtitle, String icon, AABB bounds) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.bounds = bounds;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIcon() {
        return icon;
    }

    public AABB getBounds() {
        return bounds;
    }

    public boolean contains(double x, double y, double z) {
        return bounds.contains(x, y, z);
    }
}
