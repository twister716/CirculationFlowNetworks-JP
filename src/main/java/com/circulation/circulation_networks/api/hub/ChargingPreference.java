package com.circulation.circulation_networks.api.hub;

import net.minecraft.nbt.NBTTagCompound;

public class ChargingPreference {

    private boolean chargeInventory;
    private boolean chargeHotbar;
    private boolean chargeBaubles;
    private boolean chargeMainHand;
    private boolean chargeOffHand;
    private boolean chargeArmorSlot;

    public ChargingPreference(boolean chargeInventory, boolean chargeHotbar, boolean chargeBaubles,
                              boolean chargeMainHand, boolean chargeOffHand, boolean chargeArmorSlot) {
        this.chargeInventory = chargeInventory;
        this.chargeHotbar = chargeHotbar;
        this.chargeBaubles = chargeBaubles;
        this.chargeMainHand = chargeMainHand;
        this.chargeOffHand = chargeOffHand;
        this.chargeArmorSlot = chargeArmorSlot;
    }

    public static ChargingPreference defaultAll() {
        return new ChargingPreference(true, true, true, true, true, true);
    }

    public static ChargingPreference deserialize(NBTTagCompound nbt) {
        return new ChargingPreference(
            nbt.getBoolean("inv"),
            nbt.getBoolean("hotbar"),
            nbt.getBoolean("baubles"),
            nbt.getBoolean("mainHand"),
            nbt.getBoolean("offHand"),
            nbt.getBoolean("armorSlot")
        );
    }

    public boolean getPreference(ChargingDefinition cd) {
        return switch (cd) {
            case INVENTORY -> chargeInventory;
            case HOTBAR -> chargeHotbar;
            case BAUBLES -> chargeBaubles;
            case MAIN_HAND -> chargeMainHand;
            case OFF_HAND -> chargeOffHand;
            case ARMOR -> chargeArmorSlot;
        };
    }

    public void setPreference(ChargingDefinition cd, boolean value) {
        switch (cd) {
            case INVENTORY -> chargeInventory = value;
            case HOTBAR -> chargeHotbar = value;
            case BAUBLES -> chargeBaubles = value;
            case MAIN_HAND -> chargeMainHand = value;
            case OFF_HAND -> chargeOffHand = value;
            case ARMOR -> chargeArmorSlot = value;
        }
    }

    public NBTTagCompound serialize() {
        var nbt = new NBTTagCompound();
        nbt.setBoolean("inv", chargeInventory);
        nbt.setBoolean("hotbar", chargeHotbar);
        nbt.setBoolean("baubles", chargeBaubles);
        nbt.setBoolean("mainHand", chargeMainHand);
        nbt.setBoolean("offHand", chargeOffHand);
        nbt.setBoolean("armorSlot", chargeArmorSlot);
        return nbt;
    }
}
