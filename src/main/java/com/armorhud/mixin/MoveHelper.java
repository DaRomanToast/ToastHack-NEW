package com.armorhud.mixin;

import com.armorhud.Client;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class MoveHelper {

    public static boolean hasMovement() {
        final Vec3d playerMovement = Client.MC.player.getVelocity();
        return playerMovement.getX() != 0;
    }

    public static double motionY(final double motionY) {
        final Vec3d vec3d = Client.MC.player.getVelocity();
        Client.MC.player.setVelocity(vec3d.x, motionY, vec3d.z);
        return motionY;
    }

    public static double motionYPlus(final double motionY) {
        final Vec3d vec3d = Client.MC.player.getVelocity();
        Client.MC.player.setVelocity(vec3d.x, vec3d.y + motionY, vec3d.z);
        return motionY;
    }

    public static double getDistanceToGround(Entity entity) {
        final double playerX = Client.MC.player.getX();
        final int playerHeight = (int) Math.floor(Client.MC.player.getY());
        final double playerZ = Client.MC.player.getZ();

        for (int height = playerHeight; height > 0; height--) {
            final BlockPos checkPosition = new BlockPos((int) playerX, height, (int) playerZ);

            // Check if the block is solid
            if (!Client.MC.world.isAir(checkPosition)) {
                return playerHeight - height;
            }
        }
        return 0;
    }
}