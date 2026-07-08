package dev.voidedaries.aries.client.feature.config.types;

public interface SliderConfig {
    float getMin();
    float getMax();

    float getAsFloat();
    void setFromPercent(float percent);
}
