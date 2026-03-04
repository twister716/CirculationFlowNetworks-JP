package com.circulation.circulation_networks;

import net.minecraftforge.common.config.Config;

@Config(modid = CirculationFlowNetworks.MOD_ID)
public final class CFNConfig {

    @Config.Name("Node")
    public static final Node NODE = new Node();

    @Config.Name("EnergyBlacklist")
    @Config.Comment({
        "能源实体黑名单（完全限定名或类名前缀）",
        "黑名单中的TileEntity不会被识别为能源容器",
        "示例：",
        "  - 'com.example.CustomEnergyTile' 精确匹配",
        "  - 'com.example' 前缀匹配，包含所有以此开头的类",
        "",
        "Energy entity blacklist (fully qualified name or class name prefix)",
        "TileEntitys in the blacklist will not be recognized as energy containers",
        "Examples:",
        "  - 'com.example.CustomEnergyTile' Exact match",
        "  - 'com.example' Prefix match, includes all classes starting with this"
    })
    public static String[] classNames = new String[]{"sonar.fluxnetworks.common.tileentity.TileFluxCore"};

    public static class Node {

        @Config.Name("EnergyInductionTower")
        public final EnergyInductionTowerConfig energyInductionTower = new EnergyInductionTowerConfig();

        @Config.Name("ElectromagneticInductionTower")
        public final ElectromagneticInductionTowerConfig electromagneticInductionTower = new ElectromagneticInductionTowerConfig();

        @Config.Name("Hub")
        public final HubConfig hub = new HubConfig();

        public static class HubConfig {
            @Config.Comment({"中枢的能量范围", "Energy range of Hub"})
            @Config.Name("energyScope")
            @Config.RangeDouble(min = 1, max = 32)
            public double energyScope = 10;

            @Config.Comment({"中枢的充能范围", "Charging range of Hub"})
            @Config.Name("chargingScope")
            @Config.RangeDouble(min = 1, max = 32)
            public double chargingScope = 8;

            @Config.Comment({"中枢的链接范围", "Link range of Hub"})
            @Config.Name("linkScope")
            @Config.RangeDouble(min = 1, max = 32)
            public double linkScope = 16;
        }

        public static class EnergyInductionTowerConfig {
            @Config.Comment({"能量感应塔的能量范围", "Energy range of Energy Induction Tower "})
            @Config.Name("energyScope")
            @Config.RangeDouble(min = 0.1, max = 32)
            public double energyScope = 8;

            @Config.Comment({"能量感应塔的链接范围", "Link range of Energy Induction Tower"})
            @Config.Name("linkScope")
            @Config.RangeDouble(min = 0.1, max = 32)
            public double linkScope = 12;
        }

        public static class ElectromagneticInductionTowerConfig {
            @Config.Comment({"电磁感应塔的充能范围", "Charging range of Electromagnetic Induction Tower"})
            @Config.Name("chargingScope")
            @Config.RangeDouble(min = 1, max = 32)
            public double chargingScope = 5;

            @Config.Comment({"电磁感应塔的链接范围", "Link range of Electromagnetic Induction Tower "})
            @Config.Name("linkScope")
            @Config.RangeDouble(min = 1, max = 32)
            public double linkScope = 8;
        }
    }

}
