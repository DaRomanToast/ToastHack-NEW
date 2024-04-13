package com.armorhud.mixin;

import com.armorhud.events.WorldRenderListener;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At("RETURN"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        WorldRenderListener.RenderWorldEvent renderWorldEvent = new WorldRenderListener.RenderWorldEvent(matrices);
        //@formatter:off
        DietrichEvents2.global().postInternal(WorldRenderListener.RenderWorldEvent.ID, renderWorldEvent);
        //@formatter:on
    }
}