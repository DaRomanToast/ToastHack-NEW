package com.armorhud.utils;

import com.armorhud.Client;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

import static com.armorhud.Client.*;

public enum CrystalUtils
{
	;

	public static boolean canPlaceCrystalClient(BlockPos block)
	{
		BlockState blockState = MC.world.getBlockState(block);
		if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK))
			return false;
		BlockPos blockPos2 = block.up();
		if (!MC.world.isAir(blockPos2))
			return false;
		double d = blockPos2.getX();
		double e = blockPos2.getY();
		double f = blockPos2.getZ();
		List<Entity> list = MC.world.getOtherEntities((Entity)null, new Box(d, e, f, d + 1.0D, e + 2.0D, f + 1.0D));
		return list.isEmpty();
	}

	public static boolean canPlaceCrystalClientAssumeObsidian(BlockPos block, Box bb) {
		double f;
		double e;
		BlockPos blockPos2 = block.up();
		if (!Client.MC.world.isAir(blockPos2)) {
			return false;
		}
		double d = blockPos2.getX();
		Box crystalBox = new Box(d, e = (double)blockPos2.getY(), f = (double)blockPos2.getZ(), d + 1.0, e + 2.0, f + 1.0);
		if (crystalBox.intersects(bb)) {
			return false;
		}
		List list = Client.MC.world.getOtherEntities((Entity)null, crystalBox);
		return list.isEmpty();
	}
}
