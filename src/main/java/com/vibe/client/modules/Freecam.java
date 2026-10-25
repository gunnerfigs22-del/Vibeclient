package com.vibe.client.modules;

import com.mojang.authlib.GameProfile;
import com.vibe.client.Category;
import com.vibe.client.Chat;
import com.vibe.client.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Detaches the camera from your body. Your real player stands still while you fly around.
 * Fly with WASD, Space (up) and Shift (down). Look around with the mouse.
 */
public class Freecam extends Module {
    private static final double SPEED = 0.8;

    private RemotePlayer cam;
    private ClientInput savedInput;

    public Freecam() { super("Freecam", Category.RENDER, -1); }

    @Override
    public void onEnable(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;

        cam = new RemotePlayer(mc.level, new GameProfile(UUID.randomUUID(), "Freecam"));
        cam.copyPosition(mc.player);
        cam.setInvisible(true);
        cam.setNoGravity(true);
        cam.noPhysics = true;
        mc.level.addEntity(cam);

        // Freeze the real player: swap in an empty input so WASD/jump don't move the body.
        savedInput = mc.player.input;
        mc.player.input = new ClientInput();

        mc.setCameraEntity(cam);
        Chat.info("Freecam on. WASD / Space / Shift to fly.");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (cam == null || mc.level == null || mc.player == null) return;

        // Camera looks wherever the (frozen) player's mouse-look points
        cam.setYRot(mc.player.getYRot());
        cam.setXRot(mc.player.getXRot());
        cam.yRotO = cam.getYRot();
        cam.xRotO = cam.getXRot();
        cam.setYHeadRot(cam.getYRot());

        Vec3 look = cam.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
        Vec3 move = Vec3.ZERO;
        if (mc.options.keyUp.isDown())    move = move.add(look);
        if (mc.options.keyDown.isDown())  move = move.subtract(look);
        if (mc.options.keyRight.isDown()) move = move.add(right);
        if (mc.options.keyLeft.isDown())  move = move.subtract(right);
        if (mc.options.keyJump.isDown())  move = move.add(0, 1, 0);
        if (mc.options.keyShift.isDown()) move = move.add(0, -1, 0);

        if (move.lengthSqr() > 0) {
            move = move.normalize().scale(SPEED);
            cam.xo = cam.getX(); cam.yo = cam.getY(); cam.zo = cam.getZ();
            cam.setPos(cam.getX() + move.x, cam.getY() + move.y, cam.getZ() + move.z);
        }
    }

    @Override
    public void onDisable(Minecraft mc) {
        if (mc.player != null) {
            mc.setCameraEntity(mc.player);
            if (savedInput != null) mc.player.input = savedInput;
        }
        if (cam != null) cam.discard();
        cam = null;
        savedInput = null;
    }
}
