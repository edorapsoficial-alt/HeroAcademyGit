package com.heroacademy.common.power;

public enum StatType {
    // CORPO (6)
    BODY_HP("Vida Máxima (HP)", TrainingAttribute.BODY, "+2 HP (+1 Coração) por ponto investido"),
    BODY_SPEED("Velocidade de Movimento", TrainingAttribute.BODY, "+1.5% de velocidade de corrida por ponto"),
    BODY_DEFENSE("Resistência Física", TrainingAttribute.BODY, "+1% de mitigação de dano físico por ponto"),
    BODY_STAMINA("Vigor Máximo (Stamina)", TrainingAttribute.BODY, "+10 de vigor máximo para golpes físicos e corrida"),
    BODY_STAMINA_REGEN("Recuperação de Vigor", TrainingAttribute.BODY, "+15% de velocidade de recuperação de vigor por segundo"),
    BODY_STRENGTH("Força Física", TrainingAttribute.BODY, "+2.5% de bônus de dano base físico (scaling com Artes)"),

    // MENTE (4)
    MIND_MAX_MANA("Mana Máxima", TrainingAttribute.MIND, "+15 de mana máxima para conjurações e armas arcanas"),
    MIND_MANA_REGEN("Regeneração de Mana", TrainingAttribute.MIND, "+0.15 de mana regenerada por segundo"),
    MIND_CDR("Redução de Recarga (CDR)", TrainingAttribute.MIND, "Aceleração de recarga calculada de forma assintótica"),
    MIND_MAGIC_DAMAGE("Dano Mágico", TrainingAttribute.MIND, "+2.5% de bônus de dano mágico base (scaling com Artes)"),

    // ARTES (10)
    TECHNIQUE_WESTERN("Armas Ocidentais (Espadas & Machados)", TrainingAttribute.TECHNIQUE, "+6% de dano e impacto com Espadas e Machados"),
    TECHNIQUE_POLEARMS("Armas de Haste (Lanças)", TrainingAttribute.TECHNIQUE, "+6% de dano e perfuração com Lanças de combate"),
    TECHNIQUE_EASTERN("Armas Orientais (Katanas & Tonfas)", TrainingAttribute.TECHNIQUE, "+6% de dano crítico veloz com Katanas e Tonfas"),
    TECHNIQUE_EXOTIC("Armas Exóticas (Foices)", TrainingAttribute.TECHNIQUE, "+6% de dano cortante e varredura ampla com Foices"),
    TECHNIQUE_MARTIAL("Combate Marcial (Desarmado)", TrainingAttribute.TECHNIQUE, "+2.0 de dano por soco desarmado e combos corporais"),
    TECHNIQUE_DAGGERS("Adagas (Ladino / Emboscada)", TrainingAttribute.TECHNIQUE, "+6% de dano rápido e bônus crítico com Adagas"),
    TECHNIQUE_ORBS("Orbes (Mago Encantador)", TrainingAttribute.TECHNIQUE, "+6% de dano e canalização mágica com Orbes"),
    TECHNIQUE_STAVES("Cajados (Mago Encantador)", TrainingAttribute.TECHNIQUE, "+6% de alcance e projeção arcana contínua com Cajados"),
    TECHNIQUE_TOMES("Tomos (Mago Escriba)", TrainingAttribute.TECHNIQUE, "+6% de amplificação em feitiços escritos e grimórios"),
    TECHNIQUE_RUNES("Runomancia (Mago Runólogo)", TrainingAttribute.TECHNIQUE, "+6% de potência e durabilidade em runas mágicas gravadas");

    private final String displayName;
    private final TrainingAttribute parentAttribute;
    private final String description;

    StatType(String displayName, TrainingAttribute parentAttribute, String description) {
        this.displayName = displayName;
        this.parentAttribute = parentAttribute;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TrainingAttribute getParentAttribute() {
        return parentAttribute;
    }

    public String getDescription() {
        return description;
    }
}
