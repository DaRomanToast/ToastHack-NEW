package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;

public interface GUIRenderListener
{
	void onRenderGUI(GUIRenderEvent event);

	class GUIRenderEvent extends CancellableEvent<GUIRenderListener>
	{
		public static final int ID = 9;
		private @Getter @Setter DrawContext drawContext;
		private @Getter @Setter float partialTicks;

		public GUIRenderEvent(DrawContext drawContext, float partialTicks)
		{
			this.drawContext = drawContext;
			this.partialTicks = partialTicks;
		}

		@Override
		public void call(GUIRenderListener listener) {
			listener.onRenderGUI(this);
		}
	}
}