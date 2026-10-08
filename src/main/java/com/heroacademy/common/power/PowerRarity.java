package com.heroacademy.common.power;

public enum PowerRarity {
    COMMON("Comum", 0xAAAAAA, 1.0f),
    UNCOMMON("Incomum", 0x55FF55, 1.25f),
    RARE("Raro", 0x55FFFF, 1.5f),
    EPIC("Épico", 0xAA00AA, 2.0f),
    LEGENDARY("Lendário", 0xFFAA00, 3.0f);

    private final String displayName;
    private final int colorHex;
    private final float powerMultiplier;

    PowerRarity(String displayName, int colorHex, float powerMultiplier) {
        this.displayName = displayName;
        this.colorHex = colorHex;
        this.powerMultiplier = powerMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColorHex() {
        return colorHex;
    }

    public float getPowerMultiplier() {
        return powerMultiplier;
    }

    public PowerRarity getNext() {
        return switch (this) {
            case COMMON -> UNCOMMON;
            case UNCOMMON -> RARE;
            case RARE -> EPIC;
            case EPIC, LEGENDARY -> LEGENDARY;
        };
    }
}
