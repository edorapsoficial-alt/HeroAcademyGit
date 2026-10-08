package com.heroacademy.common.academic;

import com.heroacademy.HeroAcademy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class SchoolSchedule {

    public enum SchoolPeriod {
        THEORY_CLASS("Aula Teórica (Manhã)", "§e🔔 O sinal tocou! Início das Aulas Teóricas nas salas de aula."),
        PRACTICAL_TRAINING("Treino Prático (Tarde)", "§6⚔ O sinal tocou! Início dos Treinamentos Práticos nas Arenas."),
        CURFEW_DORMITORY("Toque de Recolher (Noite)", "§9🌙 O sinal tocou! Retornem aos dormitórios de suas turmas para descansar.");

        private final String title;
        private final String announcement;

        SchoolPeriod(String title, String announcement) {
            this.title = title;
            this.announcement = announcement;
        }

        public String getTitle() { return title; }
        public String getAnnouncement() { return announcement; }
    }

    private static SchoolPeriod currentPeriod = SchoolPeriod.THEORY_CLASS;

    @SubscribeEvent
    public static void onLevelTick(final LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == ServerLevel.OVERWORLD) {
            long timeOfDay = level.getDayTime() % 24000;
            SchoolPeriod newPeriod;

            if (timeOfDay < 6000) {
                newPeriod = SchoolPeriod.THEORY_CLASS;
            } else if (timeOfDay < 12000) {
                newPeriod = SchoolPeriod.PRACTICAL_TRAINING;
            } else {
                newPeriod = SchoolPeriod.CURFEW_DORMITORY;
            }

            if (newPeriod != currentPeriod) {
                currentPeriod = newPeriod;
                broadcastSchoolBell(level, currentPeriod);
            }
        }
    }

    private static void broadcastSchoolBell(ServerLevel level, SchoolPeriod period) {
        level.players().forEach(p -> {
            p.playNotifySound(SoundEvents.BELL_BLOCK, SoundSource.RECORDS, 2.0f, 1.0f);
            p.sendSystemMessage(Component.literal("§l[ACADEMIA] " + period.getAnnouncement()));
        });
    }

    public static SchoolPeriod getCurrentPeriod() {
        return currentPeriod;
    }
}
