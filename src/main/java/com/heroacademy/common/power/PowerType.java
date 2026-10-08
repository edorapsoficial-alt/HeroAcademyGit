package com.heroacademy.common.power;

public enum PowerType {
    NONE("Sem Poder", "Ainda não despertou sua individualidade mágica."),
    CHRONOKINESIS("Cronocinese", "Controle do tempo, aceleração pessoal e paralisação temporal (Time Stop)."),
    SHADOW_MANIPULATION("Manipulação de Sombras", "Uso da escuridão para golpes perfurantes, teleporte sombrio e expansão de domínio."),
    DIVINE_LIGHTNING("Relâmpago Divino", "Descargas elétricas celestiais, velocidade hiper-reativa e tempestades."),
    MIRACLE_HEALING("Cura Milagrosa", "Regeneração celular instantânea, auras de revitalização e barreiras santificadas."),
    TELEKINESIS("Telecinese", "Controle de campos de força, repulsão cinética e vórtices gravitacionais.");

    private final String displayName;
    private final String description;

    PowerType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static PowerType getRandomCombatPower() {
        PowerType[] values = {
            CHRONOKINESIS,
            SHADOW_MANIPULATION,
            DIVINE_LIGHTNING,
            MIRACLE_HEALING,
            TELEKINESIS
        };
        int index = (int) (Math.random() * values.length);
        return values[index];
    }
}
