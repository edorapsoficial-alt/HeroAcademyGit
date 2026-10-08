package com.heroacademy.common.power;

public enum TrainingAttribute {
    BODY("Corpo", "Desenvolvimento físico, HP máximo, vigor, força e regeneração muscular.", 0xFF5555),
    MIND("Mente", "Poder arcano, reserva de Mana máxima, controle e dano mágico.", 0x55FFFF),
    TECHNIQUE("Artes", "Maestrias dedicadas de armas brancas, marciais e catalisadores mágicos.", 0xFFAA00);

    private final String displayName;
    private final String description;
    private final int colorHex;

    TrainingAttribute(String displayName, String description, int colorHex) {
        this.displayName = displayName;
        this.description = description;
        this.colorHex = colorHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getColorHex() {
        return colorHex;
    }

    public String getColorTag() {
        return switch (this) {
            case BODY -> "§c";
            case MIND -> "§b";
            case TECHNIQUE -> "§6";
        };
    }
}
