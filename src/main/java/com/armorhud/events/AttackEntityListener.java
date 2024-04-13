package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;

public interface AttackEntityListener
{
	void onAttackEntity(AttackEntityEvent event);

	class AttackEntityEvent extends CancellableEvent<AttackEntityListener>
	{
		public static final int ID = 0;
		private @Getter @Setter PlayerEntity player;
		private @Getter @Setter Entity target;

		public AttackEntityEvent(PlayerEntity player, Entity target)
		{
			// player should always be a ClientPlayerEntity
			this.player = player;
			this.target = target;
		}

		@Override
		public void call(AttackEntityListener listener) {
			listener.onAttackEntity(this);
		}
	}
}
