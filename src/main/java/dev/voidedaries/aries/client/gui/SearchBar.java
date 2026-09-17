package dev.voidedaries.aries.client.gui;

public class SearchBar {
    private static final int MAX_CHAR_LENGTH = 50;

    private final StringBuilder text = new StringBuilder();
    private boolean focused;
    private int cursorPosition;
    private int selectionAnchor;
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
        this.text.append(text, 0, Math.min(text.length(), MAX_CHAR_LENGTH));

        cursorPosition = this.text.length();
        selectionAnchor = cursorPosition;
        resetCursorBlink();
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public int getSelectionStart() {
        return Math.min(cursorPosition, selectionAnchor);
    }

    public int getSelectionEnd() {
        return Math.max(cursorPosition, selectionAnchor);
    }

    public boolean hasSelection() {
        return cursorPosition != selectionAnchor;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void updateScrollOffset(int textWidth, int visibleWidth) {
        scrollOffset = Math.max(0, textWidth - visibleWidth);
    }

    public void append(char character) {
        if (hasSelection()) {
            deleteSelection();
        }

        if (text.length() < MAX_CHAR_LENGTH) {
            text.insert(cursorPosition, character);
            cursorPosition++;
            selectionAnchor = cursorPosition;
            resetCursorBlink();
        }
    }

    public void backspace() {
        if (hasSelection()) {
            deleteSelection();
            return;
        }

        if (cursorPosition > 0) {
            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            selectionAnchor = cursorPosition;
        }

        resetCursorBlink();
    }

    public void delete() {
        if (hasSelection()) {
            deleteSelection();
            return;
        }

        if (cursorPosition < text.length()) {
            text.deleteCharAt(cursorPosition);
        }

        resetCursorBlink();
    }

    public void moveCursor(int direction, boolean selecting) {
        if (hasSelection() && !selecting) {
            cursorPosition = direction < 0 ? getSelectionStart() : getSelectionEnd();

            selectionAnchor = cursorPosition;
            resetCursorBlink();
            return;
        }

        cursorPosition = Math.clamp(cursorPosition + direction, 0, text.length());

        if (!selecting) {
            selectionAnchor = cursorPosition;
        }

        resetCursorBlink();
    }

    public void moveCursorByWord(int direction, boolean selecting) {
        int position = cursorPosition;

        if (direction < 0) {
            while (position > 0 && Character.isWhitespace(text.charAt(position - 1))) {
                position--;
            }

            while (position > 0 && !Character.isWhitespace(text.charAt(position - 1))) {
                position--;
            }
        } else {
            while (position < text.length() && Character.isWhitespace(text.charAt(position))) {
                position++;
            }

            while (position < text.length() && !Character.isWhitespace(text.charAt(position))) {
                position++;
            }
        }

        cursorPosition = position;

        if (!selecting) {
            selectionAnchor = cursorPosition;
        }

        resetCursorBlink();
    }

    public void selectAll() {
        selectionAnchor = 0;
        cursorPosition = text.length();
        resetCursorBlink();
    }

    public boolean hasQuery() {
        return !text.isEmpty();
    }

    public void deleteSelection() {
        if (!hasSelection()) {
            return;
        }

        int start = getSelectionStart();
        int end = getSelectionEnd();

        text.delete(start, end);

        cursorPosition = start;
        selectionAnchor = start;

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
