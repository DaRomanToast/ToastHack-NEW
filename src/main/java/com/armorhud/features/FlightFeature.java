package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.EnumSetting;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;


public class FlightFeature extends Feature implements UpdateListener {

	public MinecraftClient mc = MinecraftClient.getInstance();
	
    public GameMode getGameMode(PlayerEntity player) {
		nullCheck();
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid()); 
        if (playerListEntry != null) return playerListEntry.getGameMode(); 
        return GameMode.DEFAULT;
    }
	
	public final IntegerSetting speed = new IntegerSetting("Speed", 1,this);
	private final EnumSetting<FlightFeature.Mode> mode = new EnumSetting<>("mode",  FlightFeature.Mode.values(), FlightFeature.Mode.Static, this);
	
	int bypassTimer = 0;
	
	public FlightFeature() {
		super("Flight", "Movement");
	}
	
	@Override
	public void onUpdate() {
		nullCheck();
        if (mode.getValue().toString().equalsIgnoreCase("Vanilla")) {
        	mc.player.getAbilities().allowFlying = true;
        	mc.player.getAbilities().setFlySpeed(((float) speed.getValue()) / 10);
		

        } else
			nullCheck(); if (mode.getValue().toString().equalsIgnoreCase("Static")) {
        	GameOptions go = mc.options;
        	float y = mc.player.getYaw();
        	int mx = 0, my = 0, mz = 0;
 
        	if (go.jumpKey.isPressed()) {
        		my++;
        	}
        	if (go.backKey.isPressed()) {
        		mz++;
        	}
        	if (go.leftKey.isPressed()) {
        		mx--;
        	}
        	if (go.rightKey.isPressed()) {
        		mx++;
        	}
        	if (go.sneakKey.isPressed()) {
        		my--;
        	}
        	if (go.forwardKey.isPressed()) {
        		mz--;
        	}
        	double ts = speed.getValue() / 2;
            double s = Math.sin(Math.toRadians(y));
            double c = Math.cos(Math.toRadians(y));
            double nx = ts * mz * s;
            double nz = ts * mz * -c;
            double ny = ts * my;
            nx += ts * mx * -c;
            nz += ts * mx * -s;
            Vec3d nv3 = new Vec3d(nx, ny, nz);
            mc.player.setVelocity(nv3);
        }
	}
	private enum Mode {
		Static,
		Vanilla
	}

	@Override
	public void onDisable() {
		nullCheck();
		if ((getGameMode(mc.player) == GameMode.CREATIVE)) {
			mc.player.getAbilities().allowFlying = true;
			mc.player.getAbilities().flying = false;
		} else {
			mc.player.getAbilities().allowFlying = false;
			mc.player.getAbilities().flying = false;
		}
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
	}
	@Override
	public void onEnable() {
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
		nullCheck();
		if ((getGameMode(mc.player) == GameMode.CREATIVE)) {
			mc.player.getAbilities().allowFlying = true;
			mc.player.getAbilities().flying = true;
		} else {
			mc.player.getAbilities().allowFlying = true;
			mc.player.getAbilities().flying = true;
		}
	}
}