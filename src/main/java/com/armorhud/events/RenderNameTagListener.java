package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface RenderNameTagListener
{
	void onRenderNameTag(final RenderNameTagEvent event);

	class RenderNameTagEvent extends CancellableEvent<RenderNameTagListener>
	{
		public static final int ID = 24;
		@Override
		public void call(RenderNameTagListener listener) {
			listener.onRenderNameTag(this);
		}
	}
}
