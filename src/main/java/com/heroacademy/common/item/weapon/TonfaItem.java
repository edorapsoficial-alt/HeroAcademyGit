package com.heroacademy.common.item.weapon;

import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TonfaItem extends SwordItem {
    public TonfaItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HeroData data = player.getData(ModAttachments.HERO_DATA);

        if (!data.useStamina(15.0f)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("§cVigor insuficiente para guarda marcial com a Tonfa!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 2));

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            AABB parryArea = player.getBoundingBox().inflate(2.5);
            for (LivingEntity foe : serverLevel.getEntitiesOfClass(LivingEntity.class, parryArea, e -> e != player)) {
                Vec3 push = foe.position().subtract(player.position()).normalize().scale(1.2);
                foe.setDeltaMovement(push.x, 0.3, push.z);
            }
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1, player.getZ(), 15, 0.4, 0.4, 0.4, 0.1);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.2f, 1.4f);
        }

        player.getCooldowns().addCooldown(this, 25);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
