package com.circulation.circulation_networks.api.node;

//? if <1.20 {
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.core.BlockPos;
*///?}

/**
 * 标识符，确定节点可用于向玩家传输能量
 */
public interface IChargingNode extends INode {

    double getChargingScope();

    /**
     * 返回充能范围的平方，用于距离检测。<br>
     * 实现类应缓存此值以避免每次调用时重复乘法运算。
     */
    default double getChargingScopeSq() {
        double s = getChargingScope();
        return s * s;
    }

    default boolean chargingScopeCheck(BlockPos pos) {
        return this.distanceSq(pos) <= getChargingScopeSq();
    }

}
