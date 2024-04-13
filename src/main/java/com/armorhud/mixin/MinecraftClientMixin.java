package com.armorhud.mixin;

import com.armorhud.ClientInitializer;
import com.armorhud.events.FrameBeginListener;
import com.armorhud.events.UpdateListener;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "setScreen",
			at = @At(value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
					opcode = Opcodes.PUTFIELD))
	private void onScreenOpen(Screen newScreen, CallbackInfo info) {
		ClientInitializer.onScreenChange(newScreen);
	}

	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Shadow
	@Nullable
	public ClientWorld world;

	@Inject(method = "render(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V", shift = At.Shift.AFTER))
	private void onRender(boolean tick, CallbackInfo info) {
		FrameBeginListener.FrameBeginEvent framebeginevent = new FrameBeginListener.FrameBeginEvent();

		DietrichEvents2.global().postInternal(FrameBeginListener.FrameBeginEvent.ID, framebeginevent);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (player != null && world != null) {
			//@formatter:off
			DietrichEvents2.global().postInternal(UpdateListener.UpdateEvent.ID, new UpdateListener.UpdateEvent());
			//@formatter:on
		}
	}
}
