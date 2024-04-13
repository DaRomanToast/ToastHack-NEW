package com.armorhud.mixin;

import com.armorhud.events.GetOutlineShapeListener;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin
{
	@Inject(at = {@At("HEAD")}, method = {"getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"}, cancellable = true)
	private void onGetOutlineShape(BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
		GetOutlineShapeListener.GetOutlineShapeEvent xrayEvent = new GetOutlineShapeListener.GetOutlineShapeEvent(view, pos, context);
		//@formatter:off
		DietrichEvents2.global().postInternal(GetOutlineShapeListener.GetOutlineShapeEvent.ID, xrayEvent);
		//@formatter:on
		if (xrayEvent.getReturnValue() != null)
			cir.setReturnValue(xrayEvent.getReturnValue());
	}
}