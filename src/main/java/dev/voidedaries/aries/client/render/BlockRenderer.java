package dev.voidedaries.aries.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockRenderer {
    private static final double OVERLAY_EXPANSION = 0.002;

    public static void renderBlockOutline(
        LevelRenderContext context,
        AABB box,
        int rgba,
        float width
    ) {
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float r = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;

        renderBlockOutline(context, box, r, g, b, a, width);
    }

    public static void renderBlock(
        LevelRenderContext context,
        AABB box,
        int rgba
    ) {
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float r = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;

        renderBlock(context, box, r, g, b, a);
    }

    public static void renderBlock(
        LevelRenderContext context,
        AABB box,
        float r,
        float g,
        float b,
        float a
    ) {
        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();

        AABB expandedBox = box.inflate(OVERLAY_EXPANSION);

        Vec3 camPos = context.levelState().cameraRenderState.pos;

        poseStack.pushPose();

        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        SubmitNodeCollector.CustomGeometryRenderer renderer =
            (pose, buffer) -> drawFilledBox(pose, buffer, expandedBox, r, g, b, a);

        collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), renderer);

        poseStack.popPose();
    }

    public static void renderBlockOutline(
        LevelRenderContext context,
        AABB box,
        float r,
        float g,
        float b,
        float a,
        float width
    ) {
        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();

        AABB expandedBox = box.inflate(OVERLAY_EXPANSION);

        Vec3 camPos = context.levelState().cameraRenderState.pos;

        poseStack.pushPose();

        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        SubmitNodeCollector.CustomGeometryRenderer renderer =
            (pose, buffer) -> drawBoxOutline(pose, buffer, expandedBox, r, g, b, a, width);

        collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), renderer);

        poseStack.popPose();
    }

    private static void drawFilledBox(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        AABB box,
        float r,
        float g,
        float b,
        float a
    ) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;

        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Front
        drawQuad(pose, buffer, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, r, g, b, a);
        // Back
        drawQuad(pose, buffer, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        // Left
        drawQuad(pose, buffer, minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
        // Right
        drawQuad(pose, buffer, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        // Top
        drawQuad(pose, buffer, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        // Bottom
        drawQuad(pose, buffer, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, r, g, b, a);
    }

    private static void drawBoxOutline(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        AABB box,
        float r,
        float g,
        float b,
        float a,
        float width
    ) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;

        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        float w = width * 0.01F;

        // Bottom
        drawEdgeBox(pose, buffer, minX, minY, minZ, maxX, minY + w, minZ + w, r, g, b, a);
        drawEdgeBox(pose, buffer, maxX - w, minY, minZ, maxX, minY + w, maxZ, r, g, b, a);
        drawEdgeBox(pose, buffer, minX, minY, maxZ - w, maxX, minY + w, maxZ, r, g, b, a);
        drawEdgeBox(pose, buffer, minX, minY, minZ, minX + w, minY + w, maxZ, r, g, b, a);

        // Top
        drawEdgeBox(pose, buffer, minX, maxY - w, minZ, maxX, maxY, minZ + w, r, g, b, a);
        drawEdgeBox(pose, buffer, maxX - w, maxY - w, minZ, maxX, maxY, maxZ, r, g, b, a);
        drawEdgeBox(pose, buffer, minX, maxY - w, maxZ - w, maxX, maxY, maxZ, r, g, b, a);
        drawEdgeBox(pose, buffer, minX, maxY - w, minZ, minX + w, maxY, maxZ, r, g, b, a);

        // Vertical
        drawEdgeBox(pose, buffer, minX, minY, minZ, minX + w, maxY, minZ + w, r, g, b, a);
        drawEdgeBox(pose, buffer, maxX - w, minY, minZ, maxX, maxY, minZ + w, r, g, b, a);
        drawEdgeBox(pose, buffer, minX, minY, maxZ - w, minX + w, maxY, maxZ, r, g, b, a);
        drawEdgeBox(pose, buffer, maxX - w, minY, maxZ - w, maxX, maxY, maxZ, r, g, b, a);
    }

    private static void drawEdgeBox(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ,
        float r,
        float g,
        float b,
        float a
    ) {
        // Front
        drawQuad(pose, buffer, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, r, g, b, a);
        // Back
        drawQuad(pose, buffer, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        // Left
        drawQuad(pose, buffer, minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
        // Right
        drawQuad(pose, buffer, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        // Top
        drawQuad(pose, buffer, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        // Bottom
        drawQuad(pose, buffer, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, r, g, b, a);
    }

    private static void drawQuad(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        float x3,
        float y3,
        float z3,
        float x4,
        float y4,
        float z4,
        float r,
        float g,
        float b,
        float a
    ) {
        buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
        buffer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a);
        buffer.addVertex(pose, x3, y3, z3).setColor(r, g, b, a);
        buffer.addVertex(pose, x4, y4, z4).setColor(r, g, b, a);
    }
}