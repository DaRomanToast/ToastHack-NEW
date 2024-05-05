package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class AutoWTapFeature extends Feature implements UpdateListener {
    private IntegerSetting duration;
    private long stopTimeUntil = 0;

    public AutoWTapFeature() {
        super("AutoWTapFeature", "Combat");
        duration = new IntegerSetting("Stop Duration", 3, this);
        addSetting(duration);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        if (System.currentTimeMillis() < stopTimeUntil) {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;
            if (player != null) {
                player.setVelocity(0, player.getVelocity().y, 0);
           //     mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch, false));
            }
        }
    }

    public void onPlayerHit() {
        stopTimeUntil = System.currentTimeMillis() + duration.getValue() * 1000;
    }
}
