package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface FrameBeginListener
{

	void onFrameBegin(final FrameBeginEvent event);

	class FrameBeginEvent extends CancellableEvent<FrameBeginListener>
	{

		public static final int ID = 4;
		@Override
		public void call(FrameBeginListener listener) {
			listener.onFrameBegin(this);
		}
	}
}
