package com.heroacademy.common.academic;

public enum SchoolGrade {
    SS_PLUS("SS+", "Rank SS+ (Lenda Acadêmica)", 0xFFD700, 98),
    SS("SS", "Rank SS (Prodígio Supremo)", 0xFFA500, 95),
    SS_MINUS("SS-", "Rank SS- (Prodígio)", 0xFF8C00, 92),
    S_PLUS("S+", "Rank S+ (Elite Superior)", 0x55FF55, 88),
    S("S", "Rank S (Elite)", 0x55FF55, 84),
    S_MINUS("S-", "Rank S- (Quase Elite)", 0x55FF77, 80),
    A_PLUS("A+", "Rank A+ (Excelente Alto)", 0x55FFFF, 76),
    A("A", "Rank A (Excelente)", 0x55FFFF, 72),
    A_MINUS("A-", "Rank A- (Muito Bom)", 0x77FFFF, 68),
    B_PLUS("B+", "Rank B+ (Bom Desempenho Alto)", 0x55AAFF, 64),
    B("B", "Rank B (Bom Desempenho)", 0x55AAFF, 60),
    B_MINUS("B-", "Rank B- (Acima da Média)", 0x77BBFF, 56),
    C_PLUS("C+", "Rank C+ (Média Superior)", 0xFFFF55, 52),
    C("C", "Rank C (Regular)", 0xFFFF55, 48),
    C_MINUS("C-", "Rank C- (Média Baixa)", 0xFFFF77, 44),
    D_PLUS("D+", "Rank D+ (Em Alerta)", 0xFF8855, 40),
    D("D", "Rank D (Abaixo da Média)", 0xFF6644, 35),
    D_MINUS("D-", "Rank D- (Quase Reprovado)", 0xFF4444, 0);

    private final String gradeSymbol;
    private final String title;
    private final int colorHex;
    private final int minScore;

    SchoolGrade(String gradeSymbol, String title, int colorHex, int minScore) {
        this.gradeSymbol = gradeSymbol;
        this.title = title;
        this.colorHex = colorHex;
        this.minScore = minScore;
    }

    public String getGradeSymbol() {
        return gradeSymbol;
    }

    public String getTitle() {
        return title;
    }

    public int getColorHex() {
        return colorHex;
    }

    public int getMinScore() {
        return minScore;
    }

    public static SchoolGrade fromScore(int score) {
        for (SchoolGrade g : values()) {
            if (score >= g.minScore) {
                return g;
            }
        }
        return D_MINUS;
    }
}
