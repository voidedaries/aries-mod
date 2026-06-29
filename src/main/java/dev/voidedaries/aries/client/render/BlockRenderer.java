package dev.voidedaries.aries.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BlockRenderer {

    public static void renderBlockOutline(WorldRenderContext context, BlockPos pos, int rgba, float width) {
        float a = ((rgba >> 24) & 0xFF) / 255f;
        float r = ((rgba >> 16) & 0xFF) / 255f;
        float g = ((rgba >> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;

        renderBlockOutline(context, pos, r, g, b, a, width);
    }

    public static void renderBlockOutline(WorldRenderContext context, BlockPos pos, float r, float g, float b, float a, float width) {
        PoseStack matrices = context.matrices();
        VertexConsumer consumer = context.consumers().getBuffer(RenderTypes.lines());

        matrices.pushPose();

        Vec3 camera = RenderHelper.getCamera().position();

        double x = pos.getX() - camera.x;
        double y = pos.getY() - camera.y;
        double z = pos.getZ() - camera.z;

        float min = 0f;
        float max = 1f;

        Matrix4f matrix = matrices.last().pose();

        drawLine(consumer, matrix, x+min,y+min,z+min, x+max,y+min,z+min, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+min,z+min, x+max,y+min,z+max, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+min,z+max, x+min,y+min,z+max, r,g,b,a, width);
        drawLine(consumer, matrix, x+min,y+min,z+max, x+min,y+min,z+min, r,g,b,a, width);

        drawLine(consumer, matrix, x+min,y+max,z+min, x+max,y+max,z+min, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+max,z+min, x+max,y+max,z+max, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+max,z+max, x+min,y+max,z+max, r,g,b,a, width);
        drawLine(consumer, matrix, x+min,y+max,z+max, x+min,y+max,z+min, r,g,b,a, width);

        drawLine(consumer, matrix, x+min,y+min,z+min, x+min,y+max,z+min, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+min,z+min, x+max,y+max,z+min, r,g,b,a, width);
        drawLine(consumer, matrix, x+max,y+min,z+max, x+max,y+max,z+max, r,g,b,a, width);
        drawLine(consumer, matrix, x+min,y+min,z+max, x+min,y+max,z+max, r,g,b,a,width);

        matrices.popPose();
    }

    private static void drawLine(
        VertexConsumer consumer,
        Matrix4f matrix,
        double x1, double y1, double z1,
        double x2, double y2, double z2,
        float r, float g, float b, float a,
        float width
    ) {
        float nx = (float)(x2 - x1);
        float ny = (float)(y2 - y1);
        float nz = (float)(z2 - z1);

        float len = (float)Math.sqrt(nx*nx + ny*ny + nz*nz);

        if (len != 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }

        consumer.addVertex(matrix, (float)x1, (float)y1, (float)z1)
            .setColor(r, g, b, a)
            .setNormal(nx, ny, nz)
            .setLineWidth(width);

        consumer.addVertex(matrix, (float)x2, (float)y2, (float)z2)
            .setColor(r, g, b, a)
            .setNormal(nx, ny, nz)
            .setLineWidth(width);
    }

}
