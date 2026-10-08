package com.heroacademy.common.item;

import com.heroacademy.common.academic.SchoolSchedule;
import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MagicTomeItem extends Item {
    public MagicTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            HeroData data = player.getData(ModAttachments.HERO_DATA);
            boolean isTheoryTime = SchoolSchedule.getCurrentPeriod() == SchoolSchedule.SchoolPeriod.THEORY_CLASS;

            int xpGain = isTheoryTime ? 50 : 20;
            data.addMastery(xpGain);
            data.setExamScore(Math.min(100, data.getExamScore() + 2));

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1, player.getZ(), 25, 0.4, 0.4, 0.4, 0.1);
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            if (isTheoryTime) {
                player.displayClientMessage(Component.literal("§b📖 Você estudou durante o horário de aula! Bônus de +50 XP de Maestria e +2 no Boletim!"), true);
            } else {
                player.displayClientMessage(Component.literal("§7📖 Você revisou seus estudos. +20 XP de Maestria."), true);
            }

            player.getCooldowns().addCooldown(this, 100); // 5 segundos de cooldown entre leituras
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
