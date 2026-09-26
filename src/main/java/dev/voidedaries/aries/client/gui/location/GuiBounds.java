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

    public double getTopLeftX() {
        return topLeftX;
    }

    public double getTopLeftY() {
        return topLeftY;
    }

    public double getBottomRightX() {
        return bottomRightX;
    }

    public double getBottomRightY() {
        return bottomRightY;
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
}