package com.circulation.circulation_networks.client.render;

public final class NodeRotationAnimation {

    private static final float DEGREES_PER_ROTATION = 360.0F;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final float RELAY_TOP_SPIRAL_DEGREES_PER_TICK = degreesPerTickForPeriodSeconds(10.0F);
    private static final float RELAY_CRYSTAL_DEGREES_PER_TICK = degreesPerTickForPeriodSeconds(40.0F);
    private static final float RELAY_RING_DEGREES_PER_TICK = degreesPerTickForPeriodSeconds(20.0F);
    private static final float PEDESTAL_ROTATION_DEGREES_PER_TICK = 1.25F;

    private static final float BOBBING_PERIOD_TICKS = 80.0F;
    private static final float BOBBING_ANGULAR_VELOCITY = (float) (2.0 * Math.PI / BOBBING_PERIOD_TICKS);
    private static final float BOBBING_AMPLITUDE = 0.75F / 16.0F;

    private static final float SIN_22_5 = 0.38268343F;
    private static final float COS_22_5 = 0.9238795F;

    private NodeRotationAnimation() {
    }

    public static float relayTopSpiralAngle(long worldTime, float partialTicks) {
        return clockwiseDegrees(worldTime, partialTicks, RELAY_TOP_SPIRAL_DEGREES_PER_TICK);
    }

    public static float relayCrystalAngle(long worldTime, float partialTicks) {
        return clockwiseDegrees(worldTime, partialTicks, RELAY_CRYSTAL_DEGREES_PER_TICK);
    }

    public static float relayBottomSpiralAngle(long worldTime, float partialTicks) {
        return counterClockwiseDegrees(worldTime, partialTicks, RELAY_TOP_SPIRAL_DEGREES_PER_TICK);
    }

    public static float pedestalClockwiseFrameAngle(long worldTime, float partialTicks) {
        return clockwiseDegrees(worldTime, partialTicks, PEDESTAL_ROTATION_DEGREES_PER_TICK);
    }

    public static float pedestalCounterClockwiseFrameAngle(long worldTime, float partialTicks) {
        return counterClockwiseDegrees(worldTime, partialTicks, PEDESTAL_ROTATION_DEGREES_PER_TICK);
    }

    public static float tiltedAxisXForZRotation(float zRotationDegrees) {
        if (zRotationDegrees < 0.0F) {
            return SIN_22_5;
        }
        return -SIN_22_5;
    }

    public static float tiltedAxisY() {
        return COS_22_5;
    }

    public static float tiltedAxisZ() {
        return 0.0F;
    }

    public static float bobOffset(long worldTime, float partialTicks) {
        return (float) Math.sin((worldTime + partialTicks) * BOBBING_ANGULAR_VELOCITY) * BOBBING_AMPLITUDE;
    }

    private static float clockwiseDegrees(long worldTime, float partialTicks, float speed) {
        return -rotationDegrees(worldTime, partialTicks, speed);
    }

    private static float counterClockwiseDegrees(long worldTime, float partialTicks, float speed) {
        return rotationDegrees(worldTime, partialTicks, speed);
    }

    private static float rotationDegrees(long worldTime, float partialTicks, float speed) {
        return (worldTime + partialTicks) * speed;
    }

    private static float degreesPerTickForPeriodSeconds(float secondsPerRotation) {
        return DEGREES_PER_ROTATION / (secondsPerRotation * TICKS_PER_SECOND);
    }
}
