package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.ArrayList;

public interface GetOutlineShapeListener
{

	void onGetOutlineShape(GetOutlineShapeEvent event);

	class GetOutlineShapeEvent extends CancellableEvent<GetOutlineShapeListener>
	{
		public static final int ID = 8;
		private @Getter @Setter BlockView view;
		private @Getter @Setter BlockPos pos;
		private @Getter @Setter ShapeContext context;
		private @Getter @Setter VoxelShape returnValue;

		public GetOutlineShapeEvent(BlockView view, BlockPos pos, ShapeContext context)
		{
			this.view = view;
			this.pos = pos;
			this.context = context;
		}

		@Override
		public void call(GetOutlineShapeListener listener) {
			listener.onGetOutlineShape(this);
		}
	}

}
