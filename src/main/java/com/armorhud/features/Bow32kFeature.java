package com.armorhud.features;


import com.armorhud.events.SendMovementPacketsListener;
import com.armorhud.events.StopUsingItemListener;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static com.armorhud.Client.MC;


public class Bow32kFeature extends Feature implements StopUsingItemListener, SendMovementPacketsListener
{

	private final IntegerSetting power = new IntegerSetting("power", 50,this);

	public Bow32kFeature()
	{
		super("Bow32k", "Combat");
	}

	@Override
	public void onEnable()
	{
		DietrichEvents2.global().subscribe(StopUsingItemListener.StopUsingItemEvent.ID, this);
		DietrichEvents2.global().subscribe(SendMovementPacketsListener.SendMovementPacketsEvent.ID, this);
		super.onEnable();
	}


	@Override
	protected void onDisable()
	{
		DietrichEvents2.global().unsubscribe(StopUsingItemListener.StopUsingItemEvent.ID, this);
		DietrichEvents2.global().unsubscribe(SendMovementPacketsListener.SendMovementPacketsEvent.ID, this);
		super.onDisable();
	}

	@Override
	public void onStopUsingItem(StopUsingItemEvent event)
	{
		if (!MC.player.getActiveItem().isOf(Items.BOW))
			return;
		int powerI = power.getValue();
		for (int i = 0; i < powerI; i++)
		{
			MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(MC.player.getX(), MC.player.getY(), MC.player.getZ(), true));
			MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(MC.player.getX(), MC.player.getY() + 0.01, MC.player.getZ(), false));
		}
	}

	@Override
	public void onSendMovementPackets(SendMovementPacketsEvent event)
	{
		if (!MC.player.getActiveItem().isOf(Items.BOW))
			return;
		MC.player.setSprinting(true);
	}
}
