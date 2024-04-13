package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PreActionListener
{
	void onPreAction(final PreActionEvent event);

	class PreActionEvent extends CancellableEvent<PreActionListener>
	{
		public static final int ID = 21;
		@Override
		public void call(PreActionListener listener) {
			listener.onPreAction(this);
		}
	}
}
