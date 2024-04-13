package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.BlockUtils;
import com.armorhud.utils.InventoryUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static com.armorhud.Client.MC;
import static com.armorhud.features.AutoFishFeature.mc;


public class LegitAnchorFeature extends Feature implements UpdateListener {
    public BooleanSetting stopOnShift = new BooleanSetting("stopOnShift", false, this);
    public IntegerSetting backupSlot = new IntegerSetting("backupSlot",  1, this);
    private BlockPos lastLitAnchor;

    public LegitAnchorFeature() {
        super("LegitAnchor", "Combat");
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
        super.onEnable();
    }
    @Override
    public void onDisable()
    {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        super.onDisable();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.currentScreen != null || mc.player.isUsingItem())
            return;

        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS)
            return;

        if (mc.player.isSneaking() && stopOnShift.isEnabled())
            return;

        if (!(mc.crosshairTarget instanceof BlockHitResult hit))
            return;

        BlockPos pos = hit.getBlockPos();
        if (!BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, pos))
            return;

        if (BlockUtils.isAnchorCharged(pos) && !pos.equals(lastLitAnchor)) {
            // Anchor is charged and not the last interacted one, prepare for explosion
            boolean hasTotem = InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
            if (!hasTotem) {
                // If no Totem, use the item in the backup slot
                mc.player.getInventory().selectedSlot = backupSlot.getValue() - 1;
            }
            lastLitAnchor = pos; // Mark this anchor to avoid repetitive actions
        } else if (!BlockUtils.isAnchorCharged(pos) && !pos.equals(lastLitAnchor)) {
            // Anchor is not charged, attempt to light it with Glowstone
            boolean hasGlowstone = InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
            if (hasGlowstone) {
                ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.shouldSwingHand()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    // Update lastLitAnchor only if action to light is successful
                    lastLitAnchor = pos;
                }
            }
        }
    }
}