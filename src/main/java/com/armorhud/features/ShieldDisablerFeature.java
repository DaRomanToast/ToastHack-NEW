package com.armorhud.features;

import com.armorhud.FriendList;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.DecimalSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

import static com.armorhud.Client.MC;
import static com.armorhud.ClientInitializer.mc;

public class ShieldDisablerFeature extends Feature implements UpdateListener {
    public DecimalSetting cooldown = new DecimalSetting ("Cooldown", 0.3, this);
    private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS", true, this);
    private boolean switchToSword = false;
    private final FriendList friendList;
    @Override
    public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    public ShieldDisablerFeature(FriendList friendList) {
        super("AntiShieldFag", "Combat");
        this.friendList = friendList;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.targetedEntity == null || MC.currentScreen != null) {
            return;
        }
        if (!mc.player.isUsingItem() && targetUsingShield()) {
            if (MC.player.getAttackCooldownProgress(0) >= cooldown.getValue()) {
                int axeSlot = findAxeHotbarSlot(mc.player);
                if (axeSlot != -1) {
                    mc.player.getInventory().selectedSlot = axeSlot;
                    mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    if (this.fakeCPS.getValue()) {
                        IMouse mouse = (IMouse) MC.mouse;
                        mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 1, 0);
                        mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 0, 0);
                    }
                    switchToSword = true;
                }
            }
        } else if (switchToSword) {
            int swordSlot = findSwordHotbarSlot(mc.player);
            if (swordSlot != -1) {
                mc.player.getInventory().selectedSlot = swordSlot;
                switchToSword = false;
            }
        }
    }


    private int findAxeHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    private int findSwordHotbarSlot(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem) {
                return i;
            }
        }
        return -1;
    }

    public boolean targetUsingShield() {
        Entity target = mc.targetedEntity;
        if (target instanceof PlayerEntity && friendList.isFriend((PlayerEntity) target)) {
            return false;
        }
        if (target instanceof PlayerEntity && ((PlayerEntity) target).isUsingItem() && ((PlayerEntity) target).getActiveItem().getItem() == Items.SHIELD) {
            return true;
        }
        return false;
    }
}
