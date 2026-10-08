package com.heroacademy.common.command;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.network.OpenCampusNavigationPayload;
import com.heroacademy.common.network.OpenTransitTerminalPayload;
import com.heroacademy.common.network.SyncHeroDataPayload;
import com.heroacademy.common.world.CampusStructureBuilder;
import com.heroacademy.common.world.CampusTeleporter;
import com.heroacademy.common.world.ModDimensions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("campus")
                .executes(context -> {
                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                        CampusTeleporter.teleport(player);
                        return 1;
                    }
                    return 0;
                })
                .then(Commands.literal("guiar")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                PacketDistributor.sendToPlayer(player, new OpenCampusNavigationPayload());
                                return 1;
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("terminal")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                PacketDistributor.sendToPlayer(player, new OpenTransitTerminalPayload());
                                return 1;
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("creditos")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                HeroData data = player.getData(ModAttachments.HERO_DATA);
                                player.displayClientMessage(Component.literal("§6🪙 Seu saldo: §e" + data.getImperialCredits() + " Créditos Imperiais"), false);
                                return 1;
                            }
                            return 0;
                        })
                        .then(Commands.argument("quantia", IntegerArgumentType.integer())
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                        int amount = IntegerArgumentType.getInteger(context, "quantia");
                                        HeroData data = player.getData(ModAttachments.HERO_DATA);
                                        data.addImperialCredits(amount);
                                        PacketDistributor.sendToPlayer(player, SyncHeroDataPayload.from(data));
                                        player.displayClientMessage(Component.literal("§6🪙 [Créditos Imperiais] §a+" + amount + " adicionados! Saldo atual: §e" + data.getImperialCredits()), false);
                                        return 1;
                                    }
                                    return 0;
                                })
                        )
                )
                .then(Commands.literal("border")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                ServerLevel campusLevel = player.getServer().getLevel(ModDimensions.CAMPUS_LEVEL_KEY);
                                if (campusLevel != null) {
                                    CampusTeleporter.setupWorldBorder(campusLevel);
                                    player.displayClientMessage(Component.literal("§b🛡️ WorldBorder do Campus configurada com sucesso! (" + com.heroacademy.common.world.CampusLayout.BORDER_SIZE + "x" + com.heroacademy.common.world.CampusLayout.BORDER_SIZE + ")"), false);
                                    return 1;
                                }
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("rebuild")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                ServerLevel campusLevel = player.getServer().getLevel(ModDimensions.CAMPUS_LEVEL_KEY);
                                if (campusLevel != null) {
                                    CampusStructureBuilder.forceRebuild(campusLevel);
                                    player.displayClientMessage(Component.literal("§a🏰 Reconstrução procedural do Campus da Academia iniciada!"), false);
                                    return 1;
                                }
                            }
                            return 0;
                        })
                )
        );

        // Alias com namespace /heroacademy
        dispatcher.register(Commands.literal("heroacademy")
                .then(Commands.literal("campus")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                CampusTeleporter.teleport(player);
                                return 1;
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("guiar")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                PacketDistributor.sendToPlayer(player, new OpenCampusNavigationPayload());
                                return 1;
                            }
                            return 0;
                        })
                )
                .then(Commands.literal("terminal")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                PacketDistributor.sendToPlayer(player, new OpenTransitTerminalPayload());
                                return 1;
                            }
                            return 0;
                        })
                )
        );
    }
}
