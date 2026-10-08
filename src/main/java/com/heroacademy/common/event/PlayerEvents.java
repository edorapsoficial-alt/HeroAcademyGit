package com.heroacademy.common.event;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.academic.SchoolClass;
import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.item.weapon.KatanaItem;
import com.heroacademy.common.item.weapon.ScytheItem;
import com.heroacademy.common.item.weapon.SpearItem;
import com.heroacademy.common.item.weapon.TonfaItem;
import com.heroacademy.common.network.SyncHeroDataPayload;
import com.heroacademy.common.power.PowerType;
import com.heroacademy.common.power.StatType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            boolean firstJoin = (data.getPowerType() == PowerType.NONE);

            // Despertar inicial de poderes para novos alunos da Academia
            if (firstJoin) {
                PowerType assigned = PowerType.getRandomCombatPower();
                data.setPowerType(assigned);
                data.setSchoolClass(Math.random() > 0.5 ? SchoolClass.CLASS_1A : SchoolClass.CLASS_1B);

                // Entrega oficial da Carteirinha de Estudante da Academia Imperial Destiny
                if (!player.getInventory().contains(new ItemStack(com.heroacademy.common.item.ModItems.STUDENT_ID_CARD.get()))) {
                    player.getInventory().add(new ItemStack(com.heroacademy.common.item.ModItems.STUDENT_ID_CARD.get()));
                }

                player.sendSystemMessage(Component.literal("§6§l========================================"));
                player.sendSystemMessage(Component.literal("§e🎓 Bem-vindo à §6§lAcademia Imperial Destiny§e!"));
                player.sendSystemMessage(Component.literal("§aSua individualidade despertada: §d§l" + assigned.getDisplayName()));
                player.sendSystemMessage(Component.literal("§7" + assigned.getDescription()));
                player.sendSystemMessage(Component.literal("§fVocê foi matriculado na: §b" + data.getSchoolClass().getDisplayName()));
                player.sendSystemMessage(Component.literal("§6Você recebeu sua §bCarteirinha de Estudante §6no inventário!"));
                player.sendSystemMessage(Component.literal("§6Use §f[Z], [X], [C], [V] §6para habilidades, §f[G] §6para o Despertar e §f[K] §6para Menu!"));
                player.sendSystemMessage(Component.literal("§6§l========================================"));

                // A Academia Imperial Destiny é o mapa inicial oficial do jogador!
                com.heroacademy.common.world.CampusTeleporter.teleport(player);
            }

            data.recalculateDerivedStats(player);
            sync(player, data);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            data.recalculateDerivedStats(player);

            // Se o jogador não definiu uma cama no mundo exterior, o campus é sua residência oficial
            if (player.getRespawnPosition() == null && !player.level().dimension().equals(com.heroacademy.common.world.ModDimensions.CAMPUS_LEVEL_KEY)) {
                net.minecraft.server.level.ServerLevel campusLevel = player.getServer().getLevel(com.heroacademy.common.world.ModDimensions.CAMPUS_LEVEL_KEY);
                if (campusLevel != null) {
                    player.teleportTo(campusLevel, com.heroacademy.common.world.CampusTeleporter.CAMPUS_SPAWN_X, com.heroacademy.common.world.CampusTeleporter.CAMPUS_SPAWN_Y, com.heroacademy.common.world.CampusTeleporter.CAMPUS_SPAWN_Z, java.util.Set.of(), 0.0f, 0.0f);
                }
            }

            sync(player, data);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            data.tick();

            // Correr consome vigor por tick (~0.35f por tick)
            if (player.isSprinting()) {
                boolean hasStamina = data.useStamina(0.35f, player);
                if (!hasStamina || data.getCurrentStamina() <= 0) {
                    player.setSprinting(false);
                }
            }

            // Detecção da plataforma do Portal de Retorno / Docks do Dirigível (CampusLayout)
            if (player.level().dimension().equals(com.heroacademy.common.world.ModDimensions.CAMPUS_LEVEL_KEY)) {
                if (Math.abs(player.getX() - com.heroacademy.common.world.CampusLayout.PORTAL_X) <= 4.0
                        && Math.abs(player.getZ() - com.heroacademy.common.world.CampusLayout.PORTAL_Z) <= 4.0
                        && Math.abs(player.getY() - com.heroacademy.common.world.CampusLayout.G) <= 4.0) {
                    if (player.tickCount % 40 == 0) {
                        com.heroacademy.common.world.CampusTeleporter.teleport(player);
                    }
                }
            }

            // Sincroniza periodicamente a cada 1 segundo (20 ticks) para manter HUDs fluidas
            if (player.tickCount % 20 == 0) {
                sync(player, data);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(final AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity target) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            ItemStack held = player.getMainHandItem();
            String name = held.getItem().getDescriptionId().toLowerCase();

            // Detecta se é arma mágica (Orbe, Cajado, Tomo, Runomancia)
            StatType magicArt = null;
            if (held.getItem() instanceof com.heroacademy.common.item.MagicTomeItem || name.contains("tome") || name.contains("livro") || name.contains("grimoire")) {
                magicArt = StatType.TECHNIQUE_TOMES;
            } else if (name.contains("orb") || name.contains("orbe")) {
                magicArt = StatType.TECHNIQUE_ORBS;
            } else if (name.contains("staff") || name.contains("cajado") || name.contains("wand") || name.contains("vara")) {
                magicArt = StatType.TECHNIQUE_STAVES;
            } else if (name.contains("rune") || name.contains("runa")) {
                magicArt = StatType.TECHNIQUE_RUNES;
            }

            if (magicArt != null) {
                // Ataque Mágico: Consome Mana
                data.useEnergy(8.0f, player);
                float mult = data.getMagicDamageMultiplier(magicArt);
                float magicDamage = 5.0f * mult;
                target.hurt(player.serverLevel().damageSources().magic(), magicDamage);
                player.serverLevel().sendParticles(ParticleTypes.ENCHANT, target.getX(), target.getY() + 1, target.getZ(), 12, 0.4, 0.4, 0.4, 0.1);
            } else {
                // Ataque Físico: Consome Vigor
                data.useStamina(6.0f, player);

                StatType physArt = StatType.TECHNIQUE_WESTERN;
                if (held.isEmpty()) {
                    physArt = StatType.TECHNIQUE_MARTIAL;
                    float mult = data.getPhysicalDamageMultiplier(physArt);
                    int martialPoints = data.getStatPoints(StatType.TECHNIQUE_MARTIAL);
                    float damage = (2.5f + martialPoints * 1.5f) * mult;
                    target.hurt(player.serverLevel().damageSources().playerAttack(player), damage);
                    player.serverLevel().sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 8, 0.3, 0.3, 0.3, 0.1);
                } else {
                    if (held.getItem() instanceof SpearItem || name.contains("spear") || name.contains("lance")) {
                        physArt = StatType.TECHNIQUE_POLEARMS;
                    } else if (held.getItem() instanceof KatanaItem || held.getItem() instanceof TonfaItem || name.contains("katana") || name.contains("tonfa")) {
                        physArt = StatType.TECHNIQUE_EASTERN;
                    } else if (held.getItem() instanceof ScytheItem || name.contains("scythe") || name.contains("foice")) {
                        physArt = StatType.TECHNIQUE_EXOTIC;
                    } else if (name.contains("dagger") || name.contains("adaga") || name.contains("knife") || name.contains("faca")) {
                        physArt = StatType.TECHNIQUE_DAGGERS;
                    } else if (held.getItem() instanceof SwordItem || held.getItem() instanceof AxeItem || name.contains("sword") || name.contains("espada") || name.contains("axe") || name.contains("machado")) {
                        physArt = StatType.TECHNIQUE_WESTERN;
                    }

                    float mult = data.getPhysicalDamageMultiplier(physArt);
                    float bonus = 4.0f * (mult - 1.0f);
                    if (bonus > 0) {
                        target.hurt(player.serverLevel().damageSources().playerAttack(player), bonus);
                        player.serverLevel().sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1, target.getZ(), 2, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }

            // Atualiza HUD imediatamente
            sync(player, data);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(final PlayerWakeUpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            data.setRestedBonus(true);
            player.displayClientMessage(Component.literal("§a✨ Você acordou descansado nos dormitórios! (+50% de regen de mana e vigor hoje)"), false);
            sync(player, data);
        }
    }

    private static void sync(ServerPlayer player, HeroData data) {
        PacketDistributor.sendToPlayer(player, SyncHeroDataPayload.from(data));
    }
}
