package com.armorhud.features;

import com.armorhud.feature.Feature;

public class AntiCrystalBounceFeature extends Feature {
  public AntiCrystalBounceFeature() {
    super("NoCrystalBounce","Render");
  }

  public static boolean enabled = true;

  public void onEnable() {
    enabled = true;
  }

  public void onDisable() {
    enabled = false;
  }
}