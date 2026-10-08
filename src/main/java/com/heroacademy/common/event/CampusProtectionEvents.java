package com.heroacademy.common.event;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.world.CampusLayout;
import com.heroacademy.common.world.CampusStructureBuilder;
import com.heroacademy.common.world.CampusTeleporter;
import com.heroacademy.common.world.ModDimensions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class CampusProtectionEvents {

    @SubscribeEvent
    public static void onLevelLoad(final LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) {
                // Aplica e reforça as configurações da WorldBorder na Dimensão do Campus
                CampusTeleporter.setupWorldBorder(serverLevel);
                // Constrói e decora salas caso necessário
                CampusStructureBuilder.buildCampusIfNeeded(serverLevel);
                HeroAcademy.LOGGER.info("Hero Academy: WorldBorder e proteção ativados para o Campus Imperial Destiny.");
            }
        }
    }

    /**
     * Barreira arcana ativa: impede que jogadores saiam da dimensão além das montanhas.
     */
    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.level().dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) return;
        if (player.isSpectator()) return;
        if (player.tickCount % 10 != 0) return;

        double dist = Math.max(
                Math.abs(player.getX() - CampusLayout.BORDER_CENTER_X),
                Math.abs(player.getZ() - CampusLayout.BORDER_CENTER_Z));

        double barrierDist = CampusLayout.BORDER_SIZE / 2.0;
        if (dist > barrierDist) {
            ServerLevel level = (ServerLevel) player.level();
            player.teleportTo(level, CampusLayout.SPAWN_X, CampusLayout.G, CampusLayout.SPAWN_Z, Set.of(), 180.0f, 0.0f);
            level.playSound(null, CampusLayout.SPAWN_X, CampusLayout.G, CampusLayout.SPAWN_Z,
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.8f);
            level.sendParticles(ParticleTypes.PORTAL, CampusLayout.SPAWN_X, CampusLayout.G + 1, CampusLayout.SPAWN_Z,
                    40, 0.5, 0.8, 0.5, 0.1);
            player.displayClientMessage(Component.literal(
                    "§d✦ A Barreira Arcana brilha... e te devolve suavemente à Praça Imperial!"), true);
        } else if (dist > barrierDist - 15.0 && player.tickCount % 40 == 0) {
            player.displayClientMessage(Component.literal(
                    "§5⚠ As Montanhas Arcanas selam o vale — uma barreira mágica impede o avanço."), true);
        }
    }

    /**
     * Bloqueia a verificação de posição de spawn de monstros no Campus.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMobPositionCheck(final MobSpawnEvent.PositionCheck event) {
        if (event.getLevel() != null && event.getLevel().getLevel() != null) {
            if (event.getLevel().getLevel().dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) {
                if (isHostile(event.getEntity())) {
                    event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                }
            }
        }
    }

    /**
     * Como garantia extra caso qualquer mob hostil tente entrar ou ser sumonado na dimensão.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinCampus(final EntityJoinLevelEvent event) {
        if (event.getLevel() != null && event.getLevel().dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) {
            if (isHostile(event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean isHostile(net.minecraft.world.entity.Entity entity) {
        if (entity == null) return false;
        if (entity instanceof Enemy) return true;
        return entity.getType().getCategory() == MobCategory.MONSTER;
    }
}
