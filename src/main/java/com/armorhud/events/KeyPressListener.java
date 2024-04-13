package com.armorhud.events;


import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

public interface KeyPressListener
{
	void onKeyPress(KeyPressEvent event);

	class KeyPressEvent extends CancellableEvent<KeyPressListener>
	{
		public static final int ID = 13;
		private @Getter @Setter int keyCode, scanCode, action, modifiers;

		public KeyPressEvent(int keyCode, int scanCode, int action, int modifiers)
		{
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.action = action;
			this.modifiers = modifiers;
		}

		@Override
		public void call(KeyPressListener listener) {
			listener.onKeyPress(this);
		}
	}
}
