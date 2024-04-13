package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public interface RenderListener
{
	void onRender(RenderEvent event);

	class RenderEvent extends CancellableEvent<RenderListener>
	{
		public static final int ID = 23;
		private @Getter @Setter MatrixStack matrixStack;
		private @Getter @Setter float partialTicks;

		public RenderEvent(MatrixStack matrixStack, float partialTicks)
		{
			this.matrixStack = matrixStack;
			this.partialTicks = partialTicks;
		}

		@Override
		public void call(RenderListener listener) {
			listener.onRender(this);
		}
	}
}
