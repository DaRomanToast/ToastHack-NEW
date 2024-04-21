package com.armorhud.utils;

import com.armorhud.Client;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import com.armorhud.mixin.WorldRendererAccessor;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.armorhud.Client.MC;
import static com.armorhud.ClientInitializer.mc;

public enum RenderUtils
{
	;



	public static void drawString(String string, int x, int y, int color, float scale)
	{
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.translate(0.0D, 0.0D, 0.0D);
		matrixStack.scale(scale, scale, 1);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		immediate.draw();
	}



	public static void drawBoxBoth(BlockPos blockPos, QuadColor color, Box box, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(new Box(blockPos), color, lineWidth, excludeDirs);
		BlockPos BlockPos = null;
		drawBoxBoth(new Box(BlockPos), color, lineWidth);
	}

	public static void drawBoxBoth(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		QuadColor outlineColor = color.clone();
		outlineColor.overwriteAlpha(255);

		drawBoxBoth(box, color, outlineColor, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(BlockPos blockPos, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(new Box(blockPos), fillColor, outlineColor, lineWidth, excludeDirs);
	}



	public static class R3D {
		public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
			renderFilled(start, dimensions, color, stack, GL11.GL_ALWAYS);
		}
		public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack, int GLMODE) {
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			if (!stack.isEmpty()) {
				float red = color.getRed() / 255f;
				float green = color.getGreen() / 255f;
				float blue = color.getBlue() / 255f;
				float alpha = color.getAlpha() / 255f;
				Camera c = Client.MC.gameRenderer.getCamera();
				Vec3d camPos = c.getPos();
				Vec3d start1 = start.subtract(camPos);
				Vec3d end = start1.add(dimensions);
				Matrix4f matrix = stack.peek().getPositionMatrix();
				float x1 = (float) start1.x;
				float y1 = (float) start1.y;
				float z1 = (float) start1.z;
				float x2 = (float) end.x;
				float y2 = (float) end.y;
				float z2 = (float) end.z;
				BufferBuilder buffer = Tessellator.getInstance().getBuffer();

				GL11.glDepthFunc(GLMODE);
				RenderSystem.disableCull();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				RenderSystem.setShader(GameRenderer::getPositionColorProgram);
				buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

				buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

				buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

				buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

				buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

				buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
				buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();


				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			} else {
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				stack.push();
				end(stack);
			}
		}
		public static void renderLine(MatrixStack stack, Color c, double x, double y, double x1, double y1) {
			float g = c.getRed() / 255f;
			float h = c.getGreen() / 255f;
			float k = c.getBlue() / 255f;
			float f = c.getAlpha() / 255f;
			Matrix4f m = stack.peek().getPositionMatrix();
			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
			bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).next();
			bufferBuilder.end();
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			end(stack);
		}
		public static void renderLine(Vec3d start, Vec3d end, Color color, MatrixStack matrices) {
			float red = color.getRed() / 255f;
			float green = color.getGreen() / 255f;
			float blue = color.getBlue() / 255f;
			float alpha = color.getAlpha() / 255f;
			Camera c = Client.MC.gameRenderer.getCamera();
			Vec3d camPos = c.getPos();
			Vec3d start1 = start.subtract(camPos);
			Vec3d end1 = end.subtract(camPos);
			Matrix4f matrix = matrices.peek().getPositionMatrix();
			float x1 = (float) start1.x;
			float y1 = (float) start1.y;
			float z1 = (float) start1.z;
			float x2 = (float) end1.x;
			float y2 = (float) end1.y;
			float z2 = (float) end1.z;
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			GL11.glDepthFunc(GL11.GL_ALWAYS);
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();

			BufferRenderer.drawWithGlobalProgram(buffer.end());
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			end(matrices);
		}
		public static void renderCircle(Vec3d pos, double radius, int color, int segments, MatrixStack matrices) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);

			Matrix4f matrix = matrices.peek().getPositionMatrix();
			float red = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8 & 255) / 255.0F;
			float blue = (color & 255) / 255.0F;
			float alpha = (color >> 24 & 255) / 255.0F;

			BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

			double increment = 2.0 * Math.PI / segments;
			for (int i = 0; i < segments; i++) {
				double angle = i * increment;
				double nextAngle = (i + 1) * increment;
				double x = pos.x + radius * Math.cos(angle);
				double z = pos.z + radius * Math.sin(angle);
				double nextX = pos.x + radius * Math.cos(nextAngle);
				double nextZ = pos.z + radius * Math.sin(nextAngle);

				bufferBuilder.vertex(matrix, (float) x, (float) pos.y, (float) z).color(red, green, blue, alpha).next();
				bufferBuilder.vertex(matrix, (float) nextX, (float) pos.y, (float) nextZ).color(red, green, blue, alpha).next();
			}
			BufferRenderer.draw(bufferBuilder.end());
			RenderSystem.disableBlend();
		}
		public static void renderOutline(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
			float red = color.getRed() / 255f;
			float green = color.getGreen() / 255f;
			float blue = color.getBlue() / 255f;
			float alpha = color.getAlpha() / 255f;

			GL11.glDepthFunc(GL11.GL_ALWAYS);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			Camera c = Client.MC.gameRenderer.getCamera();
			Vec3d camPos = c.getPos();
			Vec3d start1 = start.subtract(camPos);
			Vec3d end = start1.add(dimensions);
			Matrix4f matrix = stack.peek().getPositionMatrix();
			float x1 = (float) start1.x;
			float y1 = (float) start1.y;
			float z1 = (float) start1.z;
			float x2 = (float) end.x;
			float y2 = (float) end.y;
			float z2 = (float) end.z;

			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();

			buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

			buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

			buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();

			buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

			BufferRenderer.drawWithGlobalProgram(buffer.end());
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
		}
		public static Vec3d getCrosshairVector() {

			Camera camera = Client.MC.gameRenderer.getCamera();

			float vec = 0.017453292F;
			float pi = (float) Math.PI;

			float f1 = MathHelper.cos(-camera.getYaw() * vec - pi);
			float f2 = MathHelper.sin(-camera.getYaw() * vec - pi);
			float f3 = -MathHelper.cos(-camera.getPitch() * vec);
			float f4 = MathHelper.sin(-camera.getPitch() * vec);

			return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
		}

		record FadingBlock(Color outline, Color fill, Vec3d start, Vec3d dimensions, long created, long lifeTime) {
			long getLifeTimeLeft() {
				return Math.max(0, (created - System.currentTimeMillis()) + lifeTime);
			}

			boolean isDead() {
				return getLifeTimeLeft() == 0;
			}
		}
		public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
			return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite,
					greenOverwrite == -1 ? original.getGreen() : greenOverwrite,
					blueOverwrite == -1 ? original.getBlue() : blueOverwrite,
					alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
		}
	}



	public static void drawItem(ItemStack itemStack, int x, int y, double scale, boolean overlay) {
		RenderSystem.disableDepthTest();

		MatrixStack matrices = RenderSystem.getModelViewStack();

		matrices.push();
		matrices.scale((float) scale, (float) scale, 1);

		MC.getItemRenderer().renderItem(new ItemStack(itemStack.getItem()),ModelTransformationMode.GUI, (int) (x / scale), (int) (y / scale), matrices, mc.getBufferBuilders().getEntityVertexConsumers(), null, 1);
		if (overlay) MC.getItemRenderer().renderItem(new ItemStack(itemStack.getItem()),ModelTransformationMode.GUI, (int) (x / scale), (int) (y / scale), matrices, mc.getBufferBuilders().getEntityVertexConsumers(), null, 1);

		matrices.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
	}

	public static void drawBoxBoth(Box box, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxFill(box, fillColor, excludeDirs);
		drawBoxOutline(box, outlineColor, lineWidth, excludeDirs);
	}

	public static Frustum getFrustum() {
		return ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getFrustum();
	}

	public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup3DRender(true);

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Fill
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Vertexer.vertexBoxQuads(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		end3DRender();
	}
	public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrices = new MatrixStack();

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();


		matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrices;
	}

	public static Box moveToZero(Box box) {
		return box.offset(getMinVec(box).negate());
	}

	public static Vec3d getMinVec(Box box) {
		return new Vec3d(box.minX, box.minY, box.minZ);
	}

	public static void setup3DRender(boolean disableDepth) {
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		if (disableDepth)
			RenderSystem.disableDepthTest();
		RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
		RenderSystem.enableCull();
	}

	public static void end3DRender() {
		RenderSystem.disableCull();
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
	}

	public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup3DRender(true);

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Outline
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(lineWidth);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexBoxLines(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		RenderSystem.enableCull();

		end3DRender();
	}

	public static void bindTexture(Identifier identifier) {
		RenderSystem.setShaderTexture(0, identifier);
	}

	public static void shaderColor(int rgb) {
		float alpha = (rgb >> 24 & 0xFF) / 255.0F;
		float red = (rgb >> 16 & 0xFF) / 255.0F;
		float green = (rgb >> 8 & 0xFF) / 255.0F;
		float blue = (rgb & 0xFF) / 255.0F;
		RenderSystem.setShaderColor(red, green, blue, alpha);
	}

	public static void setup2DRender(boolean disableDepth) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		if (disableDepth)
			RenderSystem.disableDepthTest();
	}

	public static void end2DRender() {
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
	}


	/*public static void drawItem(ItemStack itemStack, int x, int y, double scale, boolean overlay) {
		RenderSystem.disableDepthTest();

		MatrixStack matrices = RenderSystem.getModelViewStack();

		matrices.push();
		matrices.scale((float) scale, (float) scale, 1);
		if (overlay) MC.getItemRenderer().renderGuiItemOverlay(null, MC.textRenderer, itemStack, (int) (x / scale), (int) (y / scale));

		matrices.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
	}*/

	public static Vec3d getCameraPos()
	{
		return MC.getBlockEntityRenderDispatcher().camera.getPos();
	}

	public static Vec3d getRenderLookVec(double partialTicks)
	{
		double f = 0.017453292;
		double pi = Math.PI;

		double yaw = MathHelper.lerp(partialTicks, MC.player.prevYaw, MC.player.getYaw());
		double pitch = MathHelper.lerp(partialTicks, MC.player.prevPitch, MC.player.getPitch());

		double f1 = Math.cos(-yaw * f - pi);
		double f2 = Math.sin(-yaw * f - pi);
		double f3 = -Math.cos(-pitch * f);
		double f4 = Math.sin(-pitch * f);

		return new Vec3d(f2 * f3, f4, f1 * f3).normalize();
	}

	public static BlockPos getCameraBlockPos()
	{
		return MC.getBlockEntityRenderDispatcher().camera.getBlockPos();
	}

	public static void applyRegionalRenderOffset(MatrixStack matrixStack)
	{
		Vec3d camPos = getCameraPos();
		BlockPos blockPos = getCameraBlockPos();

		int regionX = (blockPos.getX() >> 9) * 512;
		int regionZ = (blockPos.getZ() >> 9) * 512;

		matrixStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
	}

	public static void fillBox(BufferBuilder bufferBuilder, Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
	}

	public static void drawSolidBox(Box bb, MatrixStack matrixStack)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
				VertexFormats.POSITION);
		fillBox(bufferBuilder, bb, matrixStack);
		bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	public static void fillOutlinedBox(BufferBuilder bufferBuilder, Box bb, MatrixStack matrixStack)
	{
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();

		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ)
				.next();
		bufferBuilder
				.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ)
				.next();
	}

	public static void drawOutlinedBox(Box bb, MatrixStack matrixStack)
	{
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
				VertexFormats.POSITION);
		fillOutlinedBox(bufferBuilder, bb, matrixStack);
		bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	public static void drawQuad(float x1, float y1, float x2, float y2, MatrixStack matrixStack)
	{
		float minX = Math.min(x1, x2);
		float maxX = Math.max(x1, x2);
		float minY = Math.min(y1, y2);
		float maxY = Math.max(y1, y2);
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x1, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y2, 0).next();
		bufferBuilder.vertex(matrix, x2, y1, 0).next();
		bufferBuilder.vertex(matrix, x1, y1, 0).next();

		bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}
	public static void drawWorldText(String text, double x, double y, double z, double scale, int color, boolean background) {
		drawWorldText(text, x, y, z, 0, 0, scale, false, color, background);
	}
	//public static void renderLine(Vec3d start, Vec3d end, java.awt.Color color, MatrixStack matrices) {
	//		float red = color.getRed() / 255f;
	//		float green = color.getGreen() / 255f;
	//		float blue = color.getBlue() / 255f;
	//		float alpha = color.getAlpha() / 255f;
	//		Camera c = MC.gameRenderer.getCamera();
	//		Vec3d camPos = c.getPos();
	//		start = start.subtract(camPos);
	//		end = end.subtract(camPos);
	//		Matrix4f matrix = matrices.peek().getPositionMatrix();
	//		float x1 = (float) start.x;
	//		float y1 = (float) start.y;
	//		float z1 = (float) start.z;
	//		float x2 = (float) end.x;
	//		float y2 = (float) end.y;
	//		float z2 = (float) end.z;
	//		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
	//		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
	//		GL11.glDepthFunc(GL11.GL_ALWAYS);
	//		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	//		RenderSystem.enableBlend();
	//		RenderSystem.defaultBlendFunc();
	//		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
	//
	//		buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
	//		buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
	//
	//		;
	//
	//		BufferRenderer.drawWithGlobalProgram(buffer.endNullable());
	//		GL11.glDepthFunc(GL11.GL_LEQUAL);
	//		RenderSystem.disableBlend();
	//	}

	private static void drawWorldText(String text, double x, double y, double z, int i, int i1, double scale, boolean b, int color, boolean background) {
		drawWorldText(text, x, y, z, 0, 0, scale, false, color, background);
	}

	public static void setup(MatrixStack stack){
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		stack.push();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();

	}

	public static void draw3DBox(MatrixStack stack, Box box, Color color, float alpha) {
		setup(stack);
		Vec3d camPos = mc.getBlockEntityRenderDispatcher().camera.getPos();
		stack.translate(-camPos.x, -camPos.y, -camPos.z);
		RenderSystem.setShaderColor(((float) color.getRed()), ((float) color.getGreen()), ((float) color.getBlue()), 1.0f);

		Matrix4f matrix = stack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		tessellator.draw();

		RenderSystem.setShaderColor(((float) color.getRed()), ((float) color.getGreen()), ((float) color.getBlue()), alpha);
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		tessellator.draw();
		end(stack);
	}
	public static void end(MatrixStack stack){
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		stack.pop();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
