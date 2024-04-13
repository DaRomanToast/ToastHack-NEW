package com.armorhud.mixin;

import com.armorhud.Client;
import com.armorhud.features.FullBrightFeature;
import com.armorhud.features.XRayFeature;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        FullBrightFeature fullBrightFeature = Client.TOASTHACK.getFeatures().fullBrightFeature;
        if (fullBrightFeature.isEnabled()) {
            args.set(2, 0xFFFFFFFF);
        }
    }
}