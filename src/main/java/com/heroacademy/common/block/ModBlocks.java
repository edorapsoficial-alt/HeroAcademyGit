package com.heroacademy.common.block;

import com.heroacademy.HeroAcademy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(HeroAcademy.MODID);

    public static final DeferredBlock<Block> CLASSROOM_DESK = BLOCKS.register(
            "classroom_desk",
            () -> new ClassroomDeskBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion())
    );

    public static final DeferredBlock<Block> IMPERIAL_TRANSIT_TERMINAL = BLOCKS.register(
            "imperial_transit_terminal",
            () -> new ImperialTransitTerminalBlock(BlockBehaviour.Properties.of()
                    .strength(3.5f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 14)
                    .noOcclusion())
    );
}
