package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class AutoFishFeature extends Feature implements UpdateListener {

    boolean retract = false;
    boolean extract = false;
    int timer;
    int timer2;
    int lock;
    static MinecraftClient mc = MinecraftClient.getInstance();

    public AutoFishFeature() {
        super("AutoFish", "Misc");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        if (!FishingBobberExist()) {
            timer = 0;
        }
        if (FishingBobberExist() && mc.player.fishHook.isTouchingWater()) {
            if (timer == 0) {
                timer = 1;
            } else if (timer == 25) {
                if (mc.player.fishHook.getVelocity().getY() < -0.1D) {
                    retract = true;
                }
            } else if (timer < 25) {
                timer++;
            }
        }
        if (retract || extract) {
            if (timer2 == 5 && retract) {
                if (lock == 0) {
                    UseRod();
                    if (FishingBobberExist()) {
                        mc.player.fishHook.remove(Entity.RemovalReason.DISCARDED);
                    }
                    timer2 = 0;
                    retract = false;
                    extract = true;
                    lock = 1;
                }
            }
            if (timer2 == 25 && extract) {
                if (isrod()) {
                    UseRod();
                }
                timer2 = 0;
                extract = false;
                lock = 0;
            }
            timer2++;
        }
    }

    public boolean PlayerExist() {
        if (mc.player != null) {
            return true;
        }
        return false;
    }

    public boolean isrod() {
        if (PlayerExist() && mc.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            return true;
        }
        return false;
    }

    public boolean FishingBobberExist() {
        if (PlayerExist() && mc.player.fishHook != null) {
            return true;
        }
        return false;
    }

    public static Hand getHand() {
        if (mc.player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            return Hand.MAIN_HAND;
        } else {
            return Hand.OFF_HAND;
        }
    }

    public static ActionResult UseRod() {
        mc.player.swingHand(getHand());
        return mc.interactionManager.interactItem(mc.player, getHand());
    }
}
