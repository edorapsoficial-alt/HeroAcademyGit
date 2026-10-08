package com.heroacademy;

import com.heroacademy.common.attachment.ModAttachments;
import com.heroacademy.common.block.ModBlocks;
import com.heroacademy.common.item.ModCreativeTabs;
import com.heroacademy.common.item.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(HeroAcademy.MODID)
public class HeroAcademy {
    public static final String MODID = "heroacademy";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HeroAcademy(IEventBus modEventBus) {
        LOGGER.info("Inicializando Hero Academy Mod - Escola de Magia e Heroismo!");

        // Registros oficiais
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Hero Academy: Setup comum finalizado com sucesso.");
    }
}
