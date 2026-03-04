package com.circulation.circulation_networks.api.hub;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

@Getter
@Setter
public class ChargingPreference {

    private boolean chargeInventory;
    private boolean chargeHotbar;
    private boolean chargeBaubles;
    private boolean chargeMainHand;
    private boolean chargeOffHand;

    public ChargingPreference(boolean chargeInventory, boolean chargeHotbar, boolean chargeBaubles,
                              boolean chargeMainHand, boolean chargeOffHand) {
        this.chargeInventory = chargeInventory;
        this.chargeHotbar = chargeHotbar;
        this.chargeBaubles = chargeBaubles;
        this.chargeMainHand = chargeMainHand;
        this.chargeOffHand = chargeOffHand;
    }

    public static ChargingPreference defaultAll() {
        return new ChargingPreference(true, true, true, true, true);
    }

    public static ChargingPreference deserialize(NBTTagCompound nbt) {
        return new ChargingPreference(
            nbt.getBoolean("inv"),
            nbt.getBoolean("hotbar"),
            nbt.getBoolean("baubles"),
            nbt.getBoolean("mainHand"),
            nbt.getBoolean("offHand")
        );
    }

    public NBTTagCompound serialize() {
        var nbt = new NBTTagCompound();
        nbt.setBoolean("inv", chargeInventory);
        nbt.setBoolean("hotbar", chargeHotbar);
        nbt.setBoolean("baubles", chargeBaubles);
        nbt.setBoolean("mainHand", chargeMainHand);
        nbt.setBoolean("offHand", chargeOffHand);
        return nbt;
    }
}
