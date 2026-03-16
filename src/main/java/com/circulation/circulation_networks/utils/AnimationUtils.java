package com.circulation.circulation_networks.utils;

public final class AnimationUtils {

    private AnimationUtils() {
    }

    public static float easeOutCubic(float progress) {
        float clamped = Math.max(0.0f, Math.min(progress, 1.0f));
        return (float) (1.0 - Math.pow(1.0 - clamped, 3.0));
    }

    public static float advanceTowardsOne(float current, float delta) {
        return Math.min(current + delta, 1.0f);
    }
}