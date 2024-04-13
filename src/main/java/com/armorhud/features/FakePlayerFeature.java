package com.armorhud.features;

import com.armorhud.Client;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import com.armorhud.feature.Feature;

public class FakePlayerFeature extends Feature
{

	public FakePlayerFeature()
	{
		super("FakePlayer", "Render");
	}

	int id;


	@Override
	protected void onEnable()
	{
		OtherClientPlayerEntity player = new OtherClientPlayerEntity(Client.MC.world, new GameProfile(null, "DaRomanToast"));
		Vec3d pos = Client.MC.player.getPos();
		Client.MC.player.updateTrackedPosition(pos.x, pos.y, pos.z);
		player.updatePositionAndAngles(pos.x, pos.y, pos.z, Client.MC.player.getYaw(), Client.MC.player.getPitch());
		player.resetPosition();
		Client.MC.world.addPlayer(player.getId(), player);
		id = player.getId();
	}

	@Override
	protected void onDisable()
	{
		Client.MC.world.removeEntity(id, Entity.RemovalReason.DISCARDED);

	}
}