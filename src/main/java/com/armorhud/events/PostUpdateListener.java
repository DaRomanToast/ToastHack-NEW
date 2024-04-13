package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PostUpdateListener
{
	void onPostUpdate(final PostUpdateEvent event);

	class PostUpdateEvent extends CancellableEvent<PostUpdateListener>
	{
		public static final int ID = 20;
		@Override
		public void call(PostUpdateListener listener) {
			listener.onPostUpdate(this);
		}
	}
}
