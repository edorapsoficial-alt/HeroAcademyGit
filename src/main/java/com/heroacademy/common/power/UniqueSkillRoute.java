package com.heroacademy.common.power;

public enum UniqueSkillRoute {
    NONE("Sem Especialização", "Ainda não escolheu uma rota de domínio."),
    ROUTE_A("Rota A - Ofensiva / Destrutiva", "Foco em maximizar poder bruto, dano e impacto direto dos golpes."),
    ROUTE_B("Rota B - Mobilidade / Controle", "Foco em velocidade, reposicionamento rápido e controle de inimigos."),
    ROUTE_C("Rota C - Despertar Lendário / Suporte", "Foco em potencial supremo, buffs profundos e ativação do modo lendário.");

    private final String title;
    private final String description;

    UniqueSkillRoute(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
