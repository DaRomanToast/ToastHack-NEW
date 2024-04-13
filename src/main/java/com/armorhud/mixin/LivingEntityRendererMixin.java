package com.armorhud.mixin;

import com.armorhud.Client;
import com.armorhud.RotationFaker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
{
	private float origYaw;
	private float origPitch;
	private float origPrevYaw;
	private float origPrevPitch;
	private boolean wasLastTimeFaked = false;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void renderHead(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		RotationFaker rotationFaker = Client.TOASTHACK.getRotationFaker();
		if (livingEntity == Client.MC.player && rotationFaker.wasFakingLastTick())
		{
			origPitch = Client.MC.player.getPitch();
			origYaw = Client.MC.player.headYaw;
			origPrevYaw = Client.MC.player.prevYaw;
			origPrevPitch = Client.MC.player.prevPitch;
			Client.MC.player.headYaw = rotationFaker.getFakedYaw();
			Client.MC.player.setPitch(rotationFaker.getFakedPitch());
			if (wasLastTimeFaked)
			{
				Client.MC.player.prevHeadYaw = rotationFaker.getLastFakedYaw();
				Client.MC.player.prevPitch = rotationFaker.getLastFakedPitch();
			}
			wasLastTimeFaked = true;
		}
		else
		{
			if (wasLastTimeFaked)
			{
				Client.MC.player.prevHeadYaw = rotationFaker.getLastFakedYaw();
				Client.MC.player.prevPitch = rotationFaker.getLastFakedPitch();
			}
			wasLastTimeFaked = false;
		}


	}

	@Inject(method = "render", at = @At("TAIL"))
	private void renderTail(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if (livingEntity == Client.MC.player && Client.TOASTHACK.getRotationFaker().wasFakingLastTick())
		{
			Client.MC.player.headYaw = origYaw;
			Client.MC.player.setPitch(origPitch);
			Client.MC.player.prevHeadYaw = origPrevYaw;
			Client.MC.player.prevPitch = origPrevPitch;
		}
	}
}
