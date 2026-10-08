package com.heroacademy.common.item;

import com.heroacademy.common.network.OpenStudentIDPayload;
import com.heroacademy.common.world.CampusTeleporter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class StudentIDCardItem extends Item {
    public StudentIDCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (player.isShiftKeyDown()) {
                // Shift + Clique Direito teleporta para o Campus da Academia
                CampusTeleporter.teleport(serverPlayer);
            } else {
                // Clique Direito normal abre a Carteirinha de Estudante
                PacketDistributor.sendToPlayer(serverPlayer, new OpenStudentIDPayload());
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.2f);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7[Clique Direito] §bAbrir Carteirinha & Deck de Habilidades"));
        tooltipComponents.add(Component.literal("§7[Shift + Clique Direito] §eTeleportar para o Campus da Academia"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
