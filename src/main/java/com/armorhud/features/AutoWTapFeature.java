package com.armorhud.features;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.DecimalSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.RandomUtil;
import com.armorhud.utils.TimerUtil;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class AutoWTapFeature extends Feature implements UpdateListener {

    public IntegerSetting chance = new IntegerSetting("Chance", 3, this);
    public IntegerSetting holdLenght = new IntegerSetting("Hold Length", 3, this);
    public DecimalSetting reaction = new DecimalSetting("Reaction",  0.9, this);

    public TimerUtil timer;
    public TimerUtil timer2;
    public boolean tap;
    public boolean idk;
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

    public AutoWTapFeature() {
        super("WTap",  "Movement");
        timer = new TimerUtil();
        timer2 = new TimerUtil();
    }


    public MinecraftClient getMc() {
        return MinecraftClient.getInstance();
    }

    @Override
    public void onUpdate() {
        if (GLFW.glfwGetKey(getMc().getWindow().getHandle(), 87) != 1) {
            idk = false;
            tap = false;
            return;
        }
        if (tap && timer.delay(reaction.getValue())) {
            getMc().options.forwardKey.setPressed(false);
            timer2.reset();
            idk = true;
            tap = false;
        }
        if (!idk || !timer2.delay(holdLenght.getValue())) {
            return;
        }
        getMc().options.forwardKey.setPressed(true);
        idk = false;
    }

  /*  public void onPacketSend(Object packet) {
        if (packet instanceof PlayerInteractEntityC2SPacket && ((IPlayerInteractEntityC2SPacket)packet).getType().getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK && getMc().options.forwardKey.isPressed()) {
            if (getMc().player.isSprinting() && RandomUtil.INSTANCE.getRandom().nextFloat(100.0f) <= chance.getValue()) {
                timer.reset();
                tap = true;
            }
        }
    }*/
}