package com.circulation.circulation_networks.tiles;

import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public final class TileEntityNodePedestal extends BaseTileEntity {

    @Override
    public @NotNull AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2));
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 4096.0D;
    }

    @Override
    public boolean canRenderBreaking() {
        return false;
    }
}
