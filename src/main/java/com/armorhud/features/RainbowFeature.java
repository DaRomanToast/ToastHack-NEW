package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;

public class RainbowFeature extends Feature implements UpdateListener {

    public RainbowFeature() {
        super("Rainbow", "Render");
    }

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

    @Override
    public void onUpdate() {
        //Color customcolor = new Color(r,g,b);
    }
}
