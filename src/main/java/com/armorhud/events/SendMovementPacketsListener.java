package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface SendMovementPacketsListener
{
	void onSendMovementPackets(SendMovementPacketsEvent event);

	class SendMovementPacketsEvent extends CancellableEvent<SendMovementPacketsListener>
	{
		public static final int ID = 26;
		@Override
		public void call(SendMovementPacketsListener listener) {
			listener.onSendMovementPackets(this);
		}
	}
}
