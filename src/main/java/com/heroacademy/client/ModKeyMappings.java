package com.heroacademy.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = com.heroacademy.HeroAcademy.MODID, value = Dist.CLIENT)
public class ModKeyMappings {
    private static final String CATEGORY = "key.categories.heroacademy";

    public static final KeyMapping KEY_ABILITY_Z = new KeyMapping(
            "key.heroacademy.ability_z",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            CATEGORY
    );

    public static final KeyMapping KEY_ABILITY_X = new KeyMapping(
            "key.heroacademy.ability_x",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            CATEGORY
    );

    public static final KeyMapping KEY_ABILITY_C = new KeyMapping(
            "key.heroacademy.ability_c",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    );

    public static final KeyMapping KEY_ABILITY_V = new KeyMapping(
            "key.heroacademy.ability_v",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    public static final KeyMapping KEY_ABILITY_G = new KeyMapping(
            "key.heroacademy.ability_g",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    public static final KeyMapping KEY_OPEN_MENU = new KeyMapping(
            "key.heroacademy.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            CATEGORY
    );

    public static final KeyMapping KEY_SWITCH_BAR = new KeyMapping(
            "key.heroacademy.switch_bar",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(KEY_ABILITY_Z);
        event.register(KEY_ABILITY_X);
        event.register(KEY_ABILITY_C);
        event.register(KEY_ABILITY_V);
        event.register(KEY_ABILITY_G);
        event.register(KEY_OPEN_MENU);
        event.register(KEY_SWITCH_BAR);
    }
}
