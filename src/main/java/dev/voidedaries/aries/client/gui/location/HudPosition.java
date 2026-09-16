package dev.voidedaries.aries.client.gui.location;

public class HudPosition {

    private int x;
    private int y;

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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}