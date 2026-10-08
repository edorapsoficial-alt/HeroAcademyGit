package com.heroacademy.common.power;

public enum AbilitySlot {
    SLOT_Z("Z", "Habilidade Primária", 0),
    SLOT_X("X", "Habilidade Secundária / Mobilidade", 1),
    SLOT_C("C", "Habilidade Tática / Defesa", 2),
    SLOT_V("V", "Habilidade Especial em Área", 3),
    SLOT_G("G", "Despertar Supremo (Awakening)", 4);

    private final String keyName;
    private final String roleDescription;
    private final int index;

    AbilitySlot(String keyName, String roleDescription, int index) {
        this.keyName = keyName;
        this.roleDescription = roleDescription;
        this.index = index;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public int getIndex() {
        return index;
    }

    public static AbilitySlot fromIndex(int index) {
        for (AbilitySlot slot : values()) {
            if (slot.getIndex() == index) {
                return slot;
            }
        }
        return SLOT_Z;
    }
}
