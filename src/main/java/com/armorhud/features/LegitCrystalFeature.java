package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static com.armorhud.ClientInitializer.mc;


public class LegitCrystalFeature extends Feature implements UpdateListener{

	private final IntegerSetting placeInterval = new IntegerSetting("Place Interval", 0, this);
	private final IntegerSetting breakInterval = new IntegerSetting("Break Interval", 0, this);
	private final BooleanSetting activateOnRightClick = new BooleanSetting("Activate on Right Click",true,this);
	private final BooleanSetting stopOnKill = new BooleanSetting("Stop on Kill",true,this);
	private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS",  true, this);
	private int crystalPlaceClock = 0;
	private int crystalBreakClock = 0;

	public LegitCrystalFeature() {
		super("ToastCrystal", "Combat");
	}

	@Override
	public void onEnable() {
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
		super.onEnable();
		crystalPlaceClock = 0;
		crystalBreakClock = 0;
	}
	@Override
	public void onDisable()
	{
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
		super.onDisable();
	}

	@Override
	public void onUpdate() {
		IMouse mouse = (IMouse) Client.MC.mouse;
		if (crystalPlaceClock > 0) crystalPlaceClock--;
		if (crystalBreakClock > 0) crystalBreakClock--;
		if (this.activateOnRightClick.getValue() && GLFW.glfwGetMouseButton(Client.MC.getWindow().getHandle(), 1) != 1)
			return;

		ItemStack mainHandStack = mc.player.getMainHandStack();
		if (!mainHandStack.isOf(Items.END_CRYSTAL)) {
			return;
		}
		if (this.stopOnKill.getValue() && isDeadBodyNearby())
			return;


		if (mc.crosshairTarget instanceof EntityHitResult) {
			EntityHitResult hit = (EntityHitResult) mc.crosshairTarget;
			if (hit.getEntity() instanceof EndCrystalEntity crystal && crystalBreakClock <= 0) {
				crystalBreakClock = (int) breakInterval.getValue();
				mc.interactionManager.attackEntity(mc.player, crystal);
				mc.player.swingHand(Hand.MAIN_HAND);
				if (this.fakeCPS.getValue()) {
					mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 0, 1, 0);
					mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 0, 0, 0);
				}
			}
		} else if (mc.crosshairTarget instanceof BlockHitResult) {
			BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
			if (canPlaceCrystal(hit.getBlockPos()) && crystalPlaceClock <= 0) {
				crystalPlaceClock = (int) placeInterval.getValue();
				ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
				if (result.isAccepted() && result.shouldSwingHand()) {
					mc.player.swingHand(Hand.MAIN_HAND);
					if (this.fakeCPS.getValue()) {
						mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 1, 0);
						mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 0, 0, 0);
					}

				}
			}
		}
	}

	private boolean isDeadBodyNearby()
	{
		return mc.world.getPlayers().parallelStream()
				.filter(e -> mc.player != e)
				.filter(e -> e.squaredDistanceTo(mc.player) < 36)
				.anyMatch(LivingEntity::isDead);
	}


	private boolean canPlaceCrystal(BlockPos pos) {
		BlockState state = mc.world.getBlockState(pos);
		return state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK);
	}
}
