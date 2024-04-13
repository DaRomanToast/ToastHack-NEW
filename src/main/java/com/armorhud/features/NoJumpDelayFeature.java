package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;

import static com.armorhud.ClientInitializer.mc;

public class NoJumpDelayFeature extends Feature implements UpdateListener {
    public NoJumpDelayFeature() {
        super("NoJumpDelay", "Movement");
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
        if (mc.player != null) {
            if (mc.player.isOnGround() && mc.options.jumpKey.isPressed()) {
                mc.player.jump();
            }
        }
    }
}