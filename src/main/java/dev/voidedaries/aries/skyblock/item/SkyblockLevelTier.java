package dev.voidedaries.aries.skyblock.item;

public enum SkyblockLevelTier {

    RAINBOW(520, new SkyblockLevelGradient(0x69FF28, 0x08802C, 0x69FF28)),
    TEST1(560, null),
    TEST2(600, null),
    TEST3(640, null),
    TEST4(680, null),
    TEST5(720, null),
    TEST6(760, null),
    TEST7(800, null),
    TEST8(840, null),
    TEST9(880, null),
    TEST10(920, null),
    TEST11(960, null),
    TEST12(1000, null);

    private final int minimumLevel;
    private final SkyblockLevelGradient gradient;

    SkyblockLevelTier(int minimumLevel, SkyblockLevelGradient gradient) {
        this.minimumLevel = minimumLevel;
        this.gradient = gradient;
    }

    public static SkyblockLevelTier getForLevel(int level) {
        SkyblockLevelTier result = null;

        for (SkyblockLevelTier tier : values()) {
            if (level < tier.minimumLevel) {
                break;
            }

            result = tier;
        }

        return result;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public SkyblockLevelGradient getGradient() {
        return gradient;
    }
}
