package com.heroacademy.common.academic;

import net.minecraft.world.level.Level;

public class AcademicCalendar {
    public static final int DAYS_PER_PERIOD = 180;
    public static final int DAYS_PER_WEEK = 7;

    public static int getPeriod(long gameTime) {
        long totalDays = gameTime / 24000L;
        return (int) (totalDays / DAYS_PER_PERIOD) + 1;
    }

    public static int getDayOfPeriod(long gameTime) {
        long totalDays = gameTime / 24000L;
        return (int) (totalDays % DAYS_PER_PERIOD) + 1;
    }

    public static int getWeekNumber(long gameTime) {
        int dayOfPeriod = getDayOfPeriod(gameTime);
        return ((dayOfPeriod - 1) / DAYS_PER_WEEK) + 1;
    }

    public static int getDayOfWeek(long gameTime) {
        long totalDays = gameTime / 24000L;
        return (int) (totalDays % DAYS_PER_WEEK) + 1; // 1 a 7
    }

    public static boolean isSchoolDay(long gameTime) {
        int dayOfWeek = getDayOfWeek(gameTime);
        return dayOfWeek >= 1 && dayOfWeek <= 5;
    }

    public static boolean isFreeDay(long gameTime) {
        return getDayOfWeek(gameTime) == 6;
    }

    public static boolean isPortalExpeditionDay(long gameTime) {
        return getDayOfWeek(gameTime) == 7;
    }

    public static String getDayDescription(long gameTime) {
        int day = getDayOfWeek(gameTime);
        return switch (day) {
            case 1 -> "Segunda-feira (Aula Teórica & Alquimia)";
            case 2 -> "Terça-feira (Controle de Mana & Feitiços)";
            case 3 -> "Quarta-feira (Treino Físico & Esportes)";
            case 4 -> "Quinta-feira (Sala de Armas & Técnicas)";
            case 5 -> "Sexta-feira (Especialização de Classes)";
            case 6 -> "Sábado (Dia Livre & Convivência)";
            case 7 -> "Domingo (EXPEDIÇÃO DE PORTAL ABERTA!)";
            default -> "Dia Letivo";
        };
    }
}
