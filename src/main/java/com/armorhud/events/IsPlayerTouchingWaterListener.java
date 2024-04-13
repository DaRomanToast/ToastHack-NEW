package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public interface IsPlayerTouchingWaterListener
{
	void onIsPlayerTouchingWater(IsPlayerTouchingWaterEvent event);

	class IsPlayerTouchingWaterEvent extends CancellableEvent<IsPlayerTouchingWaterListener>
	{
		public static final int ID = 11;
		private @Getter @Setter boolean touchingWater;

		public IsPlayerTouchingWaterEvent(boolean touchingWater)
		{
			this.touchingWater = touchingWater;
		}



		@Override
		public void call(IsPlayerTouchingWaterListener listener) {
			listener.onIsPlayerTouchingWater(this);
		}
	}
}
