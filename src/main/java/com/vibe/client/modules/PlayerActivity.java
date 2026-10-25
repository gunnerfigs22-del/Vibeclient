package com.vibe.client.modules;

import com.vibe.client.Category;
import com.vibe.client.Chat;
import com.vibe.client.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Tells you (in chat, only you see it) when players join or leave the server's player list,
 * and when a player comes into your render distance.
 */
public class PlayerActivity extends Module {
    private Set<String> online = new HashSet<>();
    private Set<String> nearby = new HashSet<>();
    private int timer;

    public PlayerActivity() { super("PlayerActivity", Category.PLAYER, -1); }

    @Override
    public void onEnable(Minecraft mc) {
        online = onlineNames(mc);
        nearby = nearbyNames(mc);
        timer = 0;
        Chat.info("PlayerActivity on.");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;
        if (++timer < 10) return;
        timer = 0;

        Set<String> nowOnline = onlineNames(mc);
        for (String n : nowOnline) if (!online.contains(n)) Chat.info("+ " + n + " joined");
        for (String n : online) if (!nowOnline.contains(n)) Chat.info("- " + n + " left");
        online = nowOnline;

        Set<String> nowNearby = nearbyNames(mc);
        for (Player p : mc.level.players()) {
            String n = p.getName().getString();
            if (p != mc.player && !nearby.contains(n)) {
                Chat.info(n + " is nearby at X " + (int) p.getX() + ", Y " + (int) p.getY() + ", Z " + (int) p.getZ());
            }
        }
        nearby = nowNearby;
    }

    private static Set<String> onlineNames(Minecraft mc) {
        Set<String> names = new HashSet<>();
        if (mc.getConnection() == null) return names;
        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            names.add(info.getProfile().name());
        }
        return names;
    }

    private static Set<String> nearbyNames(Minecraft mc) {
        Set<String> names = new HashSet<>();
        if (mc.level == null) return names;
        for (Player p : mc.level.players()) {
            if (p != mc.player) names.add(p.getName().getString());
        }
        return names;
    }
}
