package com.vibe.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Meteor-style menu: one draggable window per category. Left click a module to toggle it. */
public class ClickGui extends Screen {
    private static final int WIDTH = 110, HEADER_H = 16, ROW_H = 14, GAP = 8;
    private static final int ACCENT = 0xFF2A9D8F;

    // Window positions persist between opens
    private static final Map<Category, int[]> POS = new EnumMap<>(Category.class);

    private Category dragging;
    private double dragOffX, dragOffY;

    public ClickGui() {
        super(Component.literal("Vibe Client"));
    }

    @Override
    protected void init() {
        int i = 0;
        for (Category c : Category.values()) {
            POS.computeIfAbsent(c, k -> new int[]{0, 0});
            int[] p = POS.get(c);
            if (p[0] == 0 && p[1] == 0) { p[0] = 12 + i * (WIDTH + GAP); p[1] = 12; }
            i++;
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);

        for (Category c : Category.values()) {
            int[] p = POS.get(c);
            List<Module> mods = VibeClient.MODULES.inCategory(c);
            int x = p[0], y = p[1];

            g.fill(x, y, x + WIDTH, y + HEADER_H, 0xFF1B1B1B);
            g.fill(x, y + HEADER_H - 2, x + WIDTH, y + HEADER_H, ACCENT);
            g.text(this.font, c.label, x + 4, y + 4, 0xFFFFFFFF, true);

            int ry = y + HEADER_H;
            for (Module m : mods) {
                boolean hover = mouseX >= x && mouseX < x + WIDTH && mouseY >= ry && mouseY < ry + ROW_H;
                int bg = m.isEnabled() ? ACCENT : (hover ? 0xFF3A3A3A : 0xFF242424);
                g.fill(x, ry, x + WIDTH, ry + ROW_H, bg);
                g.text(this.font, m.name, x + 4, ry + 3, 0xFFFFFFFF, true);
                ry += ROW_H;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        if (event.button() == 0) {
            for (Category c : Category.values()) {
                int[] p = POS.get(c);
                int x = p[0], y = p[1];
                if (mx < x || mx >= x + WIDTH) continue;

                if (my >= y && my < y + HEADER_H) {          // grab the window header to drag
                    dragging = c;
                    dragOffX = mx - x;
                    dragOffY = my - y;
                    return true;
                }
                int ry = y + HEADER_H;
                for (Module m : VibeClient.MODULES.inCategory(c)) {
                    if (my >= ry && my < ry + ROW_H) {
                        m.toggle();
                        return true;
                    }
                    ry += ROW_H;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging != null) {
            int[] p = POS.get(dragging);
            p[0] = (int) (event.x() - dragOffX);
            p[1] = (int) (event.y() - dragOffY);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {   // Right Shift again closes it
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }
}
