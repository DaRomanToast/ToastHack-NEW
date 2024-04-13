package com.armorhud.features;

import com.armorhud.events.PacketListener;
import com.armorhud.events.Type;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.FakePlayerEntityUtil;
import com.armorhud.utils.PlayerUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static com.armorhud.Client.MC;

public class FreecamFeature extends Feature implements PacketListener, UpdateListener {

	public FreecamFeature() {
		super("FreeCam", "Render");
	}

	private final IntegerSetting speed = new IntegerSetting("Speed",  2, this);

	private FakePlayerEntityUtil clone;
	private Entity vehicle;
	private Vec3d playerPosition;
	private Vec2f playerRotation;

	@Override
	public void onEnable() {
		DietrichEvents2.global().subscribe(PacketEvent.ID, this);
		DietrichEvents2.global().subscribe(UpdateEvent.ID, this);

		assert MC.player != null;
		MC.player.noClip = true;
		MC.chunkCullingEnabled = false;

		clone = new FakePlayerEntityUtil();
		playerPosition = MC.player.getPos();
		playerRotation = new Vec2f(MC.player.getYaw(), MC.player.getPitch());
		vehicle = MC.player.getVehicle();

		clone.add();

		if(vehicle != null) MC.player.getVehicle().removeAllPassengers();

		if (MC.player.isSprinting()) MC.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));

		super.onEnable();
	}

	@Override
	public void onDisable() {
		DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
		DietrichEvents2.global().unsubscribe(UpdateEvent.ID, this);
		MC.chunkCullingEnabled = true;
		assert MC.player != null;
		MC.player.noClip = false;

		if(clone != null) {
			clone.remove();
			MC.player.refreshPositionAndAngles(playerPosition.getX(), playerPosition.getY(), playerPosition.getZ(), playerRotation.x, playerRotation.y);
		}

		MC.player.setVelocity(Vec3d.ZERO);
		MC.player.getAbilities().flying = false;
		MC.player.getAbilities().setFlySpeed(0.05f);

		if (vehicle != null && MC.world.getEntityById(vehicle.getId()) != null) {
			MC.player.startRiding(vehicle);
		}
	}

	@Override
	public void onPacket(PacketEvent packetEvent) {
		if(packetEvent.getType() == Type.OUTGOING){
			if(packetEvent.getPacket() instanceof PlayerMoveC2SPacket || packetEvent.getPacket() instanceof ClientCommandC2SPacket){
				packetEvent.cancel();
			}
		}
	}

	@Override
	public void onUpdate() {
		MC.player.setOnGround(false);
		if(PlayerUtils.isMoving()){
			MC.player.setVelocity(Vec3d.ZERO);
			MC.player.noClip = true;
		}
		MC.player.setPose(EntityPose.STANDING);
		MC.player.getAbilities().flying = true;
		MC.player.getAbilities().setFlySpeed(speed.getValue() / 5.0f);
	}
}