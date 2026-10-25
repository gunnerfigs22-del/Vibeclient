package com.vibe.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class VibeClient implements ClientModInitializer {
    private static KeyMapping guiKey;
    public static final String MOD_ID = "vibeclient";
    public static final ModuleManager MODULES = new ModuleManager();

    // Translation key becomes key.category.vibeclient.main
    private static final KeyMapping.Category CATEGORY =
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    @Override
    public void onInitializeClient() {
        // One rebindable key per module (Options > Controls > Key Binds > Vibe Client)
        for (Module m : MODULES.all()) {
            m.keyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.vibeclient." + m.name.toLowerCase(),
                InputConstants.Type.KEYSYM,
                m.defaultKey,
                CATEGORY));
        }

        guiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.vibeclient.gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.consumeClick()) {
                if (mc.screen == null) mc.setScreen(new ClickGui());
            }
            if (mc.player == null) return;
            for (Module m : MODULES.all()) {
                while (m.keyMapping.consumeClick()) m.toggle();
                if (m.isEnabled()) m.onTick(mc);
            }
        });

        // Active-module list, top-right
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(MOD_ID, "module_list"),
            (graphics, delta) -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return;
                int y = 4;
                int screenW = mc.getWindow().getGuiScaledWidth();
                for (Module m : MODULES.all()) {
                    if (!m.isEnabled()) continue;
                    int w = mc.font.width(m.name);
                    graphics.text(mc.font, m.name, screenW - w - 4, y, 0xFF55FFFF, true);
                    y += 10;
                }
            });
    }
}
