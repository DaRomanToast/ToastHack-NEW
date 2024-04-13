package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface PlayerMoveListener
{
	void onPlayerMove(PlayerMoveEvent event);

	class PlayerMoveEvent extends CancellableEvent<PlayerMoveListener>
	{
		public static final int ID = 16;
		private @Getter @Setter MovementType movementType;
		private @Getter @Setter Vec3d movement;

		public PlayerMoveEvent(MovementType movementType, Vec3d movement)
		{
			this.movementType = movementType;
			this.movement = movement;
		}

		@Override
		public void call(PlayerMoveListener listener) {
			listener.onPlayerMove(this);
		}
	}
}
