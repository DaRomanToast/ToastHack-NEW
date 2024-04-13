package com.armorhud.mixin;

import com.armorhud.Client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class XRayLighting {

    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
    public void getLuminance(CallbackInfoReturnable<Integer> cir) {
        if (Client.TOASTHACK.getFeatures().xRayFeature.isEnabled()) {
            cir.setReturnValue(15);
        }
    }
}