package com.armorhud.features;


import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.BlockUtils;
import com.armorhud.utils.InventoryUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

import static com.armorhud.Client.MC;
import static com.armorhud.ClientInitializer.mc;


public class FastAnchorFeature extends Feature implements UpdateListener {
	public BooleanSetting stopOnShift = new BooleanSetting("stopOnShift", false, this);
	public IntegerSetting cooldown = new IntegerSetting("cooldown", 0, this);
	public IntegerSetting backupSlot = new IntegerSetting("backupSlot",  1, this);
	private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS",  true, this);
	private int clock;
	private boolean hasAnchored;
	public FastAnchorFeature() {
		super("FastAnchor",  "Combat");
	}

	@Override
	protected void onEnable() {
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
		clock = 0;
		this.hasAnchored = false;
	}

	@Override
	protected void onDisable() {
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
	}

	@Override
	public void onUpdate() {
		IMouse mouse = (IMouse) mc.mouse;
		PlayerInventory inv = MC.player.getInventory();
		if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), 1) != 1) {
			return;
		}
		if (MC.player.isUsingItem()) {
			return;
		}
		if (this.hasAnchored) {
			if (this.clock != 0) {
				--this.clock;
				return;
			}
			this.clock = this.cooldown.getValue();
			this.hasAnchored = false;
		}

		if (MC.player.isSneaking() && stopOnShift.getValue())
			return;

		if (MC.currentScreen != null)
			return;

		if (MC.player.isUsingItem())
			return;

		boolean dontExplode = clock != 0;

		if (dontExplode)
			clock--;

		if (MC.crosshairTarget instanceof BlockHitResult hit) {
			BlockPos pos = hit.getBlockPos();


			if (dontExplode)
				return;
			clock = cooldown.getValue();
			if (BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, hit.getBlockPos())) {
				if (!BlockUtils.isAnchorCharged(pos)) {
					InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
					ActionResult actionResult = MC.interactionManager.interactBlock(MC.player,  Hand.MAIN_HAND, hit);
					if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
						MC.player.swingHand(Hand.MAIN_HAND);
						if (this.fakeCPS.getValue()) {
							mouse.cwOnMouseButton(mc.getWindow().getHandle(), 1, 0, 0);
						}
					}
				} else {
					if (InventoryUtils.hasItemInHotbar(Items.TOTEM_OF_UNDYING)) {
						InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
						ActionResult actionResult = MC.interactionManager.interactBlock(MC.player,  Hand.MAIN_HAND, hit);
						if (actionResult.isAccepted() && actionResult.shouldSwingHand())
							MC.player.swingHand(Hand.MAIN_HAND);
						if (this.fakeCPS.getValue()) {
							mouse.cwOnMouseButton(mc.getWindow().getHandle(), 1, 1, 0);
						}
					} else {
						inv.selectedSlot = backupSlot.getValue() - 1;
						ActionResult actionResult = MC.interactionManager.interactBlock(MC.player,  Hand.MAIN_HAND, hit);
						if (actionResult.isAccepted() && actionResult.shouldSwingHand())
							MC.player.swingHand(Hand.MAIN_HAND);
						if (this.fakeCPS.getValue()) {
							mouse.cwOnMouseButton(mc.getWindow().getHandle(), 1, 1, 0);
							mouse.cwOnMouseButton(mc.getWindow().getHandle(), 0, 0, 0);
						}
						this.hasAnchored = true;
					}
				}
			}
		}
	}
}
