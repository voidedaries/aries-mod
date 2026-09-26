package dev.voidedaries.aries.client.gui.location;

public class HudPosition {

    private int x;
    private int y;
    private float scale = 1.0f;

    public HudPosition() {
        this(0, 0);
    }

    public HudPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getScale() {
        return scale;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}