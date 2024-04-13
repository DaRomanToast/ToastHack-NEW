package com.armorhud.events;


import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PlayerJumpListener
{
	void onPlayerJump(PlayerJumpEvent event);

	class PlayerJumpEvent extends CancellableEvent<PlayerJumpListener>
	{
		public static final int ID = 15;
		@Override
		public void call(PlayerJumpListener listener) {
			listener.onPlayerJump(this);
		}
	}
}
