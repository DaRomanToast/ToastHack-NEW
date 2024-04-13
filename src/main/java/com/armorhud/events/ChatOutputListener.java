package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public interface ChatOutputListener
{
	void onSendMessage(ChatOutputEvent event);

	class ChatOutputEvent extends CancellableEvent<ChatOutputListener>
	{
		public static final int ID = 1;
		private @Getter @Setter String originalMessage;
		private @Getter @Setter String message;

		public ChatOutputEvent(String message)
		{
			originalMessage = this.message = message;
		}

		public boolean isModified()
		{
			return !originalMessage.equals(message);
		}

		@Override
		public void call(ChatOutputListener listener) {
			listener.onSendMessage(this);
		}
	}
}
