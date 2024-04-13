package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface UpdateListener
{
	void onUpdate();

	class UpdateEvent extends CancellableEvent<UpdateListener>
	{
		public static final int ID = 29;
		@Override
		public void call(UpdateListener listener) {
			listener.onUpdate();
		}
	}
}

