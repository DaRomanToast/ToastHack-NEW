package com.armorhud;

import com.armorhud.events.PostMotionListener;
import com.armorhud.events.PreMotionListener;
import com.armorhud.events.UpdateListener;
import com.armorhud.utils.RotationUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import static com.armorhud.Client.TOASTHACK;
import static com.armorhud.Client.MC;

public class RotationFaker implements PreMotionListener, PostMotionListener, UpdateListener
{

	private float realYaw;
	private float realPitch;
	private float serverYaw;
	private float serverPitch;
	private float lastYaw;
	private float lastPitch;
	private boolean faking = false;
	private boolean wasFaking = false;

	public RotationFaker()
	{
		DietrichEvents2.global().subscribe(PreMotionEvent.ID, this, Integer.MAX_VALUE);
		DietrichEvents2.global().subscribe(PostMotionEvent.ID, this, Integer.MAX_VALUE);
		DietrichEvents2.global().subscribe(UpdateEvent.ID, this, Integer.MAX_VALUE);
	}

	public boolean isFaking()
	{
		return faking;
	}

	public boolean wasFakingLastTick()
	{
		return wasFaking;
	}

	@Override
	public void onPreMotion(PreMotionEvent event)
	{
		if (!faking)
			return;
		ClientPlayerEntity player = MC.player;
		realYaw = player.getYaw();
		realPitch = player.getPitch();
		player.setYaw(serverYaw);
		player.setPitch(serverPitch);
	}

	@Override
	public void onPostMotion(PostMotionEvent event)
	{
		if (!faking)
			return;
		ClientPlayerEntity player = MC.player;
		player.setYaw(realYaw);
		player.setPitch(realPitch);
		faking = false;
	}

	@Override
	public void onUpdate()
	{
		if (faking != wasFaking)
			wasFaking = faking;
		lastYaw = serverYaw;
		lastPitch = serverPitch;
	}

	public void setServerLookPos(Vec3d pos)
	{
		Rotation neededRotation = RotationUtils.getNeededRotations(pos);
		setServerLookAngle(neededRotation.getYaw(), neededRotation.getPitch());
	}

	public void setServerLookAngle(float yaw, float pitch)
	{
		serverYaw = yaw;
		serverPitch = pitch;
		faking = true;
		wasFaking = true;
	}

	public void setClientLookPos(Vec3d pos)
	{
		Rotation neededRotation = RotationUtils.getNeededRotations(pos);
		setClientLookAngle(neededRotation.getYaw(), neededRotation.getPitch());
	}

	public void setClientLookAngle(float yaw, float pitch)
	{
		MC.player.setYaw(yaw);
		MC.player.setPitch(pitch);
	}

	public float getServerYaw()
	{
		if (faking)
			return serverYaw;
		return MC.player.getYaw();
	}

	public float getServerPitch()
	{
		if (faking)
			return serverPitch;
		return MC.player.getPitch();
	}

	public float getFakedYaw()
	{
		return serverYaw;
	}

	public float getFakedPitch()
	{
		return serverPitch;
	}

	public float getLastFakedYaw()
	{
		return lastYaw;
	}

	public float getLastFakedPitch()
	{
		return lastPitch;
	}
}
