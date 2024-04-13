package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface StopUsingItemListener
{
	void onStopUsingItem(StopUsingItemEvent event);

	class StopUsingItemEvent extends CancellableEvent<StopUsingItemListener>
	{
		public static final int ID = 28;
		@Override
		public void call(StopUsingItemListener listener) {
			listener.onStopUsingItem(this);
		}
	}
}
