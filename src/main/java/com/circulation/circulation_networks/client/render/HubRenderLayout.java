package com.circulation.circulation_networks.client.render;

import com.circulation.circulation_networks.math.Vec3i;

public final class HubRenderLayout {

    public static final String HUB_BASE_MODEL = "hub/hub_base";
    public static final String HUB_EMISSIVE_MODEL = "hub/hub_e";
    public static final String HUB_CRYSTAL_MODEL = "hub/hub_crystal";
    public static final String RING_UP_BASE_MODEL = "hub/ring_up_base";
    public static final String RING_UP_EMISSIVE_MODEL = "hub/ring_up_e";
    public static final String RING_DOWN_BASE_MODEL = "hub/ring_down_base";
    public static final String RING_DOWN_EMISSIVE_MODEL = "hub/ring_down_e";
    public static final String CHANNEL_BEACON_TOP_INSIDE_MODEL = "hub/channel/beacon_top_inside_e";
    public static final String CHANNEL_BEACON_TOP_OUTSIDE_MODEL = "hub/channel/beacon_top_outside_e";
    public static final String CHANNEL_BEACON_MID_INSIDE_MODEL = "hub/channel/beacon_mid_inside_e";
    public static final String CHANNEL_BEACON_MID_OUTSIDE_MODEL = "hub/channel/beacon_mid_outside_e";
    public static final String CHANNEL_BEACON_DOWN_INSIDE_MODEL = "hub/channel/beacon_down_inside_e";
    public static final String CHANNEL_BEACON_DOWN_OUTSIDE_MODEL = "hub/channel/beacon_down_outside_e";
    public static final String CHANNEL_HOLA_TOP_MODEL = "hub/channel/hola_2_e";
    public static final String CHANNEL_HOLA_MIDDLE_MODEL = "hub/channel/hola_1_e";
    public static final String CHANNEL_HOLA_BOTTOM_MODEL = "hub/channel/hola_0_e";
    public static final String CHANNEL_RING_AERIALS_BASE_MODEL = "hub/channel/ring_aerials_base";
    public static final String CHANNEL_RING_AERIALS_EMISSIVE_MODEL = "hub/channel/ring_aerials_e";
    public static final String EMPTY_PLUGIN_MODEL = "hub/plug/empty";
    public static final String DEFAULT_PLUGIN_MODEL_PREFIX = "hub/plug/default_";
    public static final String WIDE_AREA_PLUGIN_MODEL = "hub/plug/multidimensional_link_antenna";
    public static final String DIMENSIONAL_PLUGIN_MODEL = "hub/plug/dimensional_link_antenna";
    private static final double HUB_CRYSTAL_Y_OFFSET = 3.0D;
    private static final double HUB_RING_Y_OFFSET = 3.0D;
    private static final double HUB_CHANNEL_Y_OFFSET = 5.0D;
    private static final double HUB_CHANNEL_BEAM_DOWN_Y_OFFSET = 6.0D;
    private static final double HUB_CHANNEL_BEAM_MID_Y_OFFSET = 9.0D;
    private static final double HUB_CHANNEL_BEAM_TOP_Y_OFFSET = 12.0D;
    private static final int HUB_RENDER_BOUNDS_MIN_XZ = -1;
    private static final int HUB_RENDER_BOUNDS_MAX_XZ = 2;
    private static final int HUB_RENDER_BOUNDS_MIN_Y = 0;
    private static final int HUB_RENDER_BOUNDS_MAX_Y = 8;
    private static final Vec3i plugin1 = new Vec3i(-1, 2, -1);
    private static final Vec3i plugin2 = new Vec3i(1, 2, -1);
    private static final Vec3i plugin3 = new Vec3i(1, 2, 1);
    private static final Vec3i plugin4 = new Vec3i(-1, 2, 1);
    private static final Vec3i[] SHELL_OFFSETS;

    static {
        SHELL_OFFSETS = new Vec3i[23];
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) {
                        if (dy == 1 || dy == 0) {
                            continue;
                        }
                    }
                    SHELL_OFFSETS[i++] = new Vec3i(dx, dy, dz);
                }
            }
        }
        SHELL_OFFSETS[i++] = new Vec3i(0, 2, 0);
        SHELL_OFFSETS[i++] = new Vec3i(0, 3, 0);
        SHELL_OFFSETS[i++] = new Vec3i(0, 4, 0);
        SHELL_OFFSETS[i++] = plugin1;
        SHELL_OFFSETS[i++] = plugin2;
        SHELL_OFFSETS[i++] = plugin3;
        SHELL_OFFSETS[i] = plugin4;
    }

    private HubRenderLayout() {
    }

    public static int defaultPluginModelIndex(int x, int y, int z) {
        return Math.floorMod(x + y + z, 4);
    }

    public static Vec3i cornerOffsetForSlot(int slot) {
        return switch (slot) {
            case 1 -> plugin1;
            case 2 -> plugin2;
            case 3 -> plugin3;
            case 4 -> plugin4;
            default -> throw new IllegalArgumentException("Unsupported hub corner slot: " + slot);
        };
    }

    public static boolean isClockwiseCornerSlot(int slot) {
        return slot == 1 || slot == 3;
    }

    public static Vec3i[] shellOffsets() {
        return SHELL_OFFSETS;
    }

    public static double crystalYOffset() {
        return HUB_CRYSTAL_Y_OFFSET;
    }

    public static double ringYOffset() {
        return HUB_RING_Y_OFFSET;
    }

    public static double channelYOffset() {
        return HUB_CHANNEL_Y_OFFSET;
    }

    public static double channelBeamDownYOffset() {
        return HUB_CHANNEL_BEAM_DOWN_Y_OFFSET;
    }

    public static double channelBeamMidYOffset() {
        return HUB_CHANNEL_BEAM_MID_Y_OFFSET;
    }

    public static double channelBeamTopYOffset() {
        return HUB_CHANNEL_BEAM_TOP_Y_OFFSET;
    }

    public static int renderBoundsMinXZ() {
        return HUB_RENDER_BOUNDS_MIN_XZ;
    }

    public static int renderBoundsMaxXZ() {
        return HUB_RENDER_BOUNDS_MAX_XZ;
    }

    public static int renderBoundsMinY() {
        return HUB_RENDER_BOUNDS_MIN_Y;
    }

    public static int renderBoundsMaxY() {
        return HUB_RENDER_BOUNDS_MAX_Y;
    }
}
