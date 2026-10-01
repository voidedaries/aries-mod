package dev.voidedaries.aries.client.render.gif;

import java.awt.image.BufferedImage;
import java.util.List;

public record GifAnimation(List<GifFrame> frames) {

    public GifAnimation(List<GifFrame> frames) {
        this.frames = List.copyOf(frames);
    }

    public GifFrame getFrame(int index) {
        return frames.get(index);
    }

    public int getFrameIndexAtTime(long elapsedMillis) {
        if (frames.isEmpty()) {
            throw new IllegalStateException("GIF animation has no frames");
        }

        long animationDuration = 0;

        for (GifFrame frame : frames) {
            animationDuration += frame.getDelayMillis();
        }

        long animationTime = elapsedMillis % animationDuration;

        long frameStart = 0;

        for (int i = 0; i < frames.size(); i++) {
            GifFrame frame = frames.get(i);
            long frameEnd = frameStart + frame.getDelayMillis();

            if (animationTime < frameEnd) {
                return i;
            }

            frameStart = frameEnd;
        }

        return frames.size() - 1;
    }

    public record GifFrame(BufferedImage image, int delay) {

        public long getDelayMillis() {
            return delay * 10L;
        }

    }

}
