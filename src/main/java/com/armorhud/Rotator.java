package com.armorhud;

import com.armorhud.events.UpdateListener;
import com.armorhud.utils.RotationUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.armorhud.Client.MC;

public class Rotator implements UpdateListener
{

	public Rotator()
	{
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
	}

	private final ArrayList<Rotation> rotations = new ArrayList<>();
	private Runnable callback;

	@Override
	public void onUpdate()
	{

		if (rotations.size() != 0)
		{
			RotationUtils.setRotation(rotations.get(rotations.size() - 1));
			rotations.remove(rotations.size() - 1);
			if (rotations.size() == 0)
				callback.run();
		}
	}


}