package com.heroacademy.common.power;

public enum ClassBranch {
    // GUERREIRO
    SAMURAI(ClassType.WARRIOR, "Samurai", "Focado em acertos críticos e golpes letais. HP mediano e defesas baixas."),
    GLADIATOR(ClassType.WARRIOR, "Gladiador", "Ataque e defesa equilibrados com escudo. HP mediano."),
    MERCENARY(ClassType.WARRIOR, "Mercenário", "Ataque e agilidade elevadas. HP e defesas medianas."),

    // MAGO
    SCRIBE(ClassType.MAGE, "Escriba", "Conjurador focado em tomos e grimórios com magias arcanas complexas."),
    RUNOLOGIST(ClassType.MAGE, "Runólogo", "Gravação e uso de pedras rúnicas pré-prontas com cargas de feitiço."),
    ENCHANTER(ClassType.MAGE, "Encantador", "Uso de cajados e orbes mágicos carregados com feitiços contínuos."),

    // LADINO
    NINJA(ClassType.ROGUE, "Ninja", "Dano em rajadas rápidas e técnicas acrobáticas de alta mobilidade."),
    SABOTEUR(ClassType.ROGUE, "Sabotador", "Preparação de armadilhas no terreno e dano contínuo por status tóxicos."),
    SHADOW(ClassType.ROGUE, "Sombra", "Alta furtividade, invisibilidade e táticas de bater e correr."),

    // CAÇADOR
    SNIPER(ClassType.HUNTER, "Sniper", "Foco total em manter extrema distância e causar altíssimo dano pontual."),
    RANGER(ClassType.HUNTER, "Ranger", "Dano equilibrado à média distância com boa mobilidade e defesa."),
    PREDATOR(ClassType.HUNTER, "Predador", "Especializado em caçar grupos específicos de criaturas definidos no curso."),

    // TANQUE
    COLOSSUS(ClassType.TANK, "Colosso", "Altíssimas quantidades de Vida Máxima (HP colossal bruto)."),
    CARAPACE(ClassType.TANK, "Carapaça", "Altíssimas quantidades de armadura física e resistência mágica."),
    IMMORTAL(ClassType.TANK, "Imortal", "Altíssimas taxas de regeneração de vida contínua em combate."),

    // BESTIAL
    BERSERKER(ClassType.BEAST, "Berserker", "Fúria destrutiva que escala o poder de ataque conforme recebe dano."),
    BLOODTHIRSTY(ClassType.BEAST, "Sanguinário", "Dano cortante com status de sangramento (bleed) e boa regeneração."),
    BEAST(ClassType.BEAST, "Fera", "Altas parcelas de dano bruto concentrado com tempos de recarga maiores."),

    // XAMÃ
    SPIRITUALIST(ClassType.SHAMAN, "Espírita", "Magias espirituais de fortalecimento e suporte de status para aliados."),
    TRIBALIST(ClassType.SHAMAN, "Tribalista", "Buffs coletivos de atributos passivos e recuperação contínua de vigor."),
    HEALER_SHAMAN(ClassType.SHAMAN, "Curandeiro", "Rituais totêmicos e magias de regeneração de vida em área."),

    // SUPORTE
    WARLOCK(ClassType.SUPPORT, "Feiticeiro", "Aplicação de maldições, enfraquecimento e debuffs profundos em inimigos."),
    MEDIC(ClassType.SUPPORT, "Médico", "Curas maciças em grandes blocos de HP com tempos de recarga médios a altos."),
    DEFENDER(ClassType.SUPPORT, "Defensor", "Magias de escudos de energia e amplificação das resistências do time."),

    // LUTADOR
    MARTIAL_ARTIST(ClassType.BRAWLER, "Artista Marcial", "Aprimoramento corporal completo e técnicas avançadas de combate desarmado."),
    PUGILIST(ClassType.BRAWLER, "Pugilista", "Sequências rápidas de socos com atordoamentos e combos sucessivos."),
    MONK(ClassType.BRAWLER, "Monge", "Equilíbrio espiritual, canalização de energia interior e foco defensivo."),

    // BARDO
    DANCER(ClassType.BARD, "Dançarino", "Altíssima taxa de esquiva passiva e melhoria constante de velocidade de movimento."),
    MUSICIAN(ClassType.BARD, "Músico", "Acordes sonoros com controle de grupo em área e manipulação de atributos."),
    LEADER(ClassType.BARD, "Líder", "Presença inspiradora, auras constantes de fortalecimento para a equipe e bom HP."),

    // CAVALEIRO
    TITAN(ClassType.KNIGHT, "Titã", "Grande porte físico com alto HP, dano mediano e defesas medianas a baixas."),
    IRONCLAD(ClassType.KNIGHT, "Encouraçado", "Blindagem pesada com altíssima armadura física e dano mediano."),
    PUNISHER(ClassType.KNIGHT, "Justiceiro", "Focado em punição ofensiva com alto dano e HP, e defesas moderadas.");

    private final ClassType parentClass;
    private final String displayName;
    private final String description;

    ClassBranch(ClassType parentClass, String displayName, String description) {
        this.parentClass = parentClass;
        this.displayName = displayName;
        this.description = description;
    }

    public ClassType getParentClass() {
        return parentClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
