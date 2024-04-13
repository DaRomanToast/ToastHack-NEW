package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface PostMotionListener
{
	void onPostMotion(final PostMotionEvent event);

	class PostMotionEvent extends CancellableEvent<PostMotionListener>
	{
		public static final int ID = 19;
		@Override
		public void call(PostMotionListener listener) {
			listener.onPostMotion(this);
		}
	}
}
