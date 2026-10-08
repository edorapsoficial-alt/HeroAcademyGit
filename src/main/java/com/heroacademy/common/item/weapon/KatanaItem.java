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

public class KatanaItem extends SwordItem {
    public KatanaItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HeroData data = player.getData(ModAttachments.HERO_DATA);

        if (!data.useStamina(25.0f)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("§cVigor insuficiente para corte rápido com a Katana!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // Iaijutsu Flash (Blink rápido à frente com corte)
        Vec3 forward = player.getLookAngle().scale(2.2);
        player.setDeltaMovement(forward.x, 0.15, forward.z);
        player.hurtMarked = true;

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            AABB slashBox = player.getBoundingBox().inflate(3.0);
            for (LivingEntity foe : serverLevel.getEntitiesOfClass(LivingEntity.class, slashBox, e -> e != player)) {
                foe.hurt(serverLevel.damageSources().playerAttack(player), 12.0f);
            }
            serverLevel.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.2);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.2f, 1.6f);
        }

        player.getCooldowns().addCooldown(this, 35);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
