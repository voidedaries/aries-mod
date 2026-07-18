package dev.voidedaries.aries.client.feature.config;

public interface SliderValue {
    float getMin();
    float getMax();

    float getAsFloat();
    void setFromPercent(float percent);
}
