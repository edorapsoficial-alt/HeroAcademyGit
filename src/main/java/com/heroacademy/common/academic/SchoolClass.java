package com.heroacademy.common.academic;

public enum SchoolClass {
    NONE("Sem Turma"),
    CLASS_1A("Turma 1-A"),
    CLASS_1B("Turma 1-B");

    private final String displayName;

    SchoolClass(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
