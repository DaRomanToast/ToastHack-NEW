package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprintFeature extends Feature implements UpdateListener {

	public AutoSprintFeature() {
		super("ChiefKeef", "Movement");
	}

	@Override
	protected void onEnable() {
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
	}

	@Override
	protected void onDisable() {
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = Client.MC.player;

		if (player.horizontalCollision || player.isSneaking())
			return;

		if (player.isInsideWaterOrBubbleColumn() || player.isSubmergedInWater())
			return;

		if (player.forwardSpeed > 0)
			player.setSprinting(true);
	}
}
