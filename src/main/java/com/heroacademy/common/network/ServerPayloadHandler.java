package com.heroacademy.common.network;

import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.power.AbilityExecutor;
import com.heroacademy.common.power.AbilitySlot;
import com.heroacademy.common.power.HeroSkill;
import com.heroacademy.common.power.HeroSkillRegistry;
import com.heroacademy.common.power.StatType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleKeybindCast(final KeybindCastPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                AbilitySlot slot = AbilitySlot.fromIndex(payload.slotIndex());
                AbilityExecutor.execute(serverPlayer, slot);
            }
        });
    }

    public static void handleAllocateStat(final AllocateStatPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                if (payload.statOrdinal() >= 0 && payload.statOrdinal() < StatType.values().length) {
                    StatType stat = StatType.values()[payload.statOrdinal()];
                    HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                    boolean success = data.allocateStat(stat, serverPlayer);
                    if (success) {
                        serverPlayer.displayClientMessage(Component.literal("§a+1 Ponto investido em: §f" + stat.getDisplayName()), true);
                        PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));
                    } else {
                        serverPlayer.displayClientMessage(Component.literal("§cSem pontos disponíveis em " + stat.getParentAttribute().getDisplayName() + "!"), true);
                    }
                }
            }
        });
    }

    public static void handleAllocateUniqueRoute(final AllocateUniqueRoutePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                boolean success = data.allocateUniqueRoute(payload.routeIndex());
                if (success) {
                    String routeName = switch (payload.routeIndex()) {
                        case 0 -> "Rota A (Ofensiva)";
                        case 1 -> "Rota B (Mobilidade)";
                        case 2 -> "Rota C (Despertar)";
                        default -> "Rota";
                    };
                    serverPlayer.displayClientMessage(Component.literal("§d+1 Ponto de Domínio investido em: §f" + routeName), true);
                    PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));
                } else {
                    serverPlayer.displayClientMessage(Component.literal("§cSem pontos de Habilidade Única disponíveis ou rota no nível máximo!"), true);
                }
            }
        });
    }

    public static void handleEquipSkill(final EquipSkillPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                int bar = Math.max(0, Math.min(1, payload.barIndex()));
                int slot = Math.max(0, Math.min(4, payload.slotIndex()));
                String skillId = payload.skillId();

                if (skillId == null || skillId.isEmpty()) {
                    // Desequipar
                    data.setSkillInSlot(bar, slot, "");
                    serverPlayer.displayClientMessage(Component.literal("§eSlot [" + AbilitySlot.fromIndex(slot).getKeyName() + "] desequipado da Barra " + (bar + 1)), true);
                } else {
                    // Equipar
                    HeroSkill skill = HeroSkillRegistry.getSkill(skillId);
                    if (skill != null) {
                        data.setSkillInSlot(bar, slot, skillId);
                        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
                        serverPlayer.displayClientMessage(Component.literal("§aEquipado: §f" + skill.getName() + " §ano slot [" + AbilitySlot.fromIndex(slot).getKeyName() + "] (Barra " + (bar + 1) + ")"), true);
                    }
                }
                PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));
            }
        });
    }

    public static void handleSwitchSkillBar(final SwitchSkillBarPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                data.toggleSkillBar();
                int currentBar = data.getActiveBarIndex() + 1;
                serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.4f);
                serverPlayer.displayClientMessage(Component.literal("§6[Deck de Habilidades] §fAtiva: §bBarra " + (currentBar == 1 ? "I" : "II")), true);
                PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));
            }
        });
    }

    public static void handleAllocateClassPoint(final AllocateClassPointPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                boolean success = false;
                if (payload.classOrdinal() >= 0 && payload.classOrdinal() < com.heroacademy.common.power.ClassType.values().length) {
                    com.heroacademy.common.power.ClassType type = com.heroacademy.common.power.ClassType.values()[payload.classOrdinal()];
                    success = data.allocateClassPoint(type);
                    if (success) {
                        serverPlayer.displayClientMessage(Component.literal("§a+1 Ponto de Maestria em: §f" + type.getDisplayName()), true);
                    }
                } else if (payload.branchOrdinal() >= 0 && payload.branchOrdinal() < com.heroacademy.common.power.ClassBranch.values().length) {
                    com.heroacademy.common.power.ClassBranch branch = com.heroacademy.common.power.ClassBranch.values()[payload.branchOrdinal()];
                    success = data.allocateBranchPoint(branch);
                    if (success) {
                        serverPlayer.displayClientMessage(Component.literal("§6+1 Ponto de Especialização em: §f" + branch.getDisplayName()), true);
                    }
                }
                if (success) {
                    PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));
                } else {
                    serverPlayer.displayClientMessage(Component.literal("§cSem pontos de Maestria de Classe disponíveis!"), true);
                }
            }
        });
    }

    public static void handleImperialTransit(final ImperialTransitPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                com.heroacademy.common.academic.CampusWaypoint wp = com.heroacademy.common.academic.CampusWaypoint.byId(payload.waypointId());
                if (wp == null) return;

                ServerLevel campusLevel = serverPlayer.getServer().getLevel(com.heroacademy.common.world.ModDimensions.CAMPUS_LEVEL_KEY);
                if (campusLevel == null) return;

                HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
                int transitCost = 5; // Custo simbólico da rede arcantécnica da academia

                if (!serverPlayer.isCreative() && data.getImperialCredits() < transitCost) {
                    serverPlayer.displayClientMessage(
                            Component.literal("§c⚡ [Terminal Arcantécnico] Saldo insuficiente! Custo: §e" + transitCost + " Créditos §c(Seu saldo: §6" + data.getImperialCredits() + "§c). Participe de aulas ou exames para obter mais créditos!"),
                            false
                    );
                    return;
                }

                if (!serverPlayer.isCreative()) {
                    data.spendImperialCredits(transitCost);
                }

                // Som e efeito de desmaterialização energética
                serverPlayer.serverLevel().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8f, 1.8f);
                serverPlayer.serverLevel().sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        serverPlayer.getX(), serverPlayer.getY() + 1, serverPlayer.getZ(), 40, 0.5, 0.8, 0.5, 0.1);

                // Teleporta para o terminal de destino
                net.minecraft.core.BlockPos targetPos = wp.getTargetPos();
                double tx = targetPos.getX() + 0.5;
                double ty = targetPos.getY();
                double tz = targetPos.getZ() + 0.5;
                serverPlayer.teleportTo(campusLevel, tx, ty, tz, java.util.Set.of(), wp.getTargetYaw(), 0.0f);

                // Som e partículas de materialização no destino
                campusLevel.playSound(null, tx, ty, tz, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9f, 1.4f);
                campusLevel.playSound(null, tx, ty, tz, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.5f);
                campusLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, tx, ty + 1, tz, 35, 0.5, 0.8, 0.5, 0.08);
                campusLevel.sendParticles(ParticleTypes.GLOW, tx, ty + 1.2, tz, 20, 0.4, 0.5, 0.4, 0.02);

                PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));

                serverPlayer.sendSystemMessage(
                        Component.literal("§3⚡ [Terminal Arcantécnico] §fTranslocação concluída para: " + wp.getIcon() + " §b§l" + wp.getName()
                                + " §7(-" + transitCost + " Créditos | Saldo: §6" + data.getImperialCredits() + " 🪙§7)")
                );
            }
        });
    }
}
