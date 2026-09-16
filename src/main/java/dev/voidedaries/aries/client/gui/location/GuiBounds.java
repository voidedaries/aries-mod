package dev.voidedaries.aries.client.gui.location;

public class GuiBounds {

    private final double topLeftX;
    private final double topLeftY;
    private final double bottomRightX;
    private final double bottomRightY;

    public GuiBounds(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
    }

    public boolean contains(double x, double y) {
        return x >= topLeftX && x <= bottomRightX && y >= topLeftY && y <= bottomRightY;
    }

    public GuiBounds toScreenBounds(double width, double height) {
        return new GuiBounds(
            topLeftX * width,
            topLeftY * height,
            bottomRightX * width,
            bottomRightY * height
        );
    }

    public double getNearestSnapX(double x, double snapDistance) {
        double[] snapX = {
            topLeftX,
            (topLeftX + bottomRightX) / 2.0,
            bottomRightX
        };

        double nearestX = x;
        double closestDistance = snapDistance;

        for (double snap : snapX) {
            double distance = Math.abs(x - snap);

            if (distance <= closestDistance) {
                closestDistance = distance;
                nearestX = snap;
            }
        }

        return nearestX;
    }

    public double getNearestSnapY(double y, double snapDistance) {
        double[] snapY = {
            topLeftY,
            (topLeftY + bottomRightY) / 2.0,
            bottomRightY
        };

        double nearestY = y;
        double closestDistance = snapDistance;

        for (double snap : snapY) {
            double distance = Math.abs(y - snap);

            if (distance <= closestDistance) {
                closestDistance = distance;
                nearestY = snap;
            }
        }

        return nearestY;
    }
}