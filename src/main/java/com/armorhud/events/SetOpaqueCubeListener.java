package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface SetOpaqueCubeListener
{
	void onSetOpaqueCube(SetOpaqueCubeEvent event);

	class SetOpaqueCubeEvent extends CancellableEvent<SetOpaqueCubeListener>
	{
		public static final int ID = 27;
		@Override
		public void call(SetOpaqueCubeListener listener) {
			listener.onSetOpaqueCube(this);
		}
	}
}
