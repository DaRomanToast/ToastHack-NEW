package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.MoveHelper2;
import de.florianmichael.dietrichevents2.DietrichEvents2;

import static com.armorhud.ClientInitializer.mc;

public class VulcanFlyFeature extends Feature implements UpdateListener {

    double startHeight;



    private final IntegerSetting clip = new IntegerSetting("clip",  10, this);
    private final BooleanSetting ShouldClip = new BooleanSetting("ShouldClip",  false, this);

    public VulcanFlyFeature() {
        super("VulcanFly", "Movement" );
    }
    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateEvent.ID, this);
        startHeight = mc.player.getY();
        if (ShouldClip.getValue()) {
            mc.player.updatePosition(mc.player.getX(), mc.player.getY() + clip.getValue(), mc.player.getZ());
        }
    }
    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateEvent.ID, this);

    }


    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        double clipHeight = startHeight - clip.getValue();
        if (mc.player.fallDistance > 2) {
            mc.player.setOnGround(true);
            mc.player.fallDistance = 0f;
        }
        if (mc.player.age % 3 == 0) {
            MoveHelper2.motionYPlus(0.026);
        } else {
            MoveHelper2.motionY(-0.0991);
        }
    }


}
