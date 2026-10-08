package com.heroacademy.common.event;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.world.CampusStructureBuilder;
import com.heroacademy.common.world.CampusTeleporter;
import com.heroacademy.common.world.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

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
