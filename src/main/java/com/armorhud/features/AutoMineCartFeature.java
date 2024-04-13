package com.armorhud.features;

import com.armorhud.events.KeyPressListener;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.keybind.Keybind;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.setting.KeybindSetting;
import com.armorhud.utils.BlockUtils;
import com.armorhud.utils.InventoryUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static com.armorhud.Client.MC;

public class AutoMineCartFeature extends Feature implements UpdateListener, KeyPressListener {

    public AutoMineCartFeature() {
        super("AutoMineCart", "Combat");
        this.activateKey = new KeybindSetting("activateKey", this, new Keybind("activateKey", GLFW.GLFW_KEY_C, true, false, ""));
    }
    private final IntegerSetting bowCharge = new IntegerSetting("bowCharge", 5, this);
    private final IntegerSetting shootInterval = new IntegerSetting("shootInterval", 0, this);
    private final IntegerSetting placeMinecartInterval = new IntegerSetting("placeMinecartInterval", 0, this);
    private KeybindSetting activateKey;

    private int minecartPlaceClock;
    private int shootDelay;
    private int cnt;

    private boolean isThatRails;
    private boolean needToPlaceRails;
    private boolean shooted;
    private boolean needToShoot;
    private boolean needToPlaceMinecart;

    @Override
    public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
        DietrichEvents2.global().subscribe(KeyPressListener.KeyPressEvent.ID, this);

        minecartPlaceClock = 0;
        shootDelay = 0;
        cnt = 0;

        isThatRails = false;
        needToPlaceMinecart = false;
        needToPlaceRails = false;
        needToShoot = false;
        shooted = false;
    }

    private boolean check() {
        return isThatRails || needToPlaceMinecart || needToPlaceRails || needToShoot || shooted;
    }

    private boolean checkHotBar() {
        return InventoryUtils.hasItemInHotbar(Items.TNT_MINECART) && InventoryUtils.hasItemInHotbar(Items.RAIL)
                && InventoryUtils.hasItemInHotbar(Items.BOW);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        DietrichEvents2.global().unsubscribe(KeyPressListener.KeyPressEvent.ID, this);
    }

    @Override
    public void onUpdate() {

        if (GLFW.glfwGetKey(MC.getWindow().getHandle(), activateKey.getValue().getKey()) != GLFW.GLFW_PRESS) {
            isThatRails = false;
            needToPlaceMinecart = false;
            needToPlaceRails = false;
            needToShoot = false;
            shooted = false;
            cnt = 0;
            return;
        }

        if (cnt == 0) {
            if (needToPlaceRails) {
                if (MC.player.getMainHandStack().getItem() == Items.RAIL) {
                    if (MC.crosshairTarget instanceof BlockHitResult hit
                            && !BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos())) {
                        InventoryUtils.selectItemFromHotbar(Items.RAIL);
                        ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hit);
                        if (result.isAccepted() && result.shouldSwingHand()) MC.player.swingHand(Hand.MAIN_HAND);
                    }
                } else {
                    InventoryUtils.selectItemFromHotbar(Items.RAIL);
                }

                needToPlaceMinecart = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                isThatRails = true;
            }


            if (isThatRails && shootDelay != shootInterval.getValue()) {
                shootDelay++;
                return;
            } else if (isThatRails) {
                shootDelay = 0;
                isThatRails = false;
                needToPlaceMinecart = false;
                needToPlaceRails = false;
                shooted = false;
                needToShoot = true;
            }

            if (needToShoot) {

                InventoryUtils.selectItemFromHotbar(Items.BOW);

                if (MC.player.getItemUseTime() >= bowCharge.getValue()) {
                    MC.player.stopUsingItem();
                    MC.interactionManager.stopUsingItem(MC.player);
                    MC.options.useKey.setPressed(false);

                    isThatRails = false;
                    needToPlaceMinecart = false;
                    needToPlaceRails = false;
                    needToShoot = false;
                    shooted = true;
                } else {
                    MC.options.useKey.setPressed(true);
                }

            }

            if (shooted && minecartPlaceClock != placeMinecartInterval.getValue()) {
                minecartPlaceClock++;
                return;
            } else if (shooted) {
                minecartPlaceClock = 0;
                isThatRails = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                needToPlaceMinecart = true;
            }

            if (needToPlaceMinecart) {

                if (MC.crosshairTarget instanceof BlockHitResult hit
                        && BlockUtils.isBlock(Blocks.RAIL, hit.getBlockPos())) {
                    InventoryUtils.selectItemFromHotbar(Items.TNT_MINECART);
                    ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hit);
                    if (result.isAccepted() && result.shouldSwingHand())
                        MC.player.swingHand(Hand.MAIN_HAND);

                }

                isThatRails = false;
                needToPlaceMinecart = false;
                needToPlaceRails = false;
                needToShoot = false;
                shooted = false;
                cnt++;
            }
        }

    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKeyCode() == activateKey.getValue().getKey() && !check() && checkHotBar()) {
            if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
                if (BlockUtils.isBlock(Blocks.RAIL, hit.getBlockPos())) {
                    isThatRails = true;
                } else if (!BlockUtils.isBlock(Blocks.AIR, hit.getBlockPos())) {
                    needToPlaceRails = true;
                }
            }
        }
    }
}
