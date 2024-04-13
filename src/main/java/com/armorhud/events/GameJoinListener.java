package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface GameJoinListener
{
	void onGameJoin(final GameJoinEvent event);

	class GameJoinEvent extends CancellableEvent<GameJoinListener>
	{
		public static final int ID = 5;
		@Override
		public void call(GameJoinListener listener) {
			listener.onGameJoin(this);
		}
	}
}
