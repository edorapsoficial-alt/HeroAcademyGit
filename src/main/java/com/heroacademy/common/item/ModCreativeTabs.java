package com.heroacademy.common.item;

import com.heroacademy.HeroAcademy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HeroAcademy.MODID);

    public static final Supplier<CreativeModeTab> HERO_ACADEMY_TAB = CREATIVE_MODE_TABS.register(
            "hero_academy_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("§6Hero Academy"))
                    .icon(() -> new ItemStack(ModItems.STUDENT_ID_CARD.get()))
                    .displayItems((parameters, output) -> {
                        // Utilitários e Itens Escolares
                        output.accept(ModItems.STUDENT_ID_CARD.get());
                        output.accept(ModItems.CAMPUS_COMPASS.get());
                        output.accept(ModItems.IMPERIAL_CREDIT.get());
                        output.accept(ModItems.IMPERIAL_TRANSIT_TERMINAL_ITEM.get());
                        output.accept(ModItems.MAGIC_TOME.get());
                        output.accept(ModItems.CLASSROOM_DESK_ITEM.get());

                        // Lanças (Armas de Haste)
                        output.accept(ModItems.TRAINING_SPEAR.get());
                        output.accept(ModItems.IRON_SPEAR.get());
                        output.accept(ModItems.DIAMOND_SPEAR.get());
                        output.accept(ModItems.NETHERITE_SPEAR.get());

                        // Katanas (Armas Orientais)
                        output.accept(ModItems.TRAINING_KATANA.get());
                        output.accept(ModItems.IRON_KATANA.get());
                        output.accept(ModItems.DIAMOND_KATANA.get());
                        output.accept(ModItems.NETHERITE_KATANA.get());

                        // Tonfas (Armas Orientais)
                        output.accept(ModItems.TRAINING_TONFA.get());
                        output.accept(ModItems.IRON_TONFA.get());
                        output.accept(ModItems.DIAMOND_TONFA.get());
                        output.accept(ModItems.NETHERITE_TONFA.get());

                        // Foices (Armas Exóticas)
                        output.accept(ModItems.TRAINING_SCYTHE.get());
                        output.accept(ModItems.IRON_SCYTHE.get());
                        output.accept(ModItems.DIAMOND_SCYTHE.get());
                        output.accept(ModItems.NETHERITE_SCYTHE.get());
                    })
                    .build()
    );
}
