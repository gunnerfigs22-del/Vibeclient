package com.vibe.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Client-only chat messages (only you see them; nothing is sent to the server). */
public final class Chat {
    private Chat() {}

    public static void info(String msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.displayClientMessage(Component.literal("[Vibe] " + msg), false);
    }
}
