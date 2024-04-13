package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.utils.MoveHelper2;
import com.armorhud.utils.PacketHelper;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import static com.armorhud.ClientInitializer.mc;

public class GuiMoveFeature extends Feature implements UpdateListener {

	public BooleanSetting rotateOnArrows = new BooleanSetting("RotateOnArrows", true,this);
	public BooleanSetting clickBypass = new BooleanSetting("strict", false,this);
	public BooleanSetting sneak = new BooleanSetting("sneak", false,this);

	public static boolean pause = false;
	@Override
	protected void onEnable()
	{
		DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
	}

	@Override
	protected void onDisable()
	{
		DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
	}
	public GuiMoveFeature() {
		super("GuiMove", "Movement");
	}

	@Override
	public void onUpdate() {
		if (mc.currentScreen != null) {
			if (!(mc.currentScreen instanceof ChatScreen)) {
				mc.player.setSprinting(true);

				for (KeyBinding k : new KeyBinding[] { mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey }) {
					k.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));
				}
				if(rotateOnArrows.getValue()) {
					if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 264)) {
						mc.player.setPitch(mc.player.getPitch() + 5);
					}
					if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 265)) {
						mc.player.setPitch(mc.player.getPitch() - 5);
					}
					if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 262)) {
						mc.player.setYaw(mc.player.getYaw() + 5);
					}
					if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 263)) {
						mc.player.setYaw(mc.player.getYaw() - 5);
					}
					if (mc.player.getPitch() > 90) {
						mc.player.setYaw(90);
					}
					if (mc.player.getPitch() < -90) {
						mc.player.setYaw(-90);
					}
				}
				if (sneak.getValue()) {
					mc.options.sneakKey.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.sneakKey.getBoundKeyTranslationKey()).getCode()));
				}
			}
		}
	}


	public void onPacketSend() {
		if(pause) {
			pause = false;
			return;
		}
		if (clickBypass.getValue() && mc.player.isOnGround() && MoveHelper2.isMoving() && !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, 0.0656, 0.0)).iterator().hasNext()) {
			if (mc.player.isSprinting()) {
				PacketHelper.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
			}
			PacketHelper.sendPosition(new Vec3d(mc.player.getX(), mc.player.getY() + 0.0656, mc.player.getZ()));
		}
	}

	public void onPacketSendPost() {
		if (mc.player.isSprinting()) {
			PacketHelper.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
		}
	}

}
