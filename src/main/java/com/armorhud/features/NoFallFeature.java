package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.EnumSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static com.armorhud.features.AutoFishFeature.mc;

public class NoFallFeature extends Feature implements UpdateListener {

	private final EnumSetting<NoFallFeature.Mode> mode = new EnumSetting<>("mode",  NoFallFeature.Mode.values(), NoFallFeature.Mode.OnGround, this);

	public NoFallFeature() {
		super("NoFall", "Misc");
		 addSetting(mode);
	}
	@Override
	public void onEnable()
	{
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
		super.onEnable();
	}

	@Override
	public void onDisable()
	{
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
		super.onDisable();
	}

	private enum Mode {
		OnGround,
		BreakFall
	}
	  @Override
	    public void onUpdate() {
		  nullCheck();
	        if (mc.player == null || mc.getNetworkHandler() == null) {
	            return;
	        }
	        if (mode.getValue().toString().equalsIgnoreCase("BreakFall")) {
	        	if (mc.player.fallDistance > 2.5) {
	        		mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
	        		mc.player.setVelocity(0, 0.1, 0);
	        		mc.player.fallDistance = 0;
	        	}
	        } else if (mode.getValue().toString().equalsIgnoreCase("OnGround")) {
	        	if (mc.player.fallDistance > 2.5) mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
	        }
	    }
}
