package com.heroacademy.common.world;

import com.heroacademy.HeroAcademy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * Orquestrador da construção procedural da Academia Imperial Destiny.
 * Divide a construção em etapas processadas tick a tick para garantir fluidez total e zero lag.
 */
@EventBusSubscriber(modid = HeroAcademy.MODID)
public class CampusStructureBuilder {

    /** Marcador subterrâneo no leito de rocha para verificação de conclusão do mapa. */
    public static final BlockPos MARKER_POS = new BlockPos(0, 3, 0);

    private record BuildStep(String name, Consumer<BlockKit> task) {}

    private static final Queue<BuildStep> BUILD_QUEUE = new ArrayDeque<>();
    private static int totalSteps = 0;
    private static int currentStepIndex = 0;
    private static boolean isBuilding = false;

    public static boolean isBuilding() {
        return isBuilding || !BUILD_QUEUE.isEmpty();
    }

    public static void buildCampusIfNeeded(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) return;
        if (isBuilding()) return;

        // Se o bloco marcador já existe, o mapa já foi gerado
        if (level.getBlockState(MARKER_POS).is(Blocks.GOLD_BLOCK)) {
            return;
        }

        startBuild(level);
    }

    public static void forceRebuild(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) return;
        level.setBlock(MARKER_POS, Blocks.STONE.defaultBlockState(), 18);
        BUILD_QUEUE.clear();
        isBuilding = false;
        startBuild(level);
    }

    private static void startBuild(ServerLevel level) {
        BUILD_QUEUE.clear();

        // 1. Vias e Praça
        BUILD_QUEUE.add(new BuildStep("Estradas e Avenidas Imperiais", CampusBuildings::buildRoads));
        BUILD_QUEUE.add(new BuildStep("Praça Monumental e Fonte Central", CampusBuildings::buildPlaza));
        BUILD_QUEUE.add(new BuildStep("Muralha Perimetral e Portão Sul", CampusBuildings::buildPerimeterWall));

        // 2. Grandes Estruturas Centrais
        BUILD_QUEUE.add(new BuildStep("Grande Hall Imperial", CampusBuildings::buildGrandHall));
        BUILD_QUEUE.add(new BuildStep("Torre do Relógio Imperial", CampusBuildings::buildClockTower));
        BUILD_QUEUE.add(new BuildStep("Grande Biblioteca Imperial", CampusBuildings::buildLibrary));
        BUILD_QUEUE.add(new BuildStep("Grande Refeitório Imperial", CampusBuildings::buildFoodHall));

        // 3. Ciências e Estudo
        BUILD_QUEUE.add(new BuildStep("Laboratório de Ciência Mágica & Alquimia", CampusBuildings::buildLab));
        BUILD_QUEUE.add(new BuildStep("Estufa Botânica Arcana", CampusBuildings::buildGreenhouse));
        BUILD_QUEUE.add(new BuildStep("Bloco Acadêmico (4 Salas de Aula)", CampusBuildings::buildAcademicBlock));

        // 4. Treinamento e Esportes
        BUILD_QUEUE.add(new BuildStep("Dojo & Departamento Marcial", CampusBuildings::buildDojo));
        BUILD_QUEUE.add(new BuildStep("Complexo Esportivo & Arena", CampusBuildings::buildSportsArena));

        // 5. Residências
        BUILD_QUEUE.add(new BuildStep("Alojamentos & Dormitórios", CampusBuildings::buildDorms));

        // 6. Docas e Portal
        BUILD_QUEUE.add(new BuildStep("Docas do Dirigível & Portal de Retorno", CampusBuildings::buildDocks));

        // 7. Paisagismo
        BUILD_QUEUE.add(new BuildStep("Paisagismo e Bosques (Norte)", kit -> CampusBuildings.buildLandscaping(kit, -170, -60)));
        BUILD_QUEUE.add(new BuildStep("Paisagismo e Bosques (Centro)", kit -> CampusBuildings.buildLandscaping(kit, -60, 50)));
        BUILD_QUEUE.add(new BuildStep("Paisagismo e Bosques (Sul)", kit -> CampusBuildings.buildLandscaping(kit, 50, 160)));
        BUILD_QUEUE.add(new BuildStep("Paisagismo e Bosques (Docas)", kit -> CampusBuildings.buildLandscaping(kit, 160, 250)));

        // 8. Terminais de Trânsito Tecnológico
        BUILD_QUEUE.add(new BuildStep("Terminais de Trânsito Imperial", CampusBuildings::placeTransitTerminals));

        // 9. Finalização
        BUILD_QUEUE.add(new BuildStep("Finalização do Campus", kit -> kit.set(MARKER_POS, Blocks.GOLD_BLOCK)));

        totalSteps = BUILD_QUEUE.size();
        currentStepIndex = 0;
        isBuilding = true;

        HeroAcademy.LOGGER.info("Iniciando geração procedural da Academia Imperial Destiny (total de {} etapas)...", totalSteps);
        broadcastMessage(level, "§6🏰 [Academia Imperial] §eIniciando construção procedural do campus...");
    }

    @SubscribeEvent
    public static void onLevelTick(final LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (!serverLevel.dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) return;
            if (!isBuilding || BUILD_QUEUE.isEmpty()) return;

            BuildStep step = BUILD_QUEUE.poll();
            if (step != null) {
                currentStepIndex++;
                try {
                    BlockKit kit = new BlockKit(serverLevel);
                    step.task().accept(kit);
                    String actionText = String.format("§e🔨 [Construção] §f%s §7(%d/%d)", step.name(), currentStepIndex, totalSteps);
                    for (ServerPlayer player : serverLevel.players()) {
                        player.displayClientMessage(Component.literal(actionText), true);
                    }
                } catch (Exception e) {
                    HeroAcademy.LOGGER.error("Erro ao executar etapa de construção {}: {}", step.name(), e.getMessage(), e);
                }

                if (BUILD_QUEUE.isEmpty()) {
                    isBuilding = false;
                    serverLevel.setBlock(MARKER_POS, Blocks.GOLD_BLOCK.defaultBlockState(), 18);
                    HeroAcademy.LOGGER.info("Geração procedural do Campus da Academia concluída com sucesso!");
                    broadcastMessage(serverLevel, "§a✨ [Academia Imperial] Construção concluída com sucesso! Todos os prédios e terminais foram erguidos.");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(final ServerStoppingEvent event) {
        BUILD_QUEUE.clear();
        isBuilding = false;
    }

    private static void broadcastMessage(ServerLevel level, String text) {
        Component comp = Component.literal(text);
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(comp);
        }
    }
}
