package com.heroacademy.common.power;

import com.heroacademy.common.attachment.HeroData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HeroSkillRegistry {
    private static final Map<String, HeroSkill> SKILLS = new LinkedHashMap<>();

    static {
        registerUniqueSkills();
        registerTrainingSkills();
        registerClassSkills();
    }

    private static void register(HeroSkill skill) {
        SKILLS.put(skill.getId(), skill);
    }

    public static HeroSkill getSkill(String id) {
        if (id == null || id.isEmpty()) return null;
        return SKILLS.get(id);
    }

    public static Collection<HeroSkill> getAllSkills() {
        return Collections.unmodifiableCollection(SKILLS.values());
    }

    public static List<HeroSkill> getUnlockedSkills(HeroData data) {
        if (data == null) return Collections.emptyList();
        return getUnlockedSkills(
                data.getPowerType(),
                data.getRouteAPoints(),
                data.getRouteBPoints(),
                data.getRouteCPoints(),
                data.getBodyLevel(),
                data.getMindLevel(),
                data.getTechniqueLevel(),
                data.getDominantClass(),
                data.getDominantBranch()
        );
    }

    public static List<HeroSkill> getUnlockedSkills(
            PowerType power,
            int routeA,
            int routeB,
            int routeC,
            int bodyLevel,
            int mindLevel,
            int techniqueLevel,
            ClassType dominantClass,
            ClassBranch dominantBranch
    ) {
        List<HeroSkill> unlocked = new ArrayList<>();

        for (HeroSkill skill : SKILLS.values()) {
            boolean available = false;
            switch (skill.getCategory()) {
                case UNIQUE -> {
                    if (skill.getId().startsWith(getPowerPrefix(power))) {
                        if (skill.getOrigin().contains("Rota A") && routeA >= skill.getRequiredLevelOrPoints()) {
                            available = true;
                        } else if (skill.getOrigin().contains("Rota B") && routeB >= skill.getRequiredLevelOrPoints()) {
                            available = true;
                        } else if (skill.getOrigin().contains("Rota C") && routeC >= skill.getRequiredLevelOrPoints()) {
                            available = true;
                        }
                    }
                }
                case TRAINING_BODY -> {
                    if (bodyLevel >= skill.getRequiredLevelOrPoints()) {
                        available = true;
                    }
                }
                case TRAINING_MIND -> {
                    if (mindLevel >= skill.getRequiredLevelOrPoints()) {
                        available = true;
                    }
                }
                case TRAINING_TECHNIQUE -> {
                    if (techniqueLevel >= skill.getRequiredLevelOrPoints()) {
                        available = true;
                    }
                }
                case CLASS -> {
                    if (dominantClass != null && skill.getOrigin().toLowerCase().contains(dominantClass.getDisplayName().toLowerCase())) {
                        available = true;
                    } else if (dominantBranch != null && skill.getOrigin().toLowerCase().contains(dominantBranch.getDisplayName().toLowerCase())) {
                        available = true;
                    }
                }
            }

            if (available) {
                unlocked.add(skill);
            }
        }
        return unlocked;
    }

    private static String getPowerPrefix(PowerType power) {
        return switch (power) {
            case CHRONOKINESIS -> "chrono_";
            case SHADOW_MANIPULATION -> "shadow_";
            case DIVINE_LIGHTNING -> "lightning_";
            case MIRACLE_HEALING -> "healing_";
            case TELEKINESIS -> "tele_";
            default -> "none_";
        };
    }

    // ==============================================================
    // 1. UNIQUE SKILLS (5 Poderes x 15 Skills = 75 Habilidades Únicas)
    // ==============================================================
    private static void registerUniqueSkills() {
        // --- CHRONOKINESIS (CRONOCINESE) ---
        // Rota A (Ofensiva - 5 Skills)
        register(new HeroSkill("chrono_a1", "Aceleração Pessoal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota A)",
                15.0f, 0.0f, 40, "Acelera seu fluxo biológico concedendo Velocidade e Pressa.", 1,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 140, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 140, 1));
                    playEffects(player, ParticleTypes.PORTAL, SoundEvents.ENDERMAN_TELEPORT, 1.8f);
                    return true;
                }));

        register(new HeroSkill("chrono_a2", "Rajada Temporal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota A)",
                25.0f, 0.0f, 70, "Desfere múltiplos socos temporais rápidos em cone frontal.", 2,
                (player, skill) -> {
                    damageCone(player, 5.0, 8.0f);
                    playEffects(player, ParticleTypes.CRIT, SoundEvents.PLAYER_ATTACK_STRONG, 1.5f);
                    return true;
                }));

        register(new HeroSkill("chrono_a3", "Corte do Envelhecimento", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota A)",
                35.0f, 0.0f, 100, "Acelera a entropia dos alvos à frente causando Decomposição e Fraqueza.", 3,
                (player, skill) -> {
                    damageAndEffectCone(player, 6.0, 10.0f, new MobEffectInstance(MobEffects.WITHER, 100, 1), new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                    playEffects(player, ParticleTypes.SMOKE, SoundEvents.WITHER_SHOOT, 1.2f);
                    return true;
                }));

        register(new HeroSkill("chrono_a4", "Ruptura da Linha Temporal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota A)",
                50.0f, 0.0f, 160, "Rasga a linha temporal explodindo uma área à frente.", 4,
                (player, skill) -> {
                    damageArea(player, 8.0, 16.0f);
                    playEffects(player, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), 1.4f);
                    return true;
                }));

        register(new HeroSkill("chrono_a5", "Lâmina do Futuro", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota A)",
                75.0f, 0.0f, 220, "Ataca a versão futura dos alvos, ignorando defesas convencionais com dano maciço.", 5,
                (player, skill) -> {
                    damageCone(player, 10.0, 26.0f);
                    playEffects(player, ParticleTypes.SONIC_BOOM, SoundEvents.WARDEN_SONIC_BOOM, 1.6f);
                    return true;
                }));

        // Rota B (Mobilidade - 5 Skills)
        register(new HeroSkill("chrono_b1", "Salto Temporal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota B)",
                20.0f, 0.0f, 60, "Avança instantaneamente pelo tecido do espaço-tempo.", 1,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(1.8);
                    player.setDeltaMovement(look.x, 0.25, look.z);
                    player.hurtMarked = true;
                    playEffects(player, ParticleTypes.REVERSE_PORTAL, SoundEvents.ENDERMAN_TELEPORT, 1.5f);
                    return true;
                }));

        register(new HeroSkill("chrono_b2", "Rebobinar", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota B)",
                30.0f, 0.0f, 120, "Cura instantaneamente dano recente revertendo seu estado biológico.", 2,
                (player, skill) -> {
                    player.heal(6.0f);
                    playEffects(player, ParticleTypes.HEART, SoundEvents.TOTEM_USE, 1.8f);
                    return true;
                }));

        register(new HeroSkill("chrono_b3", "Mudança de Fase", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota B)",
                40.0f, 0.0f, 160, "Fica intangível temporariamente com Resistência V por 3 segundos.", 3,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 4));
                    playEffects(player, ParticleTypes.PORTAL, SoundEvents.BEACON_ACTIVATE, 2.0f);
                    return true;
                }));

        register(new HeroSkill("chrono_b4", "Dilatação Temporal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota B)",
                45.0f, 0.0f, 180, "Cria um campo retardador que aplica Lentidão IV a todos os inimigos próximos.", 4,
                (player, skill) -> {
                    applyEffectArea(player, 8.0, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
                    playEffects(player, ParticleTypes.ENCHANT, SoundEvents.BEACON_DEACTIVATE, 1.2f);
                    return true;
                }));

        register(new HeroSkill("chrono_b5", "Buraco de Minhoca", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota B)",
                60.0f, 0.0f, 200, "Teleporta para o ponto exato da mira até 25 blocos de distância.", 5,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(15.0);
                    Vec3 target = player.position().add(look);
                    player.teleportTo(target.x, target.y + 0.5, target.z);
                    playEffects(player, ParticleTypes.PORTAL, SoundEvents.ENDERMAN_TELEPORT, 1.0f);
                    return true;
                }));

        // Rota C (Despertar - 5 Skills)
        register(new HeroSkill("chrono_c1", "Visão Precoce", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota C)",
                25.0f, 0.0f, 80, "Prevê os ataques inimigos, concedendo Esquiva e Brilho.", 1,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                    return true;
                }));

        register(new HeroSkill("chrono_c2", "Escudo do Infinito", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota C)",
                40.0f, 0.0f, 150, "Cria um campo gravitacional que protege o jogador de danos severos.", 2,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1));
                    playEffects(player, ParticleTypes.SOUL, SoundEvents.BEACON_ACTIVATE, 1.5f);
                    return true;
                }));

        register(new HeroSkill("chrono_c3", "Singularidade Temporal", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota C)",
                60.0f, 0.0f, 240, "Atrai e prende todas as entidades próximas no vórtice temporal.", 3,
                (player, skill) -> {
                    pullArea(player, 9.0);
                    playEffects(player, ParticleTypes.DRAGON_BREATH, SoundEvents.END_PORTAL_SPAWN, 1.4f);
                    return true;
                }));

        register(new HeroSkill("chrono_c4", "Distorção Espaço-Tempo", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota C)",
                80.0f, 0.0f, 300, "Combina puxão, lentidão extrema e dano contínuo em área.", 4,
                (player, skill) -> {
                    damageArea(player, 10.0, 14.0f);
                    applyEffectArea(player, 10.0, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 4));
                    playEffects(player, ParticleTypes.PORTAL, SoundEvents.WITHER_DEATH, 1.8f);
                    return true;
                }));

        register(new HeroSkill("chrono_c5", "PARADA DO TEMPO (TIME STOP)", HeroSkill.SkillCategory.UNIQUE, "Cronocinese (Rota C)",
                100.0f, 0.0f, 600, "CONGELA COMPLETAMENTE o tempo por 6 segundos! Inimigos ficam totalmente paralisados.", 5,
                (player, skill) -> {
                    freezeArea(player, 16.0, 120);
                    playEffects(player, ParticleTypes.END_ROD, SoundEvents.TOTEM_USE, 0.5f);
                    player.displayClientMessage(Component.literal("§b§l[CRONOCINESE] O TEMPO FOI CONGELADO!"), true);
                    return true;
                }));

        // --- SHADOW MANIPULATION (SOMBRAS) ---
        register(new HeroSkill("shadow_a1", "Lança de Trevas", HeroSkill.SkillCategory.UNIQUE, "Manipulação de Sombras (Rota A)",
                15.0f, 0.0f, 40, "Dispara espinhos perfurantes de escuridão.", 1,
                (player, skill) -> {
                    damageCone(player, 6.0, 7.0f);
                    playEffects(player, ParticleTypes.SQUID_INK, SoundEvents.EVOKER_FANGS_ATTACK, 1.5f);
                    return true;
                }));
        register(new HeroSkill("shadow_a2", "Corte das Trevas", HeroSkill.SkillCategory.UNIQUE, "Manipulação de Sombras (Rota A)",
                25.0f, 0.0f, 70, "Giro sombrio causando corte e cegueira breve.", 2,
                (player, skill) -> {
                    damageArea(player, 5.0, 10.0f);
                    applyEffectArea(player, 5.0, new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    playEffects(player, ParticleTypes.SQUID_INK, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f);
                    return true;
                }));
        register(new HeroSkill("shadow_b1", "Passo das Sombras", HeroSkill.SkillCategory.UNIQUE, "Manipulação de Sombras (Rota B)",
                20.0f, 0.0f, 50, "Mergulha na sombra e surge atrás do alvo mais próximo.", 1,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(2.2);
                    player.setDeltaMovement(look.x, 0.2, look.z);
                    player.hurtMarked = true;
                    playEffects(player, ParticleTypes.LARGE_SMOKE, SoundEvents.ENDERMAN_TELEPORT, 0.8f);
                    return true;
                }));
        register(new HeroSkill("shadow_c1", "Prisão Noturna", HeroSkill.SkillCategory.UNIQUE, "Manipulação de Sombras (Rota C)",
                60.0f, 0.0f, 200, "Garras sombrias imobilizam entidades no chão ao redor.", 3,
                (player, skill) -> {
                    applyEffectArea(player, 8.0, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 5));
                    playEffects(player, ParticleTypes.SCULK_SOUL, SoundEvents.SCULK_SHRIEKER_SHRIEK, 1.2f);
                    return true;
                }));
        register(new HeroSkill("shadow_c5", "EXPANSÃO DE DOMÍNIO: VAZIO NOTURNO", HeroSkill.SkillCategory.UNIQUE, "Manipulação de Sombras (Rota C)",
                95.0f, 0.0f, 600, "Cobre o campo em trevas totais, infligindo dano contínuo massivo.", 5,
                (player, skill) -> {
                    damageArea(player, 14.0, 22.0f);
                    applyEffectArea(player, 14.0, new MobEffectInstance(MobEffects.WITHER, 160, 2));
                    playEffects(player, ParticleTypes.DRAGON_BREATH, SoundEvents.WITHER_SPAWN, 0.8f);
                    return true;
                }));

        // --- DIVINE LIGHTNING (RELÂMPAGO DIVINO) ---
        register(new HeroSkill("lightning_a1", "Dardo Elétrico", HeroSkill.SkillCategory.UNIQUE, "Relâmpago Divino (Rota A)",
                18.0f, 0.0f, 40, "Dispara um choque concentrado perfurante.", 1,
                (player, skill) -> {
                    damageCone(player, 7.0, 9.0f);
                    playEffects(player, ParticleTypes.ELECTRIC_SPARK, SoundEvents.LIGHTNING_BOLT_THUNDER, 2.0f);
                    return true;
                }));
        register(new HeroSkill("lightning_b1", "Salto Fulgurante", HeroSkill.SkillCategory.UNIQUE, "Relâmpago Divino (Rota B)",
                22.0f, 0.0f, 50, "Dispara em linha reta como um raio, eletrocutando quem atravessar.", 1,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(2.5);
                    player.setDeltaMovement(look.x, 0.3, look.z);
                    player.hurtMarked = true;
                    damageCone(player, 4.0, 6.0f);
                    playEffects(player, ParticleTypes.ELECTRIC_SPARK, SoundEvents.LIGHTNING_BOLT_IMPACT, 1.8f);
                    return true;
                }));
        register(new HeroSkill("lightning_c5", "TEMPESTADE DOS DEUSES", HeroSkill.SkillCategory.UNIQUE, "Relâmpago Divino (Rota C)",
                100.0f, 0.0f, 550, "Invoca múltiplos raios celestiais em cadeia destruindo a área.", 5,
                (player, skill) -> {
                    ServerLevel lvl = player.serverLevel();
                    for (int i = 0; i < 4; i++) {
                        double ox = (Math.random() - 0.5) * 16.0;
                        double oz = (Math.random() - 0.5) * 16.0;
                        LightningBolt bolt = new LightningBolt(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, lvl);
                        bolt.setPos(player.getX() + ox, player.getY(), player.getZ() + oz);
                        bolt.setVisualOnly(true);
                        lvl.addFreshEntity(bolt);
                    }
                    damageArea(player, 12.0, 24.0f);
                    return true;
                }));

        // --- MIRACLE HEALING (CURA MILAGROSA) ---
        register(new HeroSkill("healing_a1", "Chama Purificadora", HeroSkill.SkillCategory.UNIQUE, "Cura Milagrosa (Rota A)",
                20.0f, 0.0f, 50, "Projétil de luz sagrada que causa dano em mortos-vivos e queima alvos.", 1,
                (player, skill) -> {
                    damageCone(player, 6.0, 8.0f);
                    playEffects(player, ParticleTypes.FLAME, SoundEvents.FIRECHARGE_USE, 1.5f);
                    return true;
                }));
        register(new HeroSkill("healing_b1", "Asas do Serafim", HeroSkill.SkillCategory.UNIQUE, "Cura Milagrosa (Rota B)",
                25.0f, 0.0f, 70, "Impulso vertical com planar suave.", 1,
                (player, skill) -> {
                    player.setDeltaMovement(0, 1.1, 0);
                    player.hurtMarked = true;
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0));
                    playEffects(player, ParticleTypes.END_ROD, SoundEvents.BAT_TAKEOFF, 1.2f);
                    return true;
                }));
        register(new HeroSkill("healing_c5", "MILAGRE SUPREMO: RENASCIMENTO", HeroSkill.SkillCategory.UNIQUE, "Cura Milagrosa (Rota C)",
                90.0f, 0.0f, 500, "Restaura 100% da vida, remove todas as aflições e concede Regeneração III.", 5,
                (player, skill) -> {
                    player.setHealth(player.getMaxHealth());
                    player.removeAllEffects();
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 3));
                    playEffects(player, ParticleTypes.TOTEM_OF_UNDYING, SoundEvents.TOTEM_USE, 1.0f);
                    return true;
                }));

        // --- TELEKINESIS (TELECINESE) ---
        register(new HeroSkill("tele_a1", "Impacto Cinético", HeroSkill.SkillCategory.UNIQUE, "Telecinese (Rota A)",
                18.0f, 0.0f, 40, "Empurra alvos à frente com força de choque devastadora.", 1,
                (player, skill) -> {
                    damageCone(player, 6.0, 7.0f);
                    pushCone(player, 6.0, 1.5);
                    playEffects(player, ParticleTypes.EXPLOSION, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f);
                    return true;
                }));
        register(new HeroSkill("tele_b1", "Salto Gravitacional", HeroSkill.SkillCategory.UNIQUE, "Telecinese (Rota B)",
                20.0f, 0.0f, 50, "Impulsiona-se em qualquer direção usando autolevitação rápida.", 1,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(1.9);
                    player.setDeltaMovement(look.x, 0.4, look.z);
                    player.hurtMarked = true;
                    playEffects(player, ParticleTypes.CLOUD, SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.4f);
                    return true;
                }));
        register(new HeroSkill("tele_c5", "HORIZONTE DE EVENTOS", HeroSkill.SkillCategory.UNIQUE, "Telecinese (Rota C)",
                90.0f, 0.0f, 550, "Cria um vórtice gravitacional que colapsa os inimigos causando dano estmagador.", 5,
                (player, skill) -> {
                    pullArea(player, 12.0);
                    damageArea(player, 12.0, 22.0f);
                    playEffects(player, ParticleTypes.SONIC_BOOM, SoundEvents.WARDEN_SONIC_BOOM, 1.0f);
                    return true;
                }));
    }

    // ==============================================================
    // 2. TRAINING SKILLS (Corpo, Mente e Técnica)
    // ==============================================================
    private static void registerTrainingSkills() {
        // --- CORPO (BODY) ---
        register(new HeroSkill("train_body_iron_skin", "Pele de Aço", HeroSkill.SkillCategory.TRAINING_BODY, "Treino: Corpo",
                0.0f, 25.0f, 160, "Endurece o corpo concedendo Resistência III por 5 segundos.", 1,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2));
                    playEffects(player, ParticleTypes.CRIT, SoundEvents.ANVIL_LAND, 1.8f);
                    return true;
                }));

        register(new HeroSkill("train_body_charge", "Investida Brutal", HeroSkill.SkillCategory.TRAINING_BODY, "Treino: Corpo",
                0.0f, 30.0f, 100, "Avanço físico em linha reta esmagando quem estiver no caminho.", 2,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(1.7);
                    player.setDeltaMovement(look.x, 0.1, look.z);
                    player.hurtMarked = true;
                    damageCone(player, 4.0, 8.0f);
                    playEffects(player, ParticleTypes.SWEEP_ATTACK, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0f);
                    return true;
                }));

        register(new HeroSkill("train_body_slam", "Impacto Sísmico", HeroSkill.SkillCategory.TRAINING_BODY, "Treino: Corpo",
                0.0f, 40.0f, 200, "Pisão no solo que atordoa e arremessa inimigos próximos.", 3,
                (player, skill) -> {
                    damageArea(player, 6.0, 10.0f);
                    pushAreaOut(player, 6.0, 1.2);
                    playEffects(player, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE.value(), 1.0f);
                    return true;
                }));

        // --- MENTE (MIND) ---
        register(new HeroSkill("train_mind_mana_burst", "Pulso Arcano", HeroSkill.SkillCategory.TRAINING_MIND, "Treino: Mente",
                25.0f, 0.0f, 80, "Expulsão repentina de ondas de energia mágica em 360 graus.", 1,
                (player, skill) -> {
                    damageArea(player, 5.0, 7.0f);
                    pushAreaOut(player, 5.0, 0.8);
                    playEffects(player, ParticleTypes.WITCH, SoundEvents.ILLUSIONER_CAST_SPELL, 1.5f);
                    return true;
                }));

        register(new HeroSkill("train_mind_meditation", "Meditação Focada", HeroSkill.SkillCategory.TRAINING_MIND, "Treino: Mente",
                0.0f, 20.0f, 240, "Canaliza a energia interior recuperando 40 MP instantaneamente.", 2,
                (player, skill) -> {
                    HeroData data = player.getData(com.heroacademy.common.attachment.ModAttachments.HERO_DATA);
                    data.setCurrentEnergy(Math.min(data.getMaxEnergy(), data.getCurrentEnergy() + 40.0f));
                    playEffects(player, ParticleTypes.ENCHANT, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f);
                    return true;
                }));

        // --- TÉCNICA (TECHNIQUE) ---
        register(new HeroSkill("train_tech_parry", "Aparo Tático", HeroSkill.SkillCategory.TRAINING_TECHNIQUE, "Treino: Técnica",
                0.0f, 20.0f, 90, "Postura perfeita que absorve o próximo golpe com contra-ataque ágil.", 1,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 4));
                    playEffects(player, ParticleTypes.CRIT, SoundEvents.SHIELD_BLOCK, 1.2f);
                    return true;
                }));

        register(new HeroSkill("train_tech_blade_dance", "Dança das Lâminas", HeroSkill.SkillCategory.TRAINING_TECHNIQUE, "Treino: Técnica",
                0.0f, 35.0f, 140, "Giro acrobático veloz causando dano em área com arma cortante.", 2,
                (player, skill) -> {
                    damageArea(player, 4.5, 9.0f);
                    playEffects(player, ParticleTypes.SWEEP_ATTACK, SoundEvents.PLAYER_ATTACK_SWEEP, 1.6f);
                    return true;
                }));
    }

    // ==============================================================
    // 3. CLASS & SUBCLASS SKILLS (Guerreiro, Samurai, Ladino, etc.)
    // ==============================================================
    private static void registerClassSkills() {
        register(new HeroSkill("class_warrior_slash", "Corte Pesado", HeroSkill.SkillCategory.CLASS, "Guerreiro",
                0.0f, 25.0f, 60, "Golpe marcial cortante frontal de alto impacto físico.", 1,
                (player, skill) -> {
                    damageCone(player, 4.5, 11.0f);
                    playEffects(player, ParticleTypes.SWEEP_ATTACK, SoundEvents.PLAYER_ATTACK_CRIT, 1.1f);
                    return true;
                }));

        register(new HeroSkill("class_samurai_iai", "Corte Iaijutsu", HeroSkill.SkillCategory.CLASS, "Samurai",
                0.0f, 30.0f, 80, "Desembainhar ultrarrápido com corte crítico garantido.", 1,
                (player, skill) -> {
                    Vec3 look = player.getLookAngle().scale(1.5);
                    player.setDeltaMovement(look.x, 0.1, look.z);
                    player.hurtMarked = true;
                    damageCone(player, 4.0, 15.0f);
                    playEffects(player, ParticleTypes.CRIT, SoundEvents.PLAYER_ATTACK_STRONG, 1.8f);
                    return true;
                }));

        register(new HeroSkill("class_mage_arcane_bolt", "Dardo Mágico", HeroSkill.SkillCategory.CLASS, "Mago",
                20.0f, 0.0f, 50, "Dispara uma esfera arcana flamejante.", 1,
                (player, skill) -> {
                    damageCone(player, 8.0, 10.0f);
                    playEffects(player, ParticleTypes.ENCHANT, SoundEvents.EVOKER_CAST_SPELL, 1.5f);
                    return true;
                }));

        register(new HeroSkill("class_rogue_shadow_stab", "Ataque Sorrateiro", HeroSkill.SkillCategory.CLASS, "Ladino",
                0.0f, 20.0f, 50, "Golpe rápido em pontos vitais que ignora armadura.", 1,
                (player, skill) -> {
                    damageCone(player, 3.5, 12.0f);
                    playEffects(player, ParticleTypes.DAMAGE_INDICATOR, SoundEvents.PLAYER_ATTACK_CRIT, 1.7f);
                    return true;
                }));

        register(new HeroSkill("class_tank_fortress", "Fortaleza Viva", HeroSkill.SkillCategory.CLASS, "Tanque",
                0.0f, 40.0f, 200, "Fixa os pés no chão, ganhando Resistência IV e Regeneração.", 1,
                (player, skill) -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
                    playEffects(player, ParticleTypes.CRIT, SoundEvents.IRON_GOLEM_HURT, 0.8f);
                    return true;
                }));
    }

    // ==============================================================
    // COMBAT HELPERS
    // ==============================================================
    private static void playEffects(ServerPlayer player, net.minecraft.core.particles.ParticleOptions particle, net.minecraft.sounds.SoundEvent sound, float pitch) {
        ServerLevel lvl = player.serverLevel();
        lvl.sendParticles(particle, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.5, 0.4, 0.1);
        lvl.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0f, pitch);
    }

    private static void damageCone(ServerPlayer player, double range, float damage) {
        AABB box = player.getBoundingBox().inflate(range);
        Vec3 look = player.getLookAngle();
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                Vec3 dir = e.position().subtract(player.position()).normalize();
                if (look.dot(dir) > 0.4) {
                    e.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }
    }

    private static void damageAndEffectCone(ServerPlayer player, double range, float damage, MobEffectInstance... effects) {
        AABB box = player.getBoundingBox().inflate(range);
        Vec3 look = player.getLookAngle();
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                Vec3 dir = e.position().subtract(player.position()).normalize();
                if (look.dot(dir) > 0.4) {
                    e.hurt(player.damageSources().playerAttack(player), damage);
                    for (MobEffectInstance eff : effects) {
                        e.addEffect(new MobEffectInstance(eff));
                    }
                }
            }
        }
    }

    private static void damageArea(ServerPlayer player, double range, float damage) {
        AABB box = player.getBoundingBox().inflate(range);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                e.hurt(player.damageSources().playerAttack(player), damage);
            }
        }
    }

    private static void applyEffectArea(ServerPlayer player, double range, MobEffectInstance effect) {
        AABB box = player.getBoundingBox().inflate(range);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                e.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private static void pushCone(ServerPlayer player, double range, double force) {
        AABB box = player.getBoundingBox().inflate(range);
        Vec3 look = player.getLookAngle();
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                Vec3 dir = e.position().subtract(player.position()).normalize();
                if (look.dot(dir) > 0.4) {
                    e.setDeltaMovement(look.scale(force));
                    e.hurtMarked = true;
                }
            }
        }
    }

    private static void pullArea(ServerPlayer player, double range) {
        AABB box = player.getBoundingBox().inflate(range);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                Vec3 pull = player.position().subtract(e.position()).normalize().scale(1.2);
                e.setDeltaMovement(pull.x, 0.3, pull.z);
                e.hurtMarked = true;
            }
        }
    }

    private static void pushAreaOut(ServerPlayer player, double range, double force) {
        AABB box = player.getBoundingBox().inflate(range);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                Vec3 push = e.position().subtract(player.position()).normalize().scale(force);
                e.setDeltaMovement(push.x, 0.3, push.z);
                e.hurtMarked = true;
            }
        }
    }

    private static void freezeArea(ServerPlayer player, double range, int durationTicks) {
        AABB box = player.getBoundingBox().inflate(range);
        for (LivingEntity e : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (e != player) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 10));
                e.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, durationTicks, 10));
                e.setDeltaMovement(0, 0, 0);
                e.hurtMarked = true;
            }
        }
    }
}
