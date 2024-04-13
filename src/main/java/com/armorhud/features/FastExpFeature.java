package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;
import com.armorhud.setting.IntegerSetting;


public class FastExpFeature extends Feature implements UpdateListener {
    private final IntegerSetting clickAmount = new IntegerSetting("clickAmount", 1, this);
    private final BooleanSetting activateOnRightClick = new BooleanSetting("activateOnRightClick", true, this);
    private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS",  true, this);

    private int clickCounter;

    public FastExpFeature() {
        super("FastExp", "Misc");
    }

    @Override
    protected void onEnable() {
        clickCounter = 0;
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        IMouse mouse = (IMouse) Client.MC.mouse;
        if (Client.MC.currentScreen != null) {
            clickCounter = 0;
            return;
        }

        if (this.activateOnRightClick.getValue().booleanValue() && GLFW.glfwGetMouseButton(Client.MC.getWindow().getHandle(), 1) != 1) {
            clickCounter = 0;
            return;
        }

        if (Client.MC.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE) {
            clickCounter = 0;
            return;
        }

        if (clickCounter < clickAmount.getValue()) {
            if (this.fakeCPS.getValue()) {
                mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 1, 0);
                mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 0, 0);
            }
            clickCounter++;
        } else {
            clickCounter = 0;
        }
    }
}
