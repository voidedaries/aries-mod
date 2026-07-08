package dev.voidedaries.aries.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockRenderer {
    private static final int[][] EDGES = {
        {0,1},{1,2},{2,3},{3,0},
        {4,5},{5,6},{6,7},{7,4},
        {0,4},{1,5},{2,6},{3,7}
    };

    private static final float[][] CORNERS = {
        {0,0,0},
        {1,0,0},
        {1,0,1},
        {0,0,1},
        {0,1,0},
        {1,1,0},
        {1,1,1},
        {0,1,1}
    };

    public static void renderBlockOutline(LevelRenderContext context, AABB box, int rgba, float width) {
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float r = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;

        renderBlockOutline(context, box, r, g, b, a, width);
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

        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 camPos = camera.position();

        poseStack.pushPose();

        poseStack.translate(
            -camPos.x,
            -camPos.y,
            -camPos.z
        );

        float[][] corners = {
            {(float) box.minX, (float) box.minY, (float) box.minZ},
            {(float) box.maxX, (float) box.minY, (float) box.minZ},
            {(float) box.maxX, (float) box.minY, (float) box.maxZ},
            {(float) box.minX, (float) box.minY, (float) box.maxZ},

            {(float) box.minX, (float) box.maxY, (float) box.minZ},
            {(float) box.maxX, (float) box.maxY, (float) box.minZ},
            {(float) box.maxX, (float) box.maxY, (float) box.maxZ},
            {(float) box.minX, (float) box.maxY, (float) box.maxZ}
        };

        SubmitNodeCollector.CustomGeometryRenderer renderer = (pose, buffer) -> {
            for (int[] edge : EDGES) {
                float[] start = corners[edge[0]];
                float[] end = corners[edge[1]];

                drawLine(
                    pose,
                    buffer,
                    start[0], start[1], start[2],
                    end[0], end[1], end[2],
                    r, g, b, a,
                    width
                );
            }
        };

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.LINES,
            renderer
        );

        poseStack.popPose();
    }

    private static void drawLine(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float r, float g, float b, float a,
        float width
    ) {
        buffer.addVertex(pose, x1, y1, z1)
            .setColor(r, g, b, a)
            .setLineWidth(width)
            .setNormal(pose, x2-x1, y2-y1, z2-z1);

        buffer.addVertex(pose, x2, y2, z2)
            .setColor(r, g, b, a)
            .setLineWidth(width)
            .setNormal(pose, x2-x1, y2-y1, z2-z1);
    }
}
