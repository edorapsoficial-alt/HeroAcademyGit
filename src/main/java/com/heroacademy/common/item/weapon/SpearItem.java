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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpearItem extends SwordItem {
    public SpearItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HeroData data = player.getData(ModAttachments.HERO_DATA);

        if (!data.useStamina(20.0f)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("§cVigor insuficiente para investida com a Lança!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // Investida perfurante para frente
        Vec3 thrust = player.getLookAngle().scale(1.6);
        player.setDeltaMovement(thrust.x, 0.2, thrust.z);
        player.hurtMarked = true;

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Vec3 targetPos = player.position().add(player.getLookAngle().scale(3.5));
            AABB hitBox = new AABB(targetPos.x - 1.5, targetPos.y - 1, targetPos.z - 1.5, targetPos.x + 1.5, targetPos.y + 2, targetPos.z + 1.5);
            for (LivingEntity foe : serverLevel.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player)) {
                foe.hurt(serverLevel.damageSources().playerAttack(player), 9.0f);
            }
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, targetPos.x, targetPos.y + 1, targetPos.z, 5, 0.2, 0.2, 0.2, 0.0);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.3f);
        }

        player.getCooldowns().addCooldown(this, 30); // 1.5s de cooldown
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
