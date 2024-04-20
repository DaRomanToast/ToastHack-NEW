package com.armorhud.features;

import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;

import static com.armorhud.ClientInitializer.mc;

public class NoOverlayFeature extends Feature {
	private final BooleanSetting hurtCam;
	private final BooleanSetting particles;
	private final BooleanSetting fullBright;

	public NoOverlayFeature() {
		super("NoOverlay", "Render");
		hurtCam = new BooleanSetting("Hurt Cam", false, this);
		particles = new BooleanSetting("Particles", false, this);
		fullBright = new BooleanSetting("Full Bright", false, this);
	}

	public void render(MatrixStack matrices, float tickDelta) {

		if (fullBright.getValue()) {
			mc.gameRenderer.getLightmapTextureManager().enable();
		} else {
			mc.gameRenderer.getLightmapTextureManager().disable();
		}

		if (hurtCam.getValue()) {
			if (mc.player.hasStatusEffect(StatusEffects.NAUSEA)) {
				mc.player.removeStatusEffect(StatusEffects.NAUSEA);
			}
		}

		if (particles.getValue()) {
			mc.world.setRainGradient(0);
		}
	}
}
