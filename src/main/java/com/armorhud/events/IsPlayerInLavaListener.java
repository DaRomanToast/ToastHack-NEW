package com.armorhud.events;


import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public interface IsPlayerInLavaListener
{
	void onIsPlayerInLava(IsPlayerInLavaEvent event);

	class IsPlayerInLavaEvent extends CancellableEvent<IsPlayerInLavaListener>
	{
		public static final int ID = 10;
		private @Getter @Setter boolean inLava;

		public IsPlayerInLavaEvent(boolean inLava)
		{
			this.inLava = inLava;
		}

		@Override
		public void call(IsPlayerInLavaListener listener) {
			listener.onIsPlayerInLava(this);
		}
	}
}
