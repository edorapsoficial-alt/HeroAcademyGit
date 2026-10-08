package com.heroacademy.common.academic;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.power.TrainingAttribute;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class ClassroomPresenceTracker {
    public static final int REQUIRED_PRESENCE_TICKS = 400; // ~20 segundos de permanência concentrada na aula

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Verifica se é dia letivo (dias 1 a 5 da semana) e período de aula teórica
            long gameTime = player.serverLevel().getDayTime();
            boolean isSchoolDay = AcademicCalendar.isSchoolDay(gameTime);
            boolean isClassTime = SchoolSchedule.getCurrentPeriod() == SchoolSchedule.SchoolPeriod.THEORY_CLASS;

            if (isSchoolDay && isClassTime) {
                HeroData data = player.getData(ModAttachments.HERO_DATA);

                // Jogador absorve a aula a cada tick se não estiver correndo loucamente
                data.addClassPresenceTicks(1);

                // A cada 100 ticks (5s) dentro da aula, ganha partículas de aprendizado
                if (data.getClassPresenceTicks() % 100 == 0) {
                    player.serverLevel().sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.5, player.getZ(), 5, 0.3, 0.3, 0.3, 0.05);
                }

                // Ao atingir o tempo integral da aula
                if (data.getClassPresenceTicks() == REQUIRED_PRESENCE_TICKS) {
                    data.incrementWeeklyAttendance();
                    data.addMastery(50);
                    data.addClassMasteryPoints(2);

                    // Concede XP no atributo correspondente à matéria do dia
                    int dayOfWeek = AcademicCalendar.getDayOfWeek(gameTime);
                    TrainingAttribute trained = switch (dayOfWeek) {
                        case 1, 2 -> TrainingAttribute.MIND;
                        case 3 -> TrainingAttribute.BODY;
                        case 4 -> TrainingAttribute.TECHNIQUE;
                        default -> TrainingAttribute.MIND;
                    };
                    data.addTrainingXp(trained, 100);

                    player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);
                    player.sendSystemMessage(Component.literal("§a§l✔ [PRESENÇA CONFIRMADA NA AULA]"));
                    player.sendSystemMessage(Component.literal("§eVocê assistiu a aula completa! +100 XP de " + trained.getDisplayName() + " e +2 Pontos de Classe!"));
                }
            } else {
                // Fora do horário de aula, reseta o contador de presença da lição
                HeroData data = player.getData(ModAttachments.HERO_DATA);
                if (data.getClassPresenceTicks() > 0) {
                    data.resetClassPresenceTicks();
                }
            }
        }
    }
}
