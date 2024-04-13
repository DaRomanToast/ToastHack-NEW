package com.armorhud.features;

import com.armorhud.feature.Feature;
import com.armorhud.setting.DecimalSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;

public class ReachFeature extends Feature {


    public final DecimalSetting areac = new DecimalSetting("ReachFeature", 3.2, this);

    public ReachFeature() {
        super("Reach", "Combat");
    }

}