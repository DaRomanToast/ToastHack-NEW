package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PostActionListener
{
	void onPostAction(final PostActionEvent event);

	class PostActionEvent extends CancellableEvent<PostActionListener>
	{
		public static final int ID = 18;
		@Override
		public void call(PostActionListener listener) {
			listener.onPostAction(this);
		}
	}
}
