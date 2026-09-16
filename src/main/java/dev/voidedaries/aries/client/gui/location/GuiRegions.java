package dev.voidedaries.aries.client.gui.location;

public enum GuiRegions {

    TOP_LEFT(new GuiBounds(0.0, 0.0, 1.0 / 3.0, 1.0 / 3.0)),
    TOP_MIDDLE(new GuiBounds(1.0 / 3.0, 0.0, 2.0 / 3.0, 1.0 / 3.0)),
    TOP_RIGHT(new GuiBounds(2.0 / 3.0, 0.0, 1.0, 1.0 / 3.0)),

    MIDDLE_LEFT(new GuiBounds(0.0, 1.0 / 3.0, 1.0 / 3.0, 2.0 / 3.0)),
    CENTER(new GuiBounds(1.0 / 3.0, 1.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0)),
    MIDDLE_RIGHT(new GuiBounds(2.0 / 3.0, 1.0 / 3.0, 1.0, 2.0 / 3.0)),

    BOTTOM_LEFT(new GuiBounds(0.0, 2.0 / 3.0, 1.0 / 3.0, 1.0)),
    BOTTOM_MIDDLE(new GuiBounds(1.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 1.0)),
    BOTTOM_RIGHT(new GuiBounds(2.0 / 3.0, 2.0 / 3.0, 1.0, 1.0));

    private final GuiBounds region;

    GuiRegions(GuiBounds region) {
        this.region = region;
    }

    public GuiBounds getRegion() {
        return region;
    }
}
