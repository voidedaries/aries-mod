package dev.voidedaries.aries.client.gui.location;

public class GuiSnapHelper {

    private GuiSnapHelper() {}

    public static int snapX(int x, int width, int screenWidth, int snapDistance) {
        int thirdWidth = screenWidth / 3;

        int[] lines = {
            0,
            thirdWidth,
            thirdWidth * 2,
            screenWidth
        };

        int[] points = {
            x,
            x + width
        };

        return snap(x, points, lines, snapDistance);
    }

    public static int snapY(int y, int height, int screenHeight, int snapDistance) {
        int thirdHeight = screenHeight / 3;

        int[] lines = {
            0,
            thirdHeight,
            thirdHeight * 2,
            screenHeight
        };

        int[] points = {
            y,
            y + height
        };

        return snap(y, points, lines, snapDistance);
    }

    private static int snap(int position, int[] points, int[] lines, int snapDistance) {
        int closestDistance = snapDistance;
        int snappedPosition = position;

        for (int point : points) {
            for (int line : lines) {
                int distance = Math.abs(point - line);

                if (distance <= closestDistance) {
                    closestDistance = distance;
                    snappedPosition = position + (line - point);
                }
            }
        }

        return snappedPosition;
    }

}
