package com.heroacademy.common.power;

import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.network.SyncHeroDataPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class AbilityExecutor {

    public static void execute(ServerPlayer player, AbilitySlot slot) {
        HeroData data = player.getData(ModAttachments.HERO_DATA);
        data.ensureDefaultSkillsEquipped();

        int bar = data.getActiveBarIndex();
        int slotIndex = slot.getIndex();
        String skillId = data.getSkillInSlot(bar, slotIndex);

        if (skillId == null || skillId.isEmpty()) {
            player.displayClientMessage(Component.literal("§cSlot [" + slot.getKeyName() + "] vazio na Barra " + (bar + 1) + "! Equipe habilidades na Carteirinha."), true);
            return;
        }

        if (!data.isBarSlotReady(bar, slotIndex)) {
            int remaining = (data.getBarCooldown(bar, slotIndex) + 19) / 20;
            player.displayClientMessage(Component.literal("§eHabilidade em recarga: " + remaining + "s (Barra " + (bar + 1) + ")"), true);
            return;
        }

        HeroSkill skill = HeroSkillRegistry.getSkill(skillId);
        if (skill == null) {
            return;
        }

        if (skill.getManaCost() > 0 && !data.useEnergy(skill.getManaCost())) {
            player.displayClientMessage(Component.literal("§cMana insuficiente! Requer: " + (int) skill.getManaCost() + " MP"), true);
            return;
        }

        if (skill.getStaminaCost() > 0 && !data.useStamina(skill.getStaminaCost())) {
            player.displayClientMessage(Component.literal("§cVigor insuficiente! Requer: " + (int) skill.getStaminaCost() + " VP"), true);
            return;
        }

        boolean success = skill.execute(player);
        if (success) {
            int baseCd = skill.getBaseCooldownTicks();
            int adjustedCd = data.getAdjustedCooldown(baseCd);
            data.setBarCooldown(bar, slotIndex, adjustedCd);
            data.addMastery(15);
            player.displayClientMessage(Component.literal("§b✦ " + skill.getName() + " §7(Barra " + (bar + 1) + " [" + slot.getKeyName() + "])"), true);
            syncToClient(player, data);
        }
    }

    private static float getEnergyCost(AbilitySlot slot) {
        return switch (slot) {
            case SLOT_Z -> 15.0f;
            case SLOT_X -> 20.0f;
            case SLOT_C -> 25.0f;
            case SLOT_V -> 40.0f;
            case SLOT_G -> 80.0f;
        };
    }

    private static int getBaseCooldown(AbilitySlot slot) {
        return switch (slot) {
            case SLOT_Z -> 40;  // 2s
            case SLOT_X -> 80;  // 4s
            case SLOT_C -> 140; // 7s
            case SLOT_V -> 240; // 12s
            case SLOT_G -> 600; // 30s
        };
    }

    // ==========================================
    // CRONOCINESE (CONTROLE DO TEMPO)
    // ==========================================
    private static boolean executeChronokinesis(ServerPlayer player, HeroData data, AbilitySlot slot) {
        ServerLevel level = player.serverLevel();
        float multiplier = data.getRarity().getPowerMultiplier();

        switch (slot) {
            case SLOT_Z -> {
                // Aceleração Pessoal (Haste + Speed)
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) (120 * multiplier), 2));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (int) (120 * multiplier), 1));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.8f);
                level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 20, 0.3, 0.5, 0.3, 0.1);
                player.displayClientMessage(Component.literal("§b[Tempo] Aceleração Pessoal ativada!"), true);
                return true;
            }
            case SLOT_X -> {
                // Salto Temporal (Dash rápido à frente)
                Vec3 look = player.getLookAngle().scale(1.8 * multiplier);
                player.setDeltaMovement(look.x, 0.3, look.z);
                player.hurtMarked = true;
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1.0f, 1.5f);
                level.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 0.5, player.getZ(), 30, 0.4, 0.4, 0.4, 0.2);
                return true;
            }
            case SLOT_C -> {
                // Dilatação Temporal (Desacelera entidades ao redor)
                AABB area = player.getBoundingBox().inflate(8 * multiplier);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
                for (LivingEntity target : targets) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 0.5f);
                level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 0.5, player.getZ(), 50, 2.0, 0.5, 2.0, 0.05);
                player.displayClientMessage(Component.literal("§b[Tempo] Dilatação Temporal aplicada a " + targets.size() + " alvos!"), true);
                return true;
            }
            case SLOT_V -> {
                // Rebobinar Temporal (Cura instantânea e purificação)
                player.heal(6.0f * multiplier);
                player.removeAllEffects();
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 1.2f);
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(), 35, 0.4, 0.6, 0.4, 0.1);
                player.displayClientMessage(Component.literal("§b[Tempo] Estado temporal restaurado!"), true);
                return true;
            }
            case SLOT_G -> {
                // TIME STOP SUPREMO (Lendário)
                data.setAwakenedTimer(140); // 7 segundos
                AABB stopArea = player.getBoundingBox().inflate(18);
                List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, stopArea, e -> e != player);
                for (LivingEntity victim : victims) {
                    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140, 255, false, false));
                    victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                    victim.setDeltaMovement(0, 0, 0);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2.0f, 0.2f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.2f, 1.8f);
                level.sendParticles(ParticleTypes.SONIC_BOOM, player.getX(), player.getY() + 1, player.getZ(), 5, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1, player.getZ(), 100, 4.0, 2.0, 4.0, 0.1);
                player.displayClientMessage(Component.literal("§6§l⏳ [CRONOCINESE] O TEMPO FOI PARALISADO! (TIME STOP)"), false);
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // MANIPULAÇÃO DE SOMBRAS
    // ==========================================
    private static boolean executeShadowManipulation(ServerPlayer player, HeroData data, AbilitySlot slot) {
        ServerLevel level = player.serverLevel();
        float multiplier = data.getRarity().getPowerMultiplier();

        switch (slot) {
            case SLOT_Z -> {
                // Espinho de Sombra (Dano à frente)
                Vec3 look = player.getLookAngle();
                Vec3 strikePos = player.position().add(look.scale(3.5));
                AABB box = new AABB(strikePos.x - 1.5, strikePos.y - 1, strikePos.z - 1.5, strikePos.x + 1.5, strikePos.y + 2, strikePos.z + 1.5);
                List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                for (LivingEntity e : enemies) {
                    e.hurt(level.damageSources().magic(), 7.0f * multiplier);
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                }
                level.playSound(null, strikePos.x, strikePos.y, strikePos.z, SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.PLAYERS, 1.0f, 0.8f);
                level.sendParticles(ParticleTypes.SQUID_INK, strikePos.x, strikePos.y + 0.5, strikePos.z, 40, 0.8, 0.8, 0.8, 0.05);
                return true;
            }
            case SLOT_X -> {
                // Passo das Sombras (Teleporte nas sombras até 12 blocos)
                Vec3 target = player.position().add(player.getLookAngle().scale(10.0 * multiplier));
                level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), 30, 0.4, 0.6, 0.4, 0.05);
                player.teleportTo(target.x, target.y, target.z);
                player.resetFallDistance();
                level.playSound(null, target.x, target.y, target.z, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.8f, 1.6f);
                level.sendParticles(ParticleTypes.SOUL, target.x, target.y + 0.5, target.z, 25, 0.4, 0.5, 0.4, 0.02);
                return true;
            }
            case SLOT_C -> {
                // Manto da Escuridão
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 160, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 1.0f, 1.2f);
                return true;
            }
            case SLOT_V -> {
                // Vórtice Sombrio (Puxa inimigos)
                AABB pullBox = player.getBoundingBox().inflate(10);
                List<LivingEntity> pulled = level.getEntitiesOfClass(LivingEntity.class, pullBox, e -> e != player);
                for (LivingEntity e : pulled) {
                    Vec3 dir = player.position().subtract(e.position()).normalize().scale(1.2);
                    e.setDeltaMovement(dir.x, 0.4, dir.z);
                    e.hurt(level.damageSources().magic(), 4.0f * multiplier);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0f, 0.6f);
                level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY() + 1, player.getZ(), 60, 2.0, 1.0, 2.0, 0.1);
                return true;
            }
            case SLOT_G -> {
                // Domínio das Trevas Supremo
                data.setAwakenedTimer(200);
                AABB domain = player.getBoundingBox().inflate(16);
                List<LivingEntity> foes = level.getEntitiesOfClass(LivingEntity.class, domain, e -> e != player);
                for (LivingEntity foe : foes) {
                    foe.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                    foe.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 2));
                    foe.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.2f, 0.8f);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 1, player.getZ(), 100, 3.0, 1.5, 3.0, 0.1);
                player.displayClientMessage(Component.literal("§8§l🌑 [SOMBRAS] EXPANSÃO DE DOMÍNIO: MANTO DAS TREVAS!"), false);
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // RELÂMPAGO DIVINO
    // ==========================================
    private static boolean executeDivineLightning(ServerPlayer player, HeroData data, AbilitySlot slot) {
        ServerLevel level = player.serverLevel();
        float multiplier = data.getRarity().getPowerMultiplier();

        switch (slot) {
            case SLOT_Z -> {
                // Descarga Elétrica
                Vec3 targetPos = player.position().add(player.getLookAngle().scale(8));
                AABB zapBox = new AABB(targetPos.x - 2, targetPos.y - 1, targetPos.z - 2, targetPos.x + 2, targetPos.y + 2, targetPos.z + 2);
                for (LivingEntity foe : level.getEntitiesOfClass(LivingEntity.class, zapBox, e -> e != player)) {
                    foe.hurt(level.damageSources().lightningBolt(), 8.0f * multiplier);
                }
                level.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.8f, 1.8f);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, targetPos.x, targetPos.y + 1, targetPos.z, 40, 1.0, 1.0, 1.0, 0.2);
                return true;
            }
            case SLOT_X -> {
                // Velocidade Divina (Impulso Elétrico)
                Vec3 boost = player.getLookAngle().scale(2.5 * multiplier);
                player.setDeltaMovement(boost.x, 0.25, boost.z);
                player.hurtMarked = true;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 3));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_2.value(), SoundSource.PLAYERS, 1.0f, 1.4f);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 0.5, player.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
                return true;
            }
            case SLOT_C -> {
                // Escudo Estático
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 2));
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0f, 1.6f);
                return true;
            }
            case SLOT_V -> {
                // Tempestade Elétrica
                AABB storm = player.getBoundingBox().inflate(10);
                for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, storm, target -> target != player)) {
                    e.hurt(level.damageSources().lightningBolt(), 10.0f * multiplier);
                    e.setRemainingFireTicks(60);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.2f, 1.0f);
                level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 2, player.getZ(), 10, 1.5, 1.5, 1.5, 0.0);
                return true;
            }
            case SLOT_G -> {
                // Julgamento do Trovão Supremo
                data.setAwakenedTimer(180);
                AABB cataclysm = player.getBoundingBox().inflate(15);
                for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, cataclysm, e -> e != player)) {
                    victim.hurt(level.damageSources().lightningBolt(), 25.0f * multiplier);
                    victim.setDeltaMovement(0, 1.2, 0);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 0.9f);
                level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 20, 2.0, 1.0, 2.0, 0.1);
                player.displayClientMessage(Component.literal("§e§l⚡ [RELÂMPAGO] JULGAMENTO DIVINO DO TROVÃO!"), false);
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // CURA & REGENERAÇÃO MILAGROSA
    // ==========================================
    private static boolean executeMiracleHealing(ServerPlayer player, HeroData data, AbilitySlot slot) {
        ServerLevel level = player.serverLevel();
        float multiplier = data.getRarity().getPowerMultiplier();

        switch (slot) {
            case SLOT_Z -> {
                // Toque Curativo
                player.heal(8.0f * multiplier);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.6f);
                level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(), 12, 0.4, 0.4, 0.4, 0.1);
                return true;
            }
            case SLOT_X -> {
                // Purificação
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.2f);
                return true;
            }
            case SLOT_C -> {
                // Barreira Radiante
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.4f);
                return true;
            }
            case SLOT_V -> {
                // Onda de Vida em Área
                AABB healArea = player.getBoundingBox().inflate(12);
                for (LivingEntity ally : level.getEntitiesOfClass(LivingEntity.class, healArea)) {
                    ally.heal(12.0f * multiplier);
                    ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.2f, 1.5f);
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 50, 2.0, 1.0, 2.0, 0.1);
                return true;
            }
            case SLOT_G -> {
                // Renascimento Milagroso Supremo
                data.setAwakenedTimer(220);
                player.setHealth(player.getMaxHealth());
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 4));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 220, 3));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5f, 1.0f);
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1, player.getZ(), 100, 1.0, 1.5, 1.0, 0.2);
                player.displayClientMessage(Component.literal("§d§l💖 [CURA] RENASCIMENTO MILAGROSO ATIVADO!"), false);
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // TELECINESE / FORÇA ESPACIAL
    // ==========================================
    private static boolean executeTelekinesis(ServerPlayer player, HeroData data, AbilitySlot slot) {
        ServerLevel level = player.serverLevel();
        float multiplier = data.getRarity().getPowerMultiplier();

        switch (slot) {
            case SLOT_Z -> {
                // Empurrão Cinético
                Vec3 pushDir = player.getLookAngle().scale(2.0 * multiplier);
                AABB cone = player.getBoundingBox().inflate(7);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, cone, e -> e != player)) {
                    target.setDeltaMovement(pushDir.x, 0.6, pushDir.z);
                    target.hurt(level.damageSources().magic(), 6.0f * multiplier);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.2f, 1.0f);
                level.sendParticles(ParticleTypes.GUST, player.getX(), player.getY() + 1, player.getZ(), 15, 0.5, 0.5, 0.5, 0.1);
                return true;
            }
            case SLOT_X -> {
                // Salto Gravitacional
                player.setDeltaMovement(0, 1.3 * multiplier, 0);
                player.hurtMarked = true;
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 160, 0));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_JUMP, SoundSource.PLAYERS, 1.0f, 1.2f);
                return true;
            }
            case SLOT_C -> {
                // Barreira de Repulsão
                AABB barrierArea = player.getBoundingBox().inflate(5);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, barrierArea, e -> e != player)) {
                    Vec3 repulse = target.position().subtract(player.position()).normalize().scale(1.5);
                    target.setDeltaMovement(repulse.x, 0.3, repulse.z);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 0.8f);
                return true;
            }
            case SLOT_V -> {
                // Esmagamento Gravitacional
                AABB smashArea = player.getBoundingBox().inflate(9);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, smashArea, e -> e != player)) {
                    target.setDeltaMovement(0, -1.5, 0);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 4));
                    target.hurt(level.damageSources().generic(), 10.0f * multiplier);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0f, 0.6f);
                return true;
            }
            case SLOT_G -> {
                // Singularidade Espacial Suprema
                data.setAwakenedTimer(200);
                Vec3 center = player.position().add(player.getLookAngle().scale(6));
                AABB vortex = new AABB(center.x - 12, center.y - 4, center.z - 12, center.x + 12, center.y + 6, center.z + 12);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, vortex, e -> e != player)) {
                    Vec3 suction = center.subtract(target.position()).normalize().scale(1.8);
                    target.setDeltaMovement(suction.x, 0.4, suction.z);
                    target.hurt(level.damageSources().magic(), 18.0f * multiplier);
                }
                level.playSound(null, center.x, center.y, center.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.5f, 0.5f);
                level.sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 120, 2.0, 2.0, 2.0, 0.5);
                player.displayClientMessage(Component.literal("§5§l🌀 [TELECINESE] SINGULARIDADE ESPACIAL CONJURADA!"), false);
                return true;
            }
        }
        return false;
    }

    public static void syncToClient(ServerPlayer player, HeroData data) {
        PacketDistributor.sendToPlayer(player, SyncHeroDataPayload.from(data));
    }
}
