package com.armorhud.features;

import com.armorhud.FriendList;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.DecimalSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static com.armorhud.Client.MC;
import static com.armorhud.ClientInitializer.mc;

public class TriggerBotTwoFeature extends Feature implements UpdateListener
{
	private final FriendList friendList;
	private final BooleanSetting activateOnLeftClick = new BooleanSetting("activateOnLeftClick",  false, this);
	private final DecimalSetting cooldown = new DecimalSetting("cooldown", 0.9, this);
	private final BooleanSetting attackInAir = new BooleanSetting("attackInAir", true,this);
	private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS", true, this);
	private final BooleanSetting attackOnJump = new BooleanSetting("attackOnJump", true,this);
	public TriggerBotTwoFeature(FriendList friendList)
	{
		super("WhitePower", "Combat");
		this.friendList = friendList;
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

	@Override
	public void onUpdate() {
		if (MC.currentScreen != null) {
			return;
		}
		IMouse mouse = (IMouse) MC.mouse;
		if (mc.player.isUsingItem())
			return;
		if (this.activateOnLeftClick.getValue() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), 0) != 1) {
			return;
		}
		if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem) &&
				!(mc.player.getMainHandStack().getItem() instanceof AxeItem) &&
		!(mc.player.getMainHandStack().getItem() instanceof ShovelItem)
			&&
		!(mc.player.getMainHandStack().getItem() instanceof HoeItem)
						&&
		!(mc.player.getMainHandStack().getItem() instanceof PickaxeItem))
			return;
		HitResult hit = mc.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (mc.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;

		Entity target = ((EntityHitResult) hit).getEntity();
		if (!isValidEntity(target))
			return;
		if (!target.isOnGround() && !attackInAir.getValue())
			return;
		if (mc.player.getY() > mc.player.prevY && !attackOnJump.getValue())
			return;

		mc.interactionManager.attackEntity(mc.player, target);
		mc.player.swingHand(Hand.MAIN_HAND);
		if (this.fakeCPS.getValue()) {
			mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 1, 0);
			mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 0, 0);
		}
	}

	private boolean isValidEntity(final Entity crossHairTarget) {
		if (crossHairTarget instanceof PlayerEntity && friendList.isFriend((PlayerEntity) crossHairTarget)) {
			return false;
		}
		if (crossHairTarget instanceof PlayerEntity) {
			return true;
		}
		if (crossHairTarget instanceof SnowGolemEntity) {
			return true;
		}
		return true;
	}

}
