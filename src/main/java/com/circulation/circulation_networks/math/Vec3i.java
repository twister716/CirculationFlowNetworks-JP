package com.circulation.circulation_networks.math;

import org.jetbrains.annotations.NotNull;

public record Vec3i(int x, int y, int z) {

    public double distanceSquared(double x, double y, double z) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distanceSquared(Vec3i other) {
        return distanceSquared(other.x, other.y, other.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vec3i(int x1, int y1, int z1))) {
            return false;
        }
        return x == x1 && y == y1 && z == z1;
    }

    @Override
    @NotNull
    public String toString() {
        return "Vec3i{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
