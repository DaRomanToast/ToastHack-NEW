package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.MathUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;

import static com.armorhud.Client.MC;
import static com.armorhud.ClientInitializer.mc;

public class AutoJumpResetFeature extends Feature implements UpdateListener {

    private final IntegerSetting chance = new IntegerSetting("chance", 100, this);

    public AutoJumpResetFeature() {
        super("AutoJumpReset", "Movement");
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
        if (mc.player == null) {
            return;
        }
        if (MC.currentScreen != null) {
            return;
        }
        if (mc.player.isBlocking()) {
            return;
        }
        if (mc.player.isUsingItem()) {
            return;
        }
        if (mc.currentScreen instanceof HandledScreen) {
            return;
        }
        if (!mc.player.isOnGround()) {
            return;
        }
        if (mc.player.maxHurtTime == 0) {
            return;
        }
        if (mc.player.hurtTime == 0) {
            return;
        }
        if (!(mc.player.getAttacker() instanceof PlayerEntity)) {
            return;
        }
        if (mc.player.isInsideWaterOrBubbleColumn()) {
            return;
        }
        if (mc.player.isInsideWall()) {
            return;
        }
        if (mc.player.isTouchingWater()) {
            return;
        }
        if (mc.player.hurtTime == mc.player.maxHurtTime - 1 && MathUtils.getRandomInt(0, 100) <= chance.getValue()) {
            mc.player.jump();
        }

        //once you get hit it jumps
    }
}
