package com.heroacademy.common.item;

import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.network.SyncHeroDataPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ImperialCreditItem extends Item {

    public ImperialCreditItem(Properties properties) {
        super(properties.stacksTo(64).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            HeroData data = serverPlayer.getData(ModAttachments.HERO_DATA);
            int valuePerCoin = 100;
            int totalDeposit = valuePerCoin;

            if (serverPlayer.isShiftKeyDown()) {
                // Deposita todo o stack segurado
                int count = stack.getCount();
                totalDeposit = count * valuePerCoin;
                stack.setCount(0);
            } else {
                stack.shrink(1);
            }

            data.addImperialCredits(totalDeposit);
            PacketDistributor.sendToPlayer(serverPlayer, SyncHeroDataPayload.from(data));

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.2f);
            serverPlayer.displayClientMessage(
                    Component.literal("§6🪙 [Banco Imperial] §a+" + totalDeposit + " Créditos depositados! Saldo: §e" + data.getImperialCredits()),
                    true
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("§7Moeda oficial de curso arcano do Império e da Academia."));
        tooltipComponents.add(Component.literal("§eValor: §6100 Créditos Imperiais"));
        tooltipComponents.add(Component.literal("§a[Clique Direito] §fDepositar 1 moeda na sua conta do Estudante"));
        tooltipComponents.add(Component.literal("§a[Shift + Clique] §fDepositar todas as moedas da mão"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
