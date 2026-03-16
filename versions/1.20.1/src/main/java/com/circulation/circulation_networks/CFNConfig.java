package com.circulation.circulation_networks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

public final class CFNConfig {

    public static String[] classNames = new String[0];
    public static String[] supplyClassNames = new String[0];
    public static final Node NODE = new Node();

    static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CLASS_NAMES;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SUPPLY_CLASS_NAMES;
    private static final ForgeConfigSpec.DoubleValue HUB_ENERGY_SCOPE;
    private static final ForgeConfigSpec.DoubleValue HUB_CHARGING_SCOPE;
    private static final ForgeConfigSpec.DoubleValue HUB_LINK_SCOPE;
    private static final ForgeConfigSpec.DoubleValue EIT_ENERGY_SCOPE;
    private static final ForgeConfigSpec.DoubleValue EIT_LINK_SCOPE;
    private static final ForgeConfigSpec.DoubleValue EMIT_CHARGING_SCOPE;
    private static final ForgeConfigSpec.DoubleValue EMIT_LINK_SCOPE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        CLASS_NAMES = builder.comment(
                "Energy entity blacklist (fully qualified name or class name prefix)",
                "BlockEntities in the blacklist will not be recognized as energy containers",
                "Examples:",
                "  - 'com.example.CustomEnergyTile' Exact match",
                "  - 'com.example' Prefix match, includes all classes starting with this"
        ).defineListAllowEmpty(List.of("classNames"), List::of, obj -> obj instanceof String);

        SUPPLY_CLASS_NAMES = builder.comment(
                "Energy device blacklist for generic supply nodes (non-specialized).",
                "Matched devices can ONLY be connected by specialized nodes that override isBlacklisted.",
                "Examples:",
                "  - 'com.example.AdvancedEnergyTile' Exact match",
                "  - 'com.example.advanced' Prefix match"
        ).defineListAllowEmpty(List.of("supplyClassNames"), List::of, obj -> obj instanceof String);

        builder.push("Node");

        builder.push("Hub");
        HUB_ENERGY_SCOPE = builder.comment("Energy range of Hub").defineInRange("energyScope", 10.0, 1.0, 32.0);
        HUB_CHARGING_SCOPE = builder.comment("Charging range of Hub").defineInRange("chargingScope", 8.0, 1.0, 32.0);
        HUB_LINK_SCOPE = builder.comment("Link range of Hub").defineInRange("linkScope", 16.0, 1.0, 32.0);
        builder.pop();

        builder.push("EnergyInductionTower");
        EIT_ENERGY_SCOPE = builder.comment("Energy range of Energy Induction Tower").defineInRange("energyScope", 8.0, 0.1, 32.0);
        EIT_LINK_SCOPE = builder.comment("Link range of Energy Induction Tower").defineInRange("linkScope", 12.0, 0.1, 32.0);
        builder.pop();

        builder.push("ElectromagneticInductionTower");
        EMIT_CHARGING_SCOPE = builder.comment("Charging range of Electromagnetic Induction Tower").defineInRange("chargingScope", 5.0, 1.0, 32.0);
        EMIT_LINK_SCOPE = builder.comment("Link range of Electromagnetic Induction Tower").defineInRange("linkScope", 8.0, 1.0, 32.0);
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }

    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) syncFromSpec();
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) syncFromSpec();
    }

    private static void syncFromSpec() {
        classNames = CLASS_NAMES.get().stream().map(Object::toString).toArray(String[]::new);
        supplyClassNames = SUPPLY_CLASS_NAMES.get().stream().map(Object::toString).toArray(String[]::new);
        NODE.hub.energyScope = HUB_ENERGY_SCOPE.get();
        NODE.hub.chargingScope = HUB_CHARGING_SCOPE.get();
        NODE.hub.linkScope = HUB_LINK_SCOPE.get();
        NODE.energyInductionTower.energyScope = EIT_ENERGY_SCOPE.get();
        NODE.energyInductionTower.linkScope = EIT_LINK_SCOPE.get();
        NODE.electromagneticInductionTower.chargingScope = EMIT_CHARGING_SCOPE.get();
        NODE.electromagneticInductionTower.linkScope = EMIT_LINK_SCOPE.get();
    }

    public static class Node {
        public final HubConfig hub = new HubConfig();
        public final EnergyInductionTowerConfig energyInductionTower = new EnergyInductionTowerConfig();
        public final ElectromagneticInductionTowerConfig electromagneticInductionTower = new ElectromagneticInductionTowerConfig();

        public static class HubConfig {
            public double energyScope = 10;
            public double chargingScope = 8;
            public double linkScope = 16;
        }

        public static class EnergyInductionTowerConfig {
            public double energyScope = 8;
            public double linkScope = 12;
        }

        public static class ElectromagneticInductionTowerConfig {
            public double chargingScope = 5;
            public double linkScope = 8;
        }
    }
}
