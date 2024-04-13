package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.utils.*;

import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.DecimalSetting;


import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.armorhud.utils.BlockUtils.isAnchorCharged;

public class AntiDoubleTapFeature extends Feature implements UpdateListener {

	private final BooleanSetting checkPlayersAround = new BooleanSetting("checkPlayersAround", true, this);
	private final DecimalSetting distance = new DecimalSetting("distance", 6, this);
	private final BooleanSetting predictCrystals = new BooleanSetting("predictCrystals",  true, this);
	private final BooleanSetting checkEnemiesAim = new BooleanSetting("checkEnemiesAim", true, this);
	private final BooleanSetting checkHoldingItems = new BooleanSetting("checkHoldingItems", true, this);
	private final DecimalSetting activatesAbove = new DecimalSetting("activatesAbove",  0.5, this);
	private final BooleanSetting stopOnUsingItem = new BooleanSetting("stopOnUsingItem",  false, this);
	private final BooleanSetting drainFagSetting = new BooleanSetting("drainFagSetting", false, this);


	public AntiDoubleTapFeature() {
		super("AutoDoubleHand", "Combat");
	}

	@Override
	public void onEnable() {
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
	}

	@Override
	public void onDisable() {
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
	}

	private List<EndCrystalEntity> getNearByCrystals() {
		Vec3d pos = Client.MC.player.getPos();
		return Client.MC.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6, -6, -6), pos.add(6, 6, 6)), a -> true);
	}

	private boolean isRespawnAnchorNearby() {
		BlockPos playerPos = Client.MC.player.getBlockPos();
		int range = (int) Math.round(distance.getValue());
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {
					BlockPos blockPos = playerPos.add(x, y, z);
					if (isAnchorCharged(blockPos)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	@Override
	public void onUpdate() {
		double distanceSq = distance.getValue() * distance.getValue();
		if (checkPlayersAround.getValue() && Client.MC.world.getPlayers().parallelStream()
				.filter(e -> e != Client.MC.player)
				.noneMatch(player -> Client.MC.player.squaredDistanceTo(player) <= distanceSq))
			return;
		double activatesAboveV = activatesAbove.getValue();
		int f = (int) Math.floor(activatesAboveV);
		for (int i = 1; i <= f; i++)
			if (BlockUtils.hasBlock(Client.MC.player.getBlockPos().add(0, -i, 0)))
				return;
		if (BlockUtils.hasBlock(BlockPos.ofFloored(Client.MC.player.getPos().add(0, -activatesAboveV, 0))))
			return;
		if (stopOnUsingItem.getValue() && Client.MC.player.isUsingItem())
			return;
		if (drainFagSetting.getValue() && Client.MC.player.isBlocking()) {
			return;
		}
		if (isRespawnAnchorNearby()) {
		}
		if (Client.MC.currentScreen != null)
			return;
		List<EndCrystalEntity> crystals = getNearByCrystals();
		ArrayList<Vec3d> crystalPos = new ArrayList<>();
		crystals.forEach(e -> crystalPos.add(e.getPos()));

		if (predictCrystals.getValue()) {
			Stream<BlockPos> stream =
					BlockUtils.getAllInBoxStream(Client.MC.player.getBlockPos().add(-6, -8, -6), Client.MC.player.getBlockPos().add(6, 2, 6))
							.filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e))
							.filter(CrystalUtils::canPlaceCrystalClient);
			if (checkEnemiesAim.getValue().equals(true)) {
				if (checkHoldingItems.getValue().equals(true))
					stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
				else
					stream = stream.filter(this::arePeopleAimingAtBlock);
			}
			stream.forEachOrdered(e -> crystalPos.add(Vec3d.ofBottomCenter(e).add(0, 1, 0)));
		}
		for (Vec3d pos : crystalPos) {
			double damage =
					DamageUtils.crystalDamage(Client.MC.player, pos, true, null, false);
			if (damage >= Client.MC.player.getHealth() + Client.MC.player.getAbsorptionAmount()) {
				InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				break;
			}
		}
	}

	private boolean arePeopleAimingAtBlock(BlockPos block) {
		return Client.MC.world.getPlayers().parallelStream()
				.filter(e -> e != Client.MC.player)
				.anyMatch(e -> {
					Vec3d eyesPos = RotationUtils.getEyesPos(e);
					BlockHitResult hitResult = Client.MC.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
					return hitResult != null && hitResult.getBlockPos().equals(block);
				});
	}

	private boolean arePeopleAimingAtBlockAndHoldingCrystals(BlockPos block) {
		return Client.MC.world.getPlayers().parallelStream()
				.filter(e -> e != Client.MC.player)
				.filter(e -> e.isHolding(Items.END_CRYSTAL))
				.filter(e -> e.isHolding(Items.RESPAWN_ANCHOR))
				.anyMatch(e -> {
					Vec3d eyesPos = RotationUtils.getEyesPos(e);
					BlockHitResult hitResult = Client.MC.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
					return hitResult != null && hitResult.getBlockPos().equals(block);
				});
	}
}