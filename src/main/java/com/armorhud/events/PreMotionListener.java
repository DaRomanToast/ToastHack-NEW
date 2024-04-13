package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PreMotionListener
{
	void onPreMotion(final PreMotionEvent event);

	class PreMotionEvent extends CancellableEvent<PreMotionListener>
	{
		public static final int ID = 22;
		@Override
		public void call(PreMotionListener listener) {
			listener.onPreMotion(this);
		}
	}
}
