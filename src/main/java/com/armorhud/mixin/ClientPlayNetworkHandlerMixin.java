package com.armorhud.mixin;

import com.armorhud.events.*;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private ClientWorld world;
	private boolean worldNotNull;
	@Inject(at = @At("HEAD"), method = "onGameJoin")
	private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
		worldNotNull = world != null;
	}

	@Inject(at = @At("TAIL"), method = "onGameJoin")
	private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
		if (worldNotNull) {
			GameLeaveListener.GameLeaveEvent gameleaveevent = new GameLeaveListener.GameLeaveEvent();

			DietrichEvents2.global().postInternal(GameLeaveListener.GameLeaveEvent.ID, gameleaveevent);
		}
		GameJoinListener.GameJoinEvent gamejoinevent = new GameJoinListener.GameJoinEvent();

		DietrichEvents2.global().postInternal(GameJoinListener.GameJoinEvent.ID, gamejoinevent);
	}

	@Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
	private void onSendPacket(Packet<?> packet, CallbackInfo ci){
		PacketListener.PacketEvent packetEvent = new PacketListener.PacketEvent(packet, Type.OUTGOING);
		//@formatter:off
		DietrichEvents2.global().postInternal(PacketListener.PacketEvent.ID, packetEvent);
		//@formatter:on
		if(packetEvent.isCancelled()){
			ci.cancel();
		}
	}



	@Inject(at = @At("HEAD"), method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V")
	private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {

	}

	@Inject(at = @At("HEAD"), method = "onPlaySoundFromEntity(Lnet/minecraft/network/packet/s2c/play/PlaySoundFromEntityS2CPacket;)V")
	private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {

	}

	@ModifyArg(method = "onExplosion(Lnet/minecraft/network/packet/s2c/play/ExplosionS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/Explosion;affectWorld(Z)V"), index = 0)
	private boolean onExplosion(boolean particles) {
		return false;
	}

}
