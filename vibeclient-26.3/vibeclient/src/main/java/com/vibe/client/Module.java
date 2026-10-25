package com.vibe.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public abstract class Module {
    public final String name;
    public final Category category;
    public final int defaultKey;
    public KeyMapping keyMapping;
    private boolean enabled;

    protected Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        this.defaultKey = defaultKey;
    }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        Minecraft mc = Minecraft.getInstance();
        if (enabled) onEnable(mc); else onDisable(mc);
    }

    public void onEnable(Minecraft mc) {}
    public void onDisable(Minecraft mc) {}
    public void onTick(Minecraft mc) {}
}
