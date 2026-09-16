package dev.voidedaries.aries.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;

public class LineRenderer {

    private LineRenderer() {
    }

    public static void renderLine(LevelRenderContext context, Vec3 start, Vec3 end, int rgba, double thickness) {
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float r = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;

        renderLine(context, start, end, r, g, b, a, thickness);
    }

    public static void renderLine(
        LevelRenderContext context,
        Vec3 start, Vec3 end,
        float r, float g, float b, float a,
        double thickness
    ) {
        Vec3 direction = end.subtract(start);

        if (direction.lengthSqr() == 0) {
            return;
        }

        direction = direction.normalize();

        Vec3 up = Math.abs(direction.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);

        Vec3 right = direction.cross(up).normalize().scale(thickness);
        Vec3 beamUp = right.cross(direction).normalize().scale(thickness);

        PoseStack poseStack = context.poseStack();
        SubmitNodeCollector collector = context.submitNodeCollector();

        Vec3 cameraPosition = context.levelState().cameraRenderState.pos;

        poseStack.pushPose();
        poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        SubmitNodeCollector.CustomGeometryRenderer renderer =
            (pose, buffer) -> drawBeam(
                pose,
                buffer,
                start, end,
                right, beamUp,
                r, g, b, a
            );

        collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), renderer);
        poseStack.popPose();
    }

    private static void drawBeam(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        Vec3 start,
        Vec3 end,
        Vec3 right,
        Vec3 up,
        float r,
        float g,
        float b,
        float a
    ) {
        Vec3 startBottomLeft = start.subtract(right).subtract(up);
        Vec3 startBottomRight = start.add(right).subtract(up);
        Vec3 startTopRight = start.add(right).add(up);
        Vec3 startTopLeft = start.subtract(right).add(up);

        Vec3 endBottomLeft = end.subtract(right).subtract(up);
        Vec3 endBottomRight = end.add(right).subtract(up);
        Vec3 endTopRight = end.add(right).add(up);
        Vec3 endTopLeft = end.subtract(right).add(up);

        drawQuad(
            pose,
            buffer,
            startBottomLeft, startBottomRight,
            endBottomRight, endBottomLeft,
            r, g, b, a
        );

        drawQuad(
            pose,
            buffer,
            startTopLeft, endTopLeft,
            endTopRight, startTopRight,
            r, g, b, a
        );

        drawQuad(
            pose,
            buffer,
            startBottomLeft, endBottomLeft,
            endTopLeft, startTopLeft,
            r, g, b, a
        );

        drawQuad(
            pose,
            buffer,
            startBottomRight, startTopRight,
            endTopRight, endBottomRight,
            r, g, b, a
        );
    }

    private static void drawQuad(
        PoseStack.Pose pose,
        VertexConsumer buffer,
        Vec3 a,
        Vec3 b,
        Vec3 c,
        Vec3 d,
        float r,
        float g,
        float bColor,
        float alpha
    ) {
        buffer.addVertex(pose, (float) a.x, (float) a.y, (float) a.z).setColor(r, g, bColor, alpha);
        buffer.addVertex(pose, (float) b.x, (float) b.y, (float) b.z).setColor(r, g, bColor, alpha);
        buffer.addVertex(pose, (float) c.x, (float) c.y, (float) c.z).setColor(r, g, bColor, alpha);
        buffer.addVertex(pose, (float) d.x, (float) d.y, (float) d.z).setColor(r, g, bColor, alpha);
    }

}
