package com.armorhud.utils;

import static com.armorhud.Client.TOASTHACK;

public enum NotificationUtils
{
	;
	public static void notify(String string)
	{
		if (!TOASTHACK.isGhostMode())
			TOASTHACK.getNotificationRenderer().sendNotification(string);
	}
}