package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import net.minecraft.util.Mth;

public class AriesScreenHelper {

    public static AriesCategory lastCategory = AriesCategory.ABOUT;
    public static int savedScrollPosition = 0;

    private AriesScreenHelper() {}

    public static int calculateThumbHeight(int scrollbarHeight, int contentHeight, int visibleHeight) {
        if (contentHeight <= 0 || contentHeight <= visibleHeight) {
            return scrollbarHeight;
        }

        float visibleRatio = visibleHeight / (float) contentHeight;

        return Math.clamp((int) (scrollbarHeight * visibleRatio), 20, scrollbarHeight);
    }

    //Scrollbar methods
    public static int calculateThumbY(
        int scrollbarTop,
        int scrollbarHeight,
        int thumbHeight,
        int scrollOffset,
        int contentHeight,
        int visibleHeight
    ) {
        int scrollRange = contentHeight - visibleHeight;
        int thumbRange = scrollbarHeight - thumbHeight;

        if (scrollRange <= 0) {
            return scrollbarTop;
        }

        return scrollbarTop +
            (int)((scrollOffset / (float) scrollRange) * thumbRange);
    }

    public static int calculateScrollbarHeight(int visibleHeight, int padding) {
        return visibleHeight - (padding * 2);
    }

    public static int getScrollbarTop(int screenHeight, int menuHeight, int padding, int fontHeight) {
        int menuY = ScreenHelper.centreY(screenHeight, menuHeight);

        return (int) (menuY + padding + fontHeight + padding * 1.5);
    }

    public static int getContentVisibleHeight(
        int screenHeight,
        int menuHeight,
        int padding,
        int fontHeight
    ) {
        int menuY = ScreenHelper.centreY(screenHeight, menuHeight);

        int contentY = (int) (
            menuY
                + padding
                + fontHeight
                + padding * 1.5
        );

        int contentTop = contentY - padding;
        int contentBottom = menuY + menuHeight;

        return contentBottom - contentTop;
    }

    public static int getMaxScroll(int contentHeight, int visibleHeight) {
        return Math.max(0, contentHeight - visibleHeight);
    }

    public static int calculateScrollOffsetFromDrag(
        double mouseY,
        int scrollbarOffset,
        int scrollbarTop,
        int scrollbarHeight,
        int thumbHeight,
        int contentHeight,
        int visibleHeight
    ) {
        int thumbRange = scrollbarHeight - thumbHeight;

        if (thumbRange <= 0) {
            return 0;
        }

        float percent = ((float) mouseY - scrollbarOffset - scrollbarTop) / thumbRange;
        percent = Mth.clamp(percent, 0f, 1f);

        int maxScroll = getMaxScroll(contentHeight, visibleHeight);

        return (int) (percent * maxScroll);
    }

    public static int clampScroll(
        int scrollOffset,
        int contentHeight,
        int visibleHeight
    ) {
        int maxScroll = contentHeight - visibleHeight;

        return Mth.clamp(
            scrollOffset,
            0,
            Math.max(0, maxScroll)
        );
    }

}
