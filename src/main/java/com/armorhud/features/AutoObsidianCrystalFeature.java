package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

public class AutoObsidianCrystalFeature extends Feature implements UpdateListener {
    private boolean isActive = false;

    public AutoObsidianCrystalFeature() {
        super("AutoObsidianCrystal", "Combat");
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        isActive = false;
    }

    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (GLFW.glfwGetMouseButton(Client.MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) != GLFW.GLFW_PRESS) {
            isActive = false;
            return;
        }

        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            return;
        }

        if (mc.player.getMainHandStack().getItem() instanceof SwordItem && mc.crosshairTarget instanceof BlockHitResult) {
            BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
            if (hitResult.getType() == HitResult.Type.BLOCK && hitResult.getSide() == Direction.DOWN) {
                BlockPos pos = hitResult.getBlockPos().down();
                isActive = true;
                performActions(mc, pos);
            }
        } else {
            isActive = false;
        }
    }

    private void performActions(MinecraftClient mc, BlockPos pos) {
        if (!isActive) return;

        if (!placeBlock(mc, pos, Items.OBSIDIAN)) {
            return; // Failed to place obsidian, exit the method
        }

        if (!placeBlock(mc, pos.up(), Items.END_CRYSTAL)) {
            return; // Failed to place crystal, exit the method
        }

        breakCrystal(mc, pos.up());
    }

    private boolean switchTo(MinecraftClient mc, Item targetItem) {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == targetItem) {
                slot = i;
                break;
            }
        }
        if (slot != -1) {
            mc.player.getInventory().selectedSlot = slot;
            return true;
        }
        return false;
    }

    private boolean placeBlock(MinecraftClient mc, BlockPos pos, Item item) {
        if (!switchTo(mc, item)) {
            return false;
        }

        BlockHitResult hitResult = new BlockHitResult(mc.player.getPos(), Direction.DOWN, pos, false);
        var result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        return result.isAccepted();
    }

    private void breakCrystal(MinecraftClient mc, BlockPos pos) {
        Box area = new Box(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
        mc.world.getEntitiesByClass(EndCrystalEntity.class, area, e -> true)
                .forEach(crystal -> {
                    mc.interactionManager.attackEntity(mc.player, crystal);
                    mc.player.swingHand(Hand.MAIN_HAND);
                });
    }
}
