package com.heroacademy.client;

import com.heroacademy.HeroAcademy;
import com.heroacademy.client.gui.SkillTreeScreen;
import com.heroacademy.common.network.KeybindCastPayload;
import com.heroacademy.common.power.AbilitySlot;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HeroAcademy.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        // Tecla K abre a tela de Árvores de Habilidade e Atributos se não estiver em tela de chat/GUI
        if (ModKeyMappings.KEY_OPEN_MENU.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new SkillTreeScreen());
            }
            return;
        }

        if (mc.screen != null) {
            return;
        }

        if (ModKeyMappings.KEY_SWITCH_BAR.consumeClick()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.heroacademy.common.network.SwitchSkillBarPayload());
            return;
        }

        if (ModKeyMappings.KEY_ABILITY_Z.consumeClick()) {
            cast(AbilitySlot.SLOT_Z);
        } else if (ModKeyMappings.KEY_ABILITY_X.consumeClick()) {
            cast(AbilitySlot.SLOT_X);
        } else if (ModKeyMappings.KEY_ABILITY_C.consumeClick()) {
            cast(AbilitySlot.SLOT_C);
        } else if (ModKeyMappings.KEY_ABILITY_V.consumeClick()) {
            cast(AbilitySlot.SLOT_V);
        } else if (ModKeyMappings.KEY_ABILITY_G.consumeClick()) {
            cast(AbilitySlot.SLOT_G);
        }
    }

    private static void cast(AbilitySlot slot) {
        PacketDistributor.sendToServer(new KeybindCastPayload(slot.getIndex()));
    }
}
