package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
    protected static final String[] EMPTY_TOOLTIPS = new String[0];
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
            List<String> s = new ObjectArrayList<>();
            var key = getTranslationKey(stack);
            int i = 0;
            String t;
            while (I18n.hasKey(t = key + ".tooltip." + i)) {
                s.add(t);
                ++i;
            }
            tooltips = s.isEmpty() ? EMPTY_TOOLTIPS : s.toArray(EMPTY_TOOLTIPS);
        }
        //noinspection ForLoopReplaceableByForEach
        for (var i = 0; i < tooltips.length; i++) {
            tooltip.add(I18n.format(tooltips[i]));
        }
    }
}
