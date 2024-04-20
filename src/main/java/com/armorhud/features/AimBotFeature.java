package com.armorhud.features;

import com.armorhud.FriendList;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.DecimalSetting;
import com.armorhud.utils.RotationUtils;
import com.armorhud.Rotation;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import static com.armorhud.Client.MC;

public class AimBotFeature extends Feature implements UpdateListener {
    private final FriendList friendList;
    private final DecimalSetting range = new DecimalSetting("range",  4, this);
    private final DecimalSetting speed = new DecimalSetting("speed", 1, this);
    private PlayerEntity target;

    public AimBotFeature(FriendList friendList) {
        super("AimAssist",  "Combat");
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
        if (MC.currentScreen != null) {
            return;
        }
        target = findNearestPlayer();
        if (target != null) adjustPlayerRotationTowardsTarget();
    }

    private void adjustPlayerRotationTowardsTarget() {
        Rotation rotation = RotationUtils.getNeededRotations(RotationUtils.getEyesPos(), target.getBoundingBox().getCenter().add(0.0, 0.5, 0.0));

        if (RotationUtils.getEyesPos().squaredDistanceTo(target.getBoundingBox().getCenter()) > range.getValue() * range.getValue()) return;

        adjustYaw(rotation);
        adjustPitch(rotation);
    }

    private void adjustYaw(Rotation rotation) {
        float playerYaw = MC.player.getYaw();
        double deltaAngle = MathHelper.wrapDegrees(rotation.getYaw() - playerYaw);
        double toRotate = speed.getValue() * deltaAngle;
        if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
        MC.player.setYaw(playerYaw + (float) toRotate);
    }

    private void adjustPitch(Rotation rotation) {
        float playerPitch = MC.player.getPitch();
        double deltaAngle = MathHelper.wrapDegrees(rotation.getPitch() - playerPitch);
        double toRotate = speed.getValue() * deltaAngle;
        if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
        MC.player.setPitch(playerPitch + (float) toRotate);
    }

    private PlayerEntity findNearestPlayer() {
        return StreamSupport.stream(MC.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof PlayerEntity)
                .map(e -> (PlayerEntity) e)
                .filter(player -> player != MC.player)
                .filter(player -> !player.isRemoved())
                .min(Comparator.comparingDouble(player -> RotationUtils.getAngleToLookVec(player.getBoundingBox().getCenter().add(0.0, 0.5, 0.0))))
                .orElse(null);
    }
}
