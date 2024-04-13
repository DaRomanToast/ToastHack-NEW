package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface GameLeaveListener
{
	void onGameLeave(final GameLeaveEvent event);

	class GameLeaveEvent extends CancellableEvent<GameLeaveListener>
	{
		public static final int ID = 6;
		@Override
		public void call(GameLeaveListener listener) {
			listener.onGameLeave(this);
		}
	}
}
