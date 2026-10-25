package com.vibe.client.modules;

import com.vibe.client.Category;
import com.vibe.client.Chat;
import com.vibe.client.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/**
 * Scans the chunks your game has loaded and tells you (in chat, only you see it)
 * when a chunk has a lot of storage blocks: chests, barrels, shulkers, hoppers, furnaces, droppers.
 */
public class StashFinder extends Module {
    private static final int MIN_STORAGE = 10;  // storage blocks in one chunk to count as a "stash"
    private static final int SCAN_EVERY_TICKS = 40;

    private final Set<Long> reported = new HashSet<>();
    private int timer;

    public StashFinder() { super("StashFinder", Category.WORLD, GLFW.GLFW_KEY_UNKNOWN); }

    @Override
    public void onEnable(Minecraft mc) {
        reported.clear();
        timer = SCAN_EVERY_TICKS;
        Chat.info("StashFinder on. Chunks with " + MIN_STORAGE + "+ storage blocks will be reported.");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;
        if (++timer < SCAN_EVERY_TICKS) return;
        timer = 0;

        int pcx = mc.player.blockPosition().getX() >> 4;
        int pcz = mc.player.blockPosition().getZ() >> 4;
        int r = mc.options.renderDistance().get();

        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                int cx = pcx + dx, cz = pcz + dz;
                long key = (((long) cx) << 32) ^ (cz & 0xFFFFFFFFL);
                if (reported.contains(key)) continue;

                LevelChunk chunk = mc.level.getChunk(cx, cz);
                int count = 0;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (isStorage(be)) count++;
                }
                if (count >= MIN_STORAGE) {
                    reported.add(key);
                    Chat.info("Possible stash: " + count + " storage blocks near X " + (cx * 16 + 8) + ", Z " + (cz * 16 + 8));
                }
            }
        }
    }

    private static boolean isStorage(BlockEntity be) {
        return be instanceof ChestBlockEntity
            || be instanceof BarrelBlockEntity
            || be instanceof ShulkerBoxBlockEntity
            || be instanceof HopperBlockEntity
            || be instanceof DispenserBlockEntity
            || be instanceof AbstractFurnaceBlockEntity;
    }
}
