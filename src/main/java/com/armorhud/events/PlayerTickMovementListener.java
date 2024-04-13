package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PlayerTickMovementListener
{
	void onPlayerTickMovement(PlayerTickMovementEvent event);

	class PlayerTickMovementEvent extends CancellableEvent<PlayerTickMovementListener>
	{
		public static final int ID = 17;
		@Override
		public void call(PlayerTickMovementListener listener) {
			listener.onPlayerTickMovement(this);
		}
	}
}
