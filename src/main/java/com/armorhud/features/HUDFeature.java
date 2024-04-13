package com.armorhud.features;

import com.armorhud.events.GUIRenderListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.entity.Entity;

import static com.armorhud.ClientInitializer.mc;


public class HUDFeature extends Feature implements GUIRenderListener {

    public HUDFeature() {
        super("HUD", "Render");
        }

@Override
public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(GUIRenderListener.GUIRenderEvent.ID, this);
        }

@Override
public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(GUIRenderListener.GUIRenderEvent.ID, this);
        }


@Override
public void onRenderGUI(GUIRenderListener.GUIRenderEvent event) {
        DrawContext matrices = event.getDrawContext();
        matrices.getMatrices().push();
        matrices.getMatrices().translate(2, 2, 2);
        matrices.drawTextWithShadow(mc.textRenderer, "Toast", 2, 2, 0x6FA8DC);
        matrices.drawTextWithShadow(mc.textRenderer, "Hack", 2 + mc.textRenderer.getWidth("THack"), 2, 0xFFFFFF);
        assert mc.player != null;
        mc.player.getHealth();
        if (mc.player.getHealth() >= 15) {
        matrices.drawTextWithShadow(mc.textRenderer, "HP: " + Math.round(mc.player.getHealth()), 2, 6 + mc.textRenderer.fontHeight * 2, 0xFF0FFF33);
        } else if (mc.player.getHealth() > 10) {
        matrices.drawTextWithShadow(mc.textRenderer, "HP: " + Math.round(mc.player.getHealth()), 2, 6 + mc.textRenderer.fontHeight * 2, 0xFFFF8C00);
        } else if (mc.player.isAlive()) {
        matrices.drawTextWithShadow(mc.textRenderer, "HP: " + Math.round(mc.player.getHealth()), 2, 6 + mc.textRenderer.fontHeight * 2, 0xFFFF0A0A);
        } else {
        matrices.drawTextWithShadow(mc.textRenderer, "HP: " + Math.round(mc.player.getHealth()), 2, 6 + mc.textRenderer.fontHeight * 2, 0xFF000000);
        }
        mc.getCurrentFps();
        matrices.drawTextWithShadow(mc.textRenderer, "FPS: " + mc.getCurrentFps(), 2, 4 + mc.textRenderer.fontHeight, 0xFFFFFF);
        mc.player.getBlockPos();
        matrices.drawTextWithShadow(mc.textRenderer, "XYZ: " + mc.player.getBlockPos().getX() + " " + mc.player.getBlockPos().getY() + " " + mc.player.getBlockPos().getZ(), 2, 8 + mc.textRenderer.fontHeight * 3, 0xFFFFFF);
        mc.player.getDisplayName();
        matrices.drawTextWithShadow(mc.textRenderer, "Username: " + mc.player.getDisplayName().getString(), 2, 10 + mc.textRenderer.fontHeight * 4, 0xFF6A54);
        cameraEntity().getHorizontalFacing();
        matrices.drawTextWithShadow(mc.textRenderer, "Facing: " + cameraEntity().getHorizontalFacing(), 2, 22 + mc.textRenderer.fontHeight * 4, 0x00ffff);
        matrices.getMatrices().pop();
        }
private static Entity cameraEntity() {
        return mc.getCameraEntity();
        }
        }