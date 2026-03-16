package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.node.IChargingNode;
import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
import com.circulation.circulation_networks.api.node.INode;
//? if <1.20 {
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
*///?}

public final class InspectionTargetSnapshot {

    private final BlockPos pos;
    private final IGrid grid;
    private final double linkScope;
    private final double energyScope;
    private final double chargingScope;

    private InspectionTargetSnapshot(BlockPos pos, IGrid grid, double linkScope, double energyScope, double chargingScope) {
        this.pos = pos;
        this.grid = grid;
        this.linkScope = linkScope;
        this.energyScope = energyScope;
        this.chargingScope = chargingScope;
    }

    public static InspectionTargetSnapshot fromNode(INode node) {
        double energyScope = 0;
        double chargingScope = 0;
        if (node instanceof IEnergySupplyNode energySupplyNode) {
            energyScope = energySupplyNode.getEnergyScope();
        }
        if (node instanceof IChargingNode chargingNode) {
            chargingScope = chargingNode.getChargingScope();
        }
        return new InspectionTargetSnapshot(node.getPos(), node.getGrid(), node.getLinkScope(), energyScope, chargingScope);
    }

    public BlockPos pos() {
        return pos;
    }

    public IGrid grid() {
        return grid;
    }

    public double linkScope() {
        return linkScope;
    }

    public double energyScope() {
        return energyScope;
    }

    public double chargingScope() {
        return chargingScope;
    }
}