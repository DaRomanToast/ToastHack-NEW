package com.armorhud.mixin;


import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SplashOverlay.class)
public class SplashScreenMixin {

    @Shadow
    @Final
    private static Identifier LOGO;


}