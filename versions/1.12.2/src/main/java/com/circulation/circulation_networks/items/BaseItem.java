package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.circulation.circulation_networks.CirculationFlowNetworks.CREATIVE_TAB;

public abstract class BaseItem extends Item {
    protected String[] tooltips;

    protected BaseItem(String name) {
        this.setRegistryName(new ResourceLocation(CirculationFlowNetworks.MOD_ID, name));
        this.setTranslationKey(CirculationFlowNetworks.MOD_ID + "." + name);
        this.setCreativeTab(CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        if (tooltips == null) {
            tooltips = BaseItemTooltipModel.resolveTooltipKeys(getTranslationKey(stack), I18n::hasKey);
        }
        //noinspection ForLoopReplaceableByForEach
        for (var i = 0; i < tooltips.length; i++) {
            tooltip.add(I18n.format(tooltips[i]));
        }
    }
}
