package com.heroacademy.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;

import java.util.Set;

public class CampusTeleporter {

    public static final double CAMPUS_SPAWN_X = CampusLayout.SPAWN_X;
    public static final double CAMPUS_SPAWN_Y = (double) CampusLayout.G;
    public static final double CAMPUS_SPAWN_Z = CampusLayout.SPAWN_Z;

    public static void teleport(ServerPlayer player) {
        if (player.level().dimension().equals(ModDimensions.CAMPUS_LEVEL_KEY)) {
            // Retorna ao Overworld
            ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) {
                BlockPos respawn = player.getRespawnPosition();
                double targetX = (respawn != null) ? respawn.getX() + 0.5 : overworld.getSharedSpawnPos().getX() + 0.5;
                double targetY = (respawn != null) ? respawn.getY() : overworld.getSharedSpawnPos().getY();
                double targetZ = (respawn != null) ? respawn.getZ() + 0.5 : overworld.getSharedSpawnPos().getZ() + 0.5;

                player.teleportTo(overworld, targetX, targetY, targetZ, Set.of(), player.getYRot(), player.getXRot());
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.8f, 1.2f);
                player.displayClientMessage(Component.literal("§a🏠 Você retornou da Academia Imperial Destiny ao Mundo Exterior!"), true);
            }
        } else {
            // Teleporta para o Campus Imperial Destiny
            ServerLevel campusLevel = player.getServer().getLevel(ModDimensions.CAMPUS_LEVEL_KEY);
            if (campusLevel != null) {
                // Prepara o campus e decorações
                CampusStructureBuilder.buildCampusIfNeeded(campusLevel);

                // Configura o World Border para cobrir o mapa completo da Academia
                setupWorldBorder(campusLevel);

                player.teleportTo(campusLevel, CAMPUS_SPAWN_X, CAMPUS_SPAWN_Y, CAMPUS_SPAWN_Z, Set.of(), 180.0f, 0.0f);
                campusLevel.playSound(null, CAMPUS_SPAWN_X, CAMPUS_SPAWN_Y, CAMPUS_SPAWN_Z, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.9f, 1.0f);
                campusLevel.playSound(null, CAMPUS_SPAWN_X, CAMPUS_SPAWN_Y, CAMPUS_SPAWN_Z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);
                campusLevel.sendParticles(ParticleTypes.ENCHANT, CAMPUS_SPAWN_X, CAMPUS_SPAWN_Y + 1, CAMPUS_SPAWN_Z, 30, 0.5, 0.5, 0.5, 0.1);

                player.sendSystemMessage(Component.literal("§6§l========================================"));
                player.sendSystemMessage(Component.literal("§e🎓 Bem-vindo ao §6§lCampus da Imperial Destiny Academy§e!"));
                player.sendSystemMessage(Component.literal("§fVisite as §bSalas de Aula (História, Ciência, Filosofia e Tática)§f, a Biblioteca e os Dormitórios."));
                player.sendSystemMessage(Component.literal("§7Use §f[Shift + Clique] §7na sua Carteirinha de Estudante para retornar."));
                player.sendSystemMessage(Component.literal("§6§l========================================"));
            } else {
                player.displayClientMessage(Component.literal("§c[Erro] A dimensão do Campus ainda não está carregada!"), true);
            }
        }
    }

    public static void setupWorldBorder(ServerLevel campusLevel) {
        WorldBorder border = campusLevel.getWorldBorder();
        border.setCenter((double) CampusLayout.BORDER_CENTER_X, (double) CampusLayout.BORDER_CENTER_Z);
        border.setSize((double) CampusLayout.BORDER_SIZE);
        border.setDamagePerBlock(2.0);
        border.setDamageSafeZone(5.0);
        border.setWarningBlocks(25);
        border.setWarningTime(15);
    }
}
