package com.heroacademy.common.block;

import com.heroacademy.common.attachment.HeroData;
import com.heroacademy.common.attachment.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ClassroomDeskBlock extends Block {
    public ClassroomDeskBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (player.isShiftKeyDown()) {
                com.heroacademy.common.world.CampusTeleporter.teleport(serverPlayer);
                return InteractionResult.SUCCESS;
            }

            HeroData data = player.getData(ModAttachments.HERO_DATA);

            // Simulação de aplicação de prova teórica na carteira
            int scoreGain = (int) (Math.random() * 8) + 4;
            int newScore = Math.min(100, data.getExamScore() + scoreGain);
            int creditsEarned = scoreGain * 3; // Créditos concedidos com base no desempenho do aluno
            data.setExamScore(newScore);
            data.addMastery(40);
            data.addImperialCredits(creditsEarned);

            level.playSound(null, pos, SoundEvents.VILLAGER_WORK_LIBRARIAN, SoundSource.BLOCKS, 1.0f, 1.0f);
            player.sendSystemMessage(Component.literal("§6§l📝 [EXAME TEÓRICO DA ACADEMIA]"));
            player.sendSystemMessage(Component.literal("§aVocê realizou uma prova teórica na carteira escolar!"));
            player.sendSystemMessage(Component.literal("§eDesempenho: §f+" + scoreGain + " pontos de nota | +40 XP de Maestria"));
            player.sendSystemMessage(Component.literal("§6Bolsa de Mérito: §e+" + creditsEarned + " Créditos Imperiais §7(Saldo: §6" + data.getImperialCredits() + "§7)"));
            player.sendSystemMessage(Component.literal("§bSua nota atual: §f" + data.getGrade().getTitle() + " (" + newScore + " pts)"));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
