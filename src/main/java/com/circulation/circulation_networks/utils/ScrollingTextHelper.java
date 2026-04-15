package com.circulation.circulation_networks.utils;

public final class ScrollingTextHelper {

    private static final float SCROLL_SPEED = 1.0F;
    private static final float PAUSE_TICKS = 40.0F;

    private ScrollingTextHelper() {
    }

    public static float getScrollOffset(int textWidth, int maxWidth, long tick, float partialTick) {
        if (textWidth <= maxWidth) return 0;
        float overflow = textWidth - maxWidth;
        float scrollDuration = overflow / SCROLL_SPEED;
        float cycle = PAUSE_TICKS + scrollDuration + PAUSE_TICKS + scrollDuration;
        long cycleL = (long) Math.ceil(cycle);
        float time = (tick % Math.max(cycleL, 1)) + partialTick;
        float phase = time % cycle;
        if (phase < PAUSE_TICKS) return 0;
        phase -= PAUSE_TICKS;
        if (phase < scrollDuration) return phase * SCROLL_SPEED;
        phase -= scrollDuration;
        if (phase < PAUSE_TICKS) return overflow;
        phase -= PAUSE_TICKS;
        return overflow - phase * SCROLL_SPEED;
    }
}
