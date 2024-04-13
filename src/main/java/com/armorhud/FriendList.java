package com.armorhud;

import com.armorhud.events.UpdateListener;
import com.armorhud.utils.ChatUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.HashSet;
import java.util.UUID;

import static com.armorhud.Client.TOASTHACK;
import static com.armorhud.Client.MC;

public class FriendList implements UpdateListener
{
	private final HashSet<UUID> friends = new HashSet<>();

	private boolean processed = false;

	public FriendList()
	{
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
	}

	public boolean isFriend(PlayerEntity player)
	{
		return friends.contains(player.getUuid());
	}

	private void addFriend(PlayerEntity player)
	{
		friends.add(player.getUuid());
		ChatUtils.info("Added " + player.getEntityName() + " as friend. UUID: " + player.getUuid());
	}

	private void removeFriend(PlayerEntity player)
	{
		friends.remove(player.getUuid());
		ChatUtils.info("Removed " + player.getEntityName() + " as friend. UUID: " + player.getUuid());
	}

	public HashSet<UUID> getFriends(){
		return new HashSet<>(friends);
	}

	@Override
	public void onUpdate()
	{
		if (MC.options.pickItemKey.isPressed())
		{
			if (!processed)
			{
				if (MC.crosshairTarget instanceof EntityHitResult hit)
				{
					if (hit.getEntity() instanceof PlayerEntity player)
					{
						if (isFriend(player))
							removeFriend(player);
						else
							addFriend(player);
					}
				}
				processed = true;
			}
		}
		else
			processed = false;
	}
}