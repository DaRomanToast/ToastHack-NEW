package com.armorhud.mixin;

import com.armorhud.events.KeyPressListener;
import com.armorhud.mixinterface.IMouse;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin implements IMouse
{
	@Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
	private void onOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci)
	{
		KeyPressListener.KeyPressEvent event = new KeyPressListener.KeyPressEvent(button + GLFW.GLFW_KEY_LAST + 1, 0, action, mods);
		DietrichEvents2.global().postInternal(KeyPressListener.KeyPressEvent.ID, event);
		if (event.isCancelled())
			ci.cancel();
	}

	@Shadow
	private void onMouseButton(long window, int button, int action, int mods)
	{

	}

	@Override
	public void cwOnMouseButton(long window, int button, int action, int mods)
	{
		onMouseButton(window, button, action, mods);
	}
}
