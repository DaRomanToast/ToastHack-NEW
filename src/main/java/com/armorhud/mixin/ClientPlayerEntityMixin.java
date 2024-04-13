package com.armorhud.mixin;

import com.armorhud.events.*;

import com.mojang.authlib.GameProfile;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;

import static com.armorhud.Client.TOASTHACK;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	@Unique
	private @Getter @Setter ClientPlayNetworkHandler networkHandler;
	@Unique
	private @Getter @Setter Packet<?> packet;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {

		super(world, profile);

	}

	@Shadow
	protected abstract void autoJump(float dx, float dz);

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		PlayerMoveListener.PlayerMoveEvent event = new PlayerMoveListener.PlayerMoveEvent(movementType, movement);
		DietrichEvents2.global().postInternal(PlayerMoveListener.PlayerMoveEvent.ID, event);
		if (event.isCancelled()) return;
		movementType = event.getMovementType();
		movement = event.getMovement();
		double d = this.getX();
		double e = this.getZ();
		super.move(movementType, movement);
		this.autoJump((float) (this.getX() - d), (float) (this.getZ() - e));
	}


	@Inject(at = {@At("HEAD")},
			method = {
					"tickMovement()V"},
			cancellable = true)
	private void onTickMovement(CallbackInfo ci)
	{
		PlayerTickMovementListener.PlayerTickMovementEvent event = new PlayerTickMovementListener.PlayerTickMovementEvent();
		DietrichEvents2.global().postInternal(PlayerTickMovementListener.PlayerTickMovementEvent.ID, event);
		if (event.isCancelled())
			ci.cancel();
	}

	@Override
	public void jump()
	{
		PlayerJumpListener.PlayerJumpEvent event = new PlayerJumpListener.PlayerJumpEvent();
		DietrichEvents2.global().postInternal(PlayerJumpListener.PlayerJumpEvent.ID, event);
		if (!event.isCancelled())
			super.jump();
	}

	@Override
	public boolean isTouchingWater()
	{
		IsPlayerTouchingWaterListener.IsPlayerTouchingWaterEvent event = new IsPlayerTouchingWaterListener.IsPlayerTouchingWaterEvent(super.isTouchingWater());
		DietrichEvents2.global().postInternal(IsPlayerTouchingWaterListener.IsPlayerTouchingWaterEvent.ID, event);
		return event.isTouchingWater();
	}

	@Override
	public boolean isInLava()
	{
		IsPlayerInLavaListener.IsPlayerInLavaEvent event = new IsPlayerInLavaListener.IsPlayerInLavaEvent(super.isInLava());
		DietrichEvents2.global().postInternal(IsPlayerInLavaListener.IsPlayerInLavaEvent.ID, event);
		return event.isInLava();
	}
	@Inject(at = {@At("HEAD")}, method = {"sendMovementPackets()V"}, cancellable = true)
	private void onSendMovementPackets(CallbackInfo ci)
	{
		SendMovementPacketsListener.SendMovementPacketsEvent event = new SendMovementPacketsListener.SendMovementPacketsEvent();
		if (event.isCancelled())
		{
			ci.cancel();
			return;
		}
		PreActionListener.PreActionEvent preactionevent = new PreActionListener.PreActionEvent();
		DietrichEvents2.global().postInternal(PreActionListener.PreActionEvent.ID, preactionevent);
	}

	@Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z", shift = At.Shift.BEFORE)}, method = {"sendMovementPackets()V"})
	private void onSendMovementPacketsHEAD(CallbackInfo ci)
	{

		PostActionListener.PostActionEvent postevent = new PostActionListener.PostActionEvent();
		PreMotionListener.PreMotionEvent preevent = new PreMotionListener.PreMotionEvent();

		DietrichEvents2.global().postInternal(PostActionListener.PostActionEvent.ID, postevent);
		DietrichEvents2.global().postInternal(PreMotionListener.PreMotionEvent.ID, preevent);
	}

	@Inject(at = {@At("TAIL")}, method = {"sendMovementPackets()V"})
	private void onSendMovementPacketsTAIL(CallbackInfo ci)
	{
		PostMotionListener.PostMotionEvent postmotionevent = new PostMotionListener.PostMotionEvent();

		DietrichEvents2.global().postInternal(PostActionListener.PostActionEvent.ID, postmotionevent);
	}

	@Inject(at = @At(value = "TAIL"), method = "tick()V")
	private void onPostTick(CallbackInfo ci)
	{
		PostUpdateListener.PostUpdateEvent postupdateevent = new PostUpdateListener.PostUpdateEvent();

		DietrichEvents2.global().postInternal(PostUpdateListener.PostUpdateEvent.ID, postupdateevent);
	}
	@Redirect(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
			ordinal = 0), method = "tickMovement()V")
	private boolean isUsingItem(ClientPlayerEntity player) {
		if (TOASTHACK.getFeatures().noSlowFeature.isEnabled())
			return false;

		return player.isUsingItem();
	}
}
