package com.heroacademy.common.power;

public enum ClassType {
    WARRIOR("Guerreiro", "Mestres do combate corpo a corpo, lâminas e força marcial."),
    MAGE("Mago", "Conjuradores arcanos, estudiosos de feitiços, runas e artefatos."),
    ROGUE("Ladino", "Especialistas em furtividade, agilidade, emboscadas e armadilhas."),
    HUNTER("Caçador", "Mestres do combate à distância, rastreamento e sobrevivência."),
    TANK("Tanque", "Muralhas vivas focadas em absorção extrema de dano, armadura e regeneração."),
    BEAST("Bestial", "Guerreiros instintivos que canalizam fúria primal, sangramento e selvageria."),
    SHAMAN("Xamã", "Conectados aos espíritos da natureza e totens de aprimoramento e suporte."),
    SUPPORT("Suporte", "Especialistas em maldições contra inimigos, curas maciças e escudos protetores."),
    BRAWLER("Lutador", "Mestres de artes marciais corpo a corpo, socos rápidos e disciplina espiritual."),
    BARD("Bardo", "Artistas que manipulam o ritmo da batalha com dança, música e liderança inspiradora."),
    KNIGHT("Cavaleiro", "Guerreiros nobres blindados com alta presença em campo, vigor e impacto sagrado.");

    private final String displayName;
    private final String description;

    ClassType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
