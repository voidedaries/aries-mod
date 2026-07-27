package dev.voidedaries.aries.client.gui;

public class SearchBar {
    private final StringBuilder text = new StringBuilder();
    private boolean focused;
    private int scrollOffset;

    private int cursorTicks;

    public void tick() {
        cursorTicks++;
    }

    public boolean shouldShowCursor() {
        return focused && (cursorTicks / 10) % 2 == 0;
    }

    public void resetCursorBlink() {
        cursorTicks = 0;
    }

    public String getText() {
        return text.toString();
    }

    public void setText(String text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = Math.max(0, scrollOffset);
    }

    public void updateScrollOffset(int textWidth, int visibleWidth) {
        scrollOffset = Math.max(0, textWidth - visibleWidth);
    }

    public void append(char character) {
        int MAX_CHAR_LENGTH = 40;
        if (text.length() < MAX_CHAR_LENGTH) {
            text.append(character);
            resetCursorBlink();
        }
    }

    public void backspace() {
        if (!text.isEmpty()) {
            text.deleteCharAt(text.length() - 1);
        }

        resetCursorBlink();
    }

    public boolean hasQuery() {
        return !text.isEmpty();
    }

    public void clear() {
        text.setLength(0);
        resetCursorBlink();
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        resetCursorBlink();
    }

}
