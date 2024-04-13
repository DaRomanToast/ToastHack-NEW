package com.armorhud.mixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.armorhud.Client.TOASTHACK;

@Mixin(Entity.class)
public abstract class EntityMixin {
  @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
  private void onIsInvisible(CallbackInfoReturnable<Boolean> info) {
    if ((Object)this instanceof PlayerEntity && com.armorhud.Client.TOASTHACK.getFeatures().trueSightFeature.isEnabled()) {
      info.setReturnValue(false);
    }
  }
}