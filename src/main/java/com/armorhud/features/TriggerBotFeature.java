package com.armorhud.features;

import com.armorhud.FriendList;
import com.armorhud.events.UpdateListener;
import com.armorhud.mixin.MoveHelper;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.DecimalSetting;
import com.armorhud.setting.EnumSetting;

import static com.armorhud.Client.MC;



public class TriggerBotFeature extends Feature implements UpdateListener {
    private final FriendList friendList;
    private final EnumSetting<Mode> mode = new EnumSetting<>("mode",  Mode.values(), Mode.All, this);
    public DecimalSetting cooldown = new DecimalSetting("HitCooldown",  0.9, this);
    public IntegerSetting critDistance = new IntegerSetting("CritDistance", 3, this);

    public  BooleanSetting attackInAir  = new BooleanSetting ("AttackInAir", true, this);
    public  BooleanSetting attackOnJump = new BooleanSetting("AttackOnJump",  false, this);
    private final BooleanSetting fakeCPS = new BooleanSetting("FakeCPS", true, this);
    private final BooleanSetting autoCrit = new BooleanSetting("AutoCrit",  true, this);
    public BooleanSetting immediateAttack = new BooleanSetting("ImmediateAttack",  false, this);
    private boolean firstAttackDone = false;
    private final BooleanSetting activateOnLeftClick = new BooleanSetting("ActivateOnLeftClick",  false, this);

    public TriggerBotFeature(FriendList friendList) {
        super("NiggerBeater", "Combat");
        this.friendList = friendList;
    }

    @Override
    protected void onEnable()
    {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    protected void onDisable()
    {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        firstAttackDone = false;
    }

    @Override
    public void onUpdate() {
        if (MC.currentScreen != null) {
            return;
        }
        IMouse mouse = (IMouse) MC.mouse;
        if (this.activateOnLeftClick.getValue() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), 0) != 1) {
            return;
        }
        if (MC.player.isUsingItem()) {
            return;
        }
        if (!itemInHand()) {
            return;
        }
        HitResult hit = MC.crosshairTarget;
        assert hit != null;
        if (hit.getType() != HitResult.Type.ENTITY) {
            return;
        }
        if (immediateAttack.getValue() && !firstAttackDone || MC.player.getAttackCooldownProgress(0) >= cooldown.getValue()) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (!isValidEntity(target)) {
                return;
            }
            if (!target.isOnGround() && !attackInAir.getValue()) {
                return;
            }
            if (MC.player.getY() > MC.player.prevY && !attackOnJump.getValue()) {
                return;
            }
            MC.interactionManager.attackEntity(MC.player, target);
            MC.player.swingHand(Hand.MAIN_HAND);
            if (this.fakeCPS.getValue()) {
                mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 1, 0);
                mouse.cwOnMouseButton(MC.getWindow().getHandle(), 0, 0, 0);
            }
            if (!firstAttackDone) {
                firstAttackDone = true;
            }
            if (MC.player.isOnGround() || MC.player.fallDistance >= critDistance.getValue() || this.hasFlyUtilities()) {
                if (autoCrit.isEnabled() && !MC.player.isOnGround() && MoveHelper.hasMovement()) {
                    assert MC.world != null;
                    MC.world.sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                }
            }
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

    private boolean hasFlyUtilities() {
        return MC.player.getAbilities().flying;
    }


    private enum Mode {
        Sword,
        All,
        Any
    }

    private boolean itemInHand() {
        assert MC.player != null;
        final Item item = MC.player.getMainHandStack().getItem();
        {
            if (mode.getValue() == Mode.Sword)
                return item instanceof SwordItem;
            if (mode.getValue() == Mode.All)
                return item instanceof SwordItem || item instanceof AxeItem || item instanceof PickaxeItem || item instanceof HoeItem || item instanceof ShovelItem || item instanceof Item && item.equals(Items.TOTEM_OF_UNDYING);
            if (mode.getValue() == Mode.Any);
            return true;
        }
    }
}

