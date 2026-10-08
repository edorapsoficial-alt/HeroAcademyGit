package com.heroacademy.client;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.academic.CampusWaypoint;
import com.heroacademy.common.world.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = HeroAcademy.MODID, value = Dist.CLIENT)
public class CampusNavigationClient {

    private static CampusWaypoint activeWaypoint = null;
    private static int tickCounter = 0;

    public static CampusWaypoint getActiveWaypoint() {
        return activeWaypoint;
    }

    public static void setWaypoint(CampusWaypoint waypoint) {
        activeWaypoint = waypoint;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (waypoint != null) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 1.0f));
                mc.player.displayClientMessage(
                        Component.literal("§a🧭 [GPS Mágico] §fRota traçada rumo a: " + waypoint.getIcon() + " §e§l" + waypoint.getName()),
                        false
                );
            } else {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 0.7f));
                mc.player.displayClientMessage(
                        Component.literal("§c🧭 [GPS Mágico] §7Navegação do Campus desativada."),
                        false
                );
            }
        }
    }

    public static void clearWaypoint() {
        setWaypoint(null);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        if (activeWaypoint == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        // Atua preferencialmente no Campus ou no mapa da Academia
        tickCounter++;

        Vec3 playerPos = player.position();
        Vec3 targetPos = new Vec3(
                activeWaypoint.getTargetPos().getX() + 0.5,
                activeWaypoint.getTargetPos().getY() + 0.5,
                activeWaypoint.getTargetPos().getZ() + 0.5
        );

        double dist = activeWaypoint.getDistanceTo(playerPos);

        // Chegada ao destino (raio de 5.5 blocos)
        if (dist <= 5.5) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.4f, 1.0f));
            mc.gui.setTimes(10, 40, 20);
            mc.gui.setTitle(Component.literal("§a✨ Destino Alcançado!"));
            mc.gui.setSubtitle(Component.literal(activeWaypoint.getIcon() + " §f" + activeWaypoint.getName()));
            player.displayClientMessage(
                    Component.literal("§a✨ Você chegou ao seu destino: " + activeWaypoint.getIcon() + " §e§l" + activeWaypoint.getName() + "§a!"),
                    false
            );
            activeWaypoint = null;
            return;
        }

        // Rastro de Partículas Luminosas no chão (a cada 12 ticks)
        if (tickCounter % 12 == 0) {
            spawnTrailParticles(mc, playerPos, targetPos);
        }

        // HUD / Actionbar direcional (a cada 10 ticks)
        if (tickCounter % 10 == 0) {
            String arrow = getDirectionArrow(player, targetPos);
            String message = "§6🧭 §f" + activeWaypoint.getIcon() + " " + activeWaypoint.getName()
                    + " §7• §e" + (int) dist + "m §a" + arrow;
            player.displayClientMessage(Component.literal(message), true);
        }
    }

    private static void spawnTrailParticles(Minecraft mc, Vec3 playerPos, Vec3 targetPos) {
        if (mc.level == null) return;

        Vec3 diff = targetPos.subtract(playerPos);
        Vec3 dir = new Vec3(diff.x, 0, diff.z).normalize();

        // Projeta 10 pontos de luz à frente na direção do destino no chão
        for (int i = 2; i <= 14; i += 2) {
            double px = playerPos.x + dir.x * i;
            double pz = playerPos.z + dir.z * i;
            double py = playerPos.y + 0.2;

            mc.level.addParticle(ParticleTypes.GLOW, px, py, pz, 0, 0.02, 0);
            if (i % 4 == 0) {
                mc.level.addParticle(ParticleTypes.END_ROD, px, py + 0.1, pz, 0, 0.01, 0);
            }
        }
    }

    private static String getDirectionArrow(LocalPlayer player, Vec3 targetPos) {
        double dx = targetPos.x - player.getX();
        double dz = targetPos.z - player.getZ();

        double angleToTarget = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double diff = Mth.wrapDegrees(angleToTarget - player.getYRot());

        if (Math.abs(diff) < 22.5) {
            return "⬆ [Frente]";
        } else if (diff >= 22.5 && diff < 67.5) {
            return "⬈ [Diag. Direita]";
        } else if (diff >= 67.5 && diff < 112.5) {
            return "➔ [Direita]";
        } else if (diff >= 112.5 && diff < 157.5) {
            return "⬊ [Trás Direita]";
        } else if (Math.abs(diff) >= 157.5) {
            return "⬇ [Atrás]";
        } else if (diff <= -112.5 && diff > -157.5) {
            return "⬋ [Trás Esquerda]";
        } else if (diff <= -67.5 && diff > -112.5) {
            return "⬅ [Esquerda]";
        } else {
            return "⬉ [Diag. Esquerda]";
        }
    }
}
