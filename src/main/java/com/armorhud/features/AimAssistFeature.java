package com.armorhud.features;

import com.armorhud.FriendList;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.RotationUtils;
import com.armorhud.Rotation;
import com.armorhud.utils.MathUtil;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import static com.armorhud.ClientInitializer.mc;

public class AimAssistFeature extends Feature implements UpdateListener {

    private FriendList friendList;
    private PlayerEntity target;
    private final IntegerSetting speed = new IntegerSetting("speed", 5, this);
    private final IntegerSetting range = new IntegerSetting("range", 6, this);
    private final BooleanSetting requireWeapon  = new BooleanSetting("RequireWeapon wip keep false",false,this);

    public AimAssistFeature(FriendList friendList) {
        super("AimAssist", "Combat");
        this.friendList = friendList;
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen != null || !mc.player.isSprinting()) return;


        target = findNearestPlayer();
        if (target != null && checkConditions(target)) adjustPlayerRotationTowardsTarget();
    }

    private void adjustPlayerRotationTowardsTarget() {
        Rotation rotation = RotationUtils.getNeededRotations(RotationUtils.getEyesPos(), target.getBoundingBox().getCenter().add(0.0, 0.5, 0.0));
        double distance = RotationUtils.getEyesPos().squaredDistanceTo(target.getBoundingBox().getCenter());

        if (distance > Math.pow(range.getValue(), 2)) return;
        double yawSpeed = speed.getValue() / 3.0;
        double pitchSpeed = speed.getValue() / 4.5;

        adjustYaw(rotation, yawSpeed);
        adjustPitch(rotation, pitchSpeed);
    }

    private void adjustYaw(Rotation rotation, double maxSpeed) {
        float currentYaw = mc.player.getYaw();
        double targetYaw = rotation.getYaw();
        double delta = MathHelper.wrapDegrees(targetYaw - currentYaw);

        double smoothingFactor = 0.05;
        double change = delta * smoothingFactor;

        if (Math.abs(change) > maxSpeed) {
            change = Math.signum(change) * maxSpeed;
        }
        mc.player.setYaw((float) (currentYaw + change));
    }

    private void adjustPitch(Rotation rotation, double maxSpeed) {
        float currentPitch = mc.player.getPitch();
        double targetPitch = rotation.getPitch();
        double delta = MathHelper.wrapDegrees(targetPitch - currentPitch);

        double smoothingFactor = 0.05;
        double change = delta * smoothingFactor;

        if (Math.abs(change) > maxSpeed) {
            change = Math.signum(change) * maxSpeed;
        }
        mc.player.setPitch((float) (currentPitch + change));
    }

    private boolean checkConditions(PlayerEntity player) {
        return (!requireWeapon.getValue() || isHoldingWeapon(player)) && withinDistance(player);
    }

    private boolean isHoldingWeapon(PlayerEntity player) {
        Item item = player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof PickaxeItem || item instanceof HoeItem || item instanceof ShovelItem || item instanceof Item && item.equals(Items.TOTEM_OF_UNDYING);

    }

    private boolean withinDistance(PlayerEntity player) {
        double distance = player.distanceTo(mc.player);
        return distance <= range.getValue();
    }

    private PlayerEntity findNearestPlayer() {
        return StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof PlayerEntity)
                .map(e -> (PlayerEntity) e)
                .filter(player -> player != mc.player)
                .min(Comparator.comparingDouble(player -> player.distanceTo(mc.player)))
                .orElse(null);
    }
}