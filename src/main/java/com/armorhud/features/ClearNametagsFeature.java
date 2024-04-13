package com.armorhud.features;

import com.armorhud.feature.Feature;
import com.armorhud.utils.NotificationUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import com.armorhud.utils.RenderUtils;

import static com.armorhud.ClientInitializer.mc;

public class ClearNametagsFeature extends Feature {

    public ClearNametagsFeature() {
        super("ClearNametags", "HUD");
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        super.onWorldRender(matrices);
        nullCheck();
        renderNametags(matrices);
    }

    private void renderNametags(MatrixStack matrices) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSneaky()) continue; // Skip if the player is the client or is sneaking

            double distance = mc.player.squaredDistanceTo(player);
            if (distance > 10000) continue; // Skip if the player is too far away

            Text name = player.getName();
            renderNameTag(name, matrices, (AbstractClientPlayerEntity) player);
        }
    }

    private void renderNameTag(Text text, MatrixStack matrices, AbstractClientPlayerEntity player) {
        double x = player.prevX + (player.getX() - player.prevX) * mc.getTickDelta();
        double y = player.prevY + (player.getY() - player.prevY) * mc.getTickDelta();
        double z = player.prevZ + (player.getZ() - player.prevZ) * mc.getTickDelta();

        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        x -= camPos.x;
        y -= camPos.y;
        z -= camPos.z;

 //       RenderUtils.renderText(text, x, y + player.getHeight() + 0.5, z, 0.025f, true, 0, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), 0xF000F0, 0x00FF00); // Custom method to render text
    }

    @Override
    protected void onEnable() {
        NotificationUtils.notify("ClearNametags has been enabled");
    }

    @Override
    protected void onDisable() {
        NotificationUtils.notify("ClearNametags has been disabled");
    }
}
