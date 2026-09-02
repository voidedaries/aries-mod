package dev.voidedaries.aries.skyblock.item;

public class SkyblockLevelGradient {

    private final int[] colors;

    public SkyblockLevelGradient(int... colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("A gradient requires at least two colors");
        }

        this.colors = colors;
    }

    public int getColor(double position) {
        int lastIndex = colors.length - 1;

        double scaledPosition = position * lastIndex;

        int firstIndex = (int) Math.floor(scaledPosition);
        int secondIndex = firstIndex + 1;

        if (firstIndex < 0) {
            firstIndex = 0;
        }

        if (secondIndex >= colors.length) {
            secondIndex = colors.length - 1;
        }

        double amount = scaledPosition - Math.floor(scaledPosition);

        return blend(
            colors[firstIndex],
            colors[secondIndex],
            amount
        );
    }

    public int getAnimatedColor(double position, long time) {
        double animation = (time % 2000L) / 2000.0;

        double animatedPosition = getOscillatingPosition(position + animation);

        return getColor(animatedPosition);
    }

    private static double getOscillatingPosition(double value) {
        value %= 2.0;

        if (value < 0.0) {
            value += 2.0;
        }

        return value <= 1.0 ? value : 2.0 - value;
    }

    private static int blend(int first, int second, double amount) {
        amount = smoothStep(amount);

        int red = (int) (getRed(first) * (1 - amount) + getRed(second) * amount);
        int green = (int) (getGreen(first) * (1 - amount) + getGreen(second) * amount);
        int blue = (int) (getBlue(first) * (1 - amount) + getBlue(second) * amount);

        return (red << 16) | (green << 8) | blue;
    }

    private static double smoothStep(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    private static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    private static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    private static int getBlue(int color) {
        return color & 0xFF;
    }

}
