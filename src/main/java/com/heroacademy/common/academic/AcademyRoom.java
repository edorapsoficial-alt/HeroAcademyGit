package com.heroacademy.common.academic;

import com.heroacademy.common.power.ClassType;
import com.heroacademy.common.power.TrainingAttribute;

public enum AcademyRoom {
    // 11 SALAS ESPECÍFICAS DE CADA CLASSE
    WARRIOR_DOJO("Dojo de Artes Marciais & Arsenal", ClassType.WARRIOR, TrainingAttribute.TECHNIQUE,
            "Tatames, suportes com katanas, espadas e manequins de teste de impacto."),
    MAGE_SANCTUM("Torre do Sanctum Arcana", ClassType.MAGE, TrainingAttribute.MIND,
            "Círculos rúnicos no piso, mesas de escriba, estantes de pergaminhos e orbes mágicos."),
    ROGUE_CHAMBER("Câmara das Sombras & Labirinto Furtivo", ClassType.ROGUE, TrainingAttribute.TECHNIQUE,
            "Penumbra, vigas elevadas para acrobacias, alvos de adagas e bancadas de armadilhas."),
    HUNTER_RANGE("Estande de Tiro & Pavilhão do Rastreador", ClassType.HUNTER, TrainingAttribute.TECHNIQUE,
            "Galeria de tiro ao alvo à longa distância com alvos móveis e troféus de caça."),
    TANK_BASTION("Bastião da Fortaleza & Câmara de Blindagem", ClassType.TANK, TrainingAttribute.BODY,
            "Paredes de pedra reforçada, bigornas pesadas e prensas de resistência a impactos."),
    BEAST_DEN("Covil Primal", ClassType.BEAST, TrainingAttribute.BODY,
            "Arena rústica selvagem com troncos arranhados, fogueiras e totens de fúria."),
    SHAMAN_GROVE("Bosque dos Espíritos Ancestrais", ClassType.SHAMAN, TrainingAttribute.MIND,
            "Jardim botânico sagrado com claraboia, pedras místicas e totens espirituais."),
    SUPPORT_INFIRMARY("Pavilhão de Proteção & Enfermaria", ClassType.SUPPORT, TrainingAttribute.MIND,
            "Leitos mágicos com cortinas, altares de bênção e frascos de elixires medicinais."),
    BRAWLER_RING("Ringue de Combate Desarmado", ClassType.BRAWLER, TrainingAttribute.BODY,
            "Ringue com cordas, tatames, sacos de pancada de couro e manequins de golpes sucessivos."),
    BARD_CONSERVATORY("Conservatório dos Menestréis & Anfiteatro", ClassType.BARD, TrainingAttribute.MIND,
            "Sala acústica com piso de madeira, instrumentos musicais mágicos e palco para dança e ritmos."),
    KNIGHT_HALL("Salão da Ordem da Cavalaria", ClassType.KNIGHT, TrainingAttribute.TECHNIQUE,
            "Salão imponente com estandartes heráldicos, pedestais com armaduras completas e altar de juramentos."),

    // SALAS COMUNS E INSTALAÇÕES GERAIS
    ALCHEMY_LAB("Laboratório de Alquimia", null, TrainingAttribute.MIND,
            "Caldeirões, destiladores e bancadas de reagentes para treinos da Mente."),
    MANA_CONTROL_ROOM("Sala de Controle de Mana", null, TrainingAttribute.MIND,
            "Círculos rúnicos de meditação para ampliação de reserva e regeneração de Mana."),
    LIBRARY("Biblioteca Arcana Geral", null, TrainingAttribute.MIND,
            "Grandes estantes de livros sobre história do mundo, monstros e teorias de magia."),
    FOOD_HALL("Grande Salão de Refeições (Food Hall)", null, null,
            "Refeitório central com mesas comunais, chefs NPCs, fofocas de lore e comércio escolar."),
    SPORTS_ARENA("Complexo Esportivo & Campo de Atletismo", null, TrainingAttribute.BODY,
            "Pistas de corrida, esteiras e obstáculos para treinos físicos e festivais interclasses."),
    DORMITORIES("Prédio dos Dormitórios", null, null,
            "Alas residenciais das turmas para descanso e regeneração do efeito Revitalizado."),
    PORTAL_COURTYARD("Pátio dos Portais Dimensionais", null, null,
            "Santuário com portais que se ativam no Dia 7 para expedições e desafios de masmorras.");

    private final String displayName;
    private final ClassType associatedClass;
    private final TrainingAttribute primaryAttribute;
    private final String description;

    AcademyRoom(String displayName, ClassType associatedClass, TrainingAttribute primaryAttribute, String description) {
        this.displayName = displayName;
        this.associatedClass = associatedClass;
        this.primaryAttribute = primaryAttribute;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ClassType getAssociatedClass() {
        return associatedClass;
    }

    public TrainingAttribute getPrimaryAttribute() {
        return primaryAttribute;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClassRoom() {
        return associatedClass != null;
    }
}
