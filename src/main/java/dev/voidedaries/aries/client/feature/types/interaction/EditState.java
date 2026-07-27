package dev.voidedaries.aries.client.feature.types.interaction;

public abstract class EditState {

    protected String input;

    private long lastBlink = System.currentTimeMillis();
    private boolean caretVisible = true;

    public EditState(String initialValue) {
        this.input = initialValue;
    }

    protected static String formatInput(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }

        return String.valueOf(value);
    }

    public String getInput() {
        return input;
    }

    public void addChar(char c) {
        input += c;
        resetBlink();
    }

    public void backspace() {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            resetBlink();
        }
    }

    public boolean showCaret() {
        if (System.currentTimeMillis() - lastBlink > 500) {
            caretVisible = !caretVisible;
            lastBlink = System.currentTimeMillis();
        }

        return caretVisible;
    }

    private void resetBlink() {
        lastBlink = System.currentTimeMillis();
        caretVisible = true;
    }

    public abstract boolean apply();

}
