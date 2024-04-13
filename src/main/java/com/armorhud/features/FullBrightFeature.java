package com.armorhud.features;

import com.armorhud.feature.Feature;

import static com.armorhud.Client.MC;

public class FullBrightFeature extends Feature {
    public FullBrightFeature() {
        super("FullBright", "Render");
    }

    @Override
    public void onEnable() {
        if (MC.options != null) {
            if (MC.worldRenderer != null) {
                MC.options.getGamma().setValue(10d);
                MC.worldRenderer.reload();
            }
        }
    }

    @Override
    public void onDisable() {
        if (MC.options != null) {
            if (MC.worldRenderer != null) {
                MC.options.getGamma().setValue(1d);
                MC.worldRenderer.reload();
            }
        }
    }
}
