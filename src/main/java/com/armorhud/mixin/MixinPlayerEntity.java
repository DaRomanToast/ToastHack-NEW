package com.armorhud.mixin;

import com.armorhud.Client;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.armorhud.ClientInitializer.mc;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z", opcode = Opcodes.PUTFIELD))
    public void noClipHook(PlayerEntity playerEntity, boolean value) {
        if (Client.TOASTHACK.getFeatures().freecamFeature.isEnabled() && !playerEntity.isOnGround()) {
            playerEntity.noClip = true;
        } else {
            playerEntity.noClip = playerEntity.isSpectator();
        }
    }
}
