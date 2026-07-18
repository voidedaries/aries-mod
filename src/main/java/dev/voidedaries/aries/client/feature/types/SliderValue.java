package dev.voidedaries.aries.client.feature.types;

public interface SliderValue {

    float getMin();
    float getMax();

    float getAsFloat();

    float getStep();

    void setFromPercent(float percent);

    default void step(int direction) {
        float value = getAsFloat() + direction * getStep();
        float percent = (value - getMin()) / (getMax() - getMin());

        setFromPercent(percent);
    }
}
