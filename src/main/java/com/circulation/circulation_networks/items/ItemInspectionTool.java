package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.node.IChargingNode;
import com.circulation.circulation_networks.api.node.IEnergySupplyNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.manager.EnergyMachineManager;
import com.circulation.circulation_networks.manager.EnergyTypeOverrideManager;
import com.circulation.circulation_networks.manager.NetworkManager;
import com.circulation.circulation_networks.packets.ConfigOverrideRendering;
import com.circulation.circulation_networks.packets.NodeNetworkRendering;
import com.circulation.circulation_networks.packets.SpoceRendering;
import com.circulation.circulation_networks.registry.RegistryEnergyHandler;
import com.circulation.circulation_networks.utils.Functions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemInspectionTool extends BaseItem {

    private static final List<InspectionMode> INSPECTION_VALUES = new ObjectArrayList<>();

    public ItemInspectionTool() {
        super("inspection_tool");
    }

    private static void sendModeMessage(EntityPlayerMP player, ToolFunction function, int subMode) {
        TextComponentTranslation modeComponent;
        TextComponentTranslation submodeComponent;

        if (function == ToolFunction.INSPECTION) {
            InspectionMode mode = InspectionMode.fromID(subMode);
            modeComponent = new TextComponentTranslation(function.getLangKey());
            submodeComponent = new TextComponentTranslation(mode.getLangKey());
        } else {
            ConfigurationMode mode = ConfigurationMode.fromID(subMode);
            modeComponent = new TextComponentTranslation(function.getLangKey());
            submodeComponent = new TextComponentTranslation(mode.getLangKey());
        }

        modeComponent.getStyle().setColor(TextFormatting.GOLD);
        submodeComponent.getStyle().setColor(TextFormatting.BLUE);

        TextComponentTranslation message = new TextComponentTranslation(
            "item.circulation_networks.inspection_tool.mode_display",
            modeComponent,
            submodeComponent
        );
        player.sendStatusMessage(message, true);
    }

    private static void toggleFunction(ItemStack stack, EntityPlayerMP player) {
        var nbt = Functions.getOrCreateTagCompound(stack);
        int oldFunc = nbt.getInteger("function");
        int newFunc = (oldFunc + 1) % ToolFunction.values().length;
        nbt.setInteger("function", newFunc);
        nbt.setInteger("mode", 0);

        // Sync configuration overlay rendering
        if (ToolFunction.fromID(newFunc) == ToolFunction.CONFIGURATION) {
            ConfigOverrideRendering.sendFullSync(player);
        } else if (ToolFunction.fromID(oldFunc) == ToolFunction.CONFIGURATION) {
            ConfigOverrideRendering.sendClear(player);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        ToolFunction function = getFunction(stack);
        int subMode = getSubMode(stack);

        String modeName;
        String subModeName;

        if (function == ToolFunction.INSPECTION) {
            InspectionMode mode = InspectionMode.fromID(subMode);
            modeName = I18n.format(function.getLangKey());
            subModeName = I18n.format(mode.getLangKey());
        } else {
            ConfigurationMode mode = ConfigurationMode.fromID(subMode);
            modeName = I18n.format(function.getLangKey());
            subModeName = I18n.format(mode.getLangKey());
        }

        tooltip.add(I18n.format("item.circulation_networks.inspection_tool.current_mode", modeName));
        tooltip.add(I18n.format("item.circulation_networks.inspection_tool.current_submode", subModeName));
        tooltip.add("§7" + I18n.format("item.circulation_networks.inspection_tool.mode." + function.name().toLowerCase() + ".description"));
        tooltip.add("");
        tooltip.add(I18n.format("item.circulation_networks.inspection_tool.usage.switch_mode"));
        tooltip.add(I18n.format("item.circulation_networks.inspection_tool.usage.switch_submode"));
    }

    @Override
    public @NotNull EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(player instanceof EntityPlayerMP p)) return EnumActionResult.PASS;

        ItemStack stack = p.getHeldItemMainhand();
        ToolFunction function = getFunction(stack);
        int subMode = getSubMode(stack);
        return function.execute(p, worldIn, pos, subMode);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer player, @NotNull EnumHand hand) {
        if (!worldIn.isRemote && player instanceof EntityPlayerMP p && p.isSneaking()) {
            RayTraceResult ray = p.rayTrace(p.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue(), 1.0F);
            if (ray == null || ray.typeOfHit == RayTraceResult.Type.MISS) {
                ItemStack stack = p.getHeldItem(hand);
                toggleFunction(stack, p);
                sendModeMessage(p, getFunction(stack), getSubMode(stack));
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return super.onItemRightClick(worldIn, player, hand);
    }

    public ToolFunction getFunction(ItemStack stack) {
        var nbt = stack.getTagCompound();
        if (nbt == null) return ToolFunction.INSPECTION;
        return ToolFunction.fromID(nbt.getInteger("function"));
    }

    public int getSubMode(ItemStack stack) {
        var nbt = stack.getTagCompound();
        if (nbt == null) return 0;
        return nbt.getInteger("mode");
    }

    public enum ToolFunction {
        INSPECTION(InspectionMode.values().length, (player, world, pos, subMode) -> {
            INode node = NetworkManager.INSTANCE.getNodeFromPos(world, pos);
            if (node == null) return EnumActionResult.PASS;
            var mode = InspectionMode.fromID(subMode);
            return mode.sendPacket(player, node);
        }),
        CONFIGURATION(ConfigurationMode.values().length, (player, world, pos, subMode) -> {
            var manager = EnergyTypeOverrideManager.get();
            if (manager == null) return EnumActionResult.FAIL;

            INode node = NetworkManager.INSTANCE.getNodeFromPos(world, pos);
            if (node != null) {
                player.sendMessage(new TextComponentTranslation("item.circulation_networks.inspection_tool.config.node_blocked"));
                return EnumActionResult.FAIL;
            }

            TileEntity te = world.getTileEntity(pos);
            if (te == null) return EnumActionResult.PASS;

            if (RegistryEnergyHandler.isBlack(te) || !RegistryEnergyHandler.isEnergyTileEntity(te)
                || !EnergyMachineManager.INSTANCE.getMachineGridMap().containsKey(te)) {
                player.sendMessage(new TextComponentTranslation("item.circulation_networks.inspection_tool.config.invalid_target"));
                return EnumActionResult.FAIL;
            }

            ConfigurationMode mode = ConfigurationMode.fromID(subMode);
            int dim = world.provider.getDimension();

            if (mode == ConfigurationMode.CLEAR) {
                manager.clearOverride(dim, pos);
                ConfigOverrideRendering.sendRemove(player, pos.toLong());
                player.sendMessage(new TextComponentTranslation("item.circulation_networks.inspection_tool.config.cleared"));
            } else {
                manager.setOverride(dim, pos, mode.getEnergyType());
                ConfigOverrideRendering.sendAdd(player, pos.toLong(), mode.getEnergyType());
                player.sendMessage(new TextComponentTranslation("item.circulation_networks.inspection_tool.config.set",
                    new TextComponentTranslation(mode.getLangKey())));
            }
            return EnumActionResult.SUCCESS;
        });

        @Getter
        private final int subModeCount;
        private final ExecuteFunction executeFunction;

        ToolFunction(int subModeCount, ExecuteFunction executeFunction) {
            this.subModeCount = subModeCount;
            this.executeFunction = executeFunction;
        }

        public static ToolFunction fromID(int id) {
            return values()[Math.floorMod(id, values().length)];
        }

        public EnumActionResult execute(EntityPlayerMP player, World world, BlockPos pos, int subMode) {
            return executeFunction.execute(player, world, pos, subMode);
        }

        public String getLangKey() {
            return "item.circulation_networks.inspection_tool.mode." + this.name().toLowerCase();
        }
    }

    public enum InspectionMode {
        ALL,
        SPOCE(((player, node) -> {
            double l = node.getLinkScope();
            double e = 0;
            double c = 0;
            if (node instanceof IEnergySupplyNode n) {
                e = n.getEnergyScope();
            }
            if (node instanceof IChargingNode n) {
                c = n.getChargingScope();
            }
            CirculationFlowNetworks.NET_CHANNEL.sendTo(new SpoceRendering(node.getPos(), l, e, c), player);
            return EnumActionResult.SUCCESS;
        })),
        LINK(((player, node) -> {
            CirculationFlowNetworks.NET_CHANNEL.sendTo(new NodeNetworkRendering(player, node.getGrid()), player);
            NodeNetworkRendering.addPlayer(node.getGrid(), player);
            return EnumActionResult.SUCCESS;
        }));

        private final ModeRun run;

        InspectionMode() {
            run = ((player, node) -> {
                for (InspectionMode value : INSPECTION_VALUES) {
                    value.sendPacket(player, node);
                }
                return EnumActionResult.SUCCESS;
            });
        }

        InspectionMode(ModeRun run) {
            this.run = run;
            INSPECTION_VALUES.add(this);
        }

        public static InspectionMode fromID(int id) {
            return values()[Math.floorMod(id, values().length)];
        }

        public EnumActionResult sendPacket(EntityPlayerMP player, INode node) {
            return run.send(player, node);
        }

        public boolean isMode(InspectionMode mode) {
            return this == ALL || this.ordinal() == mode.ordinal();
        }

        public String getLangKey() {
            return "item.circulation_networks.inspection_tool.submode.inspection." + this.name().toLowerCase();
        }
    }

    @Getter
    public enum ConfigurationMode {
        SEND(IEnergyHandler.EnergyType.SEND),
        RECEIVE(IEnergyHandler.EnergyType.RECEIVE),
        STORAGE(IEnergyHandler.EnergyType.STORAGE),
        CLEAR(null);

        private final IEnergyHandler.EnergyType energyType;

        ConfigurationMode(IEnergyHandler.EnergyType energyType) {
            this.energyType = energyType;
        }

        public static ConfigurationMode fromID(int id) {
            return values()[Math.floorMod(id, values().length)];
        }

        public String getLangKey() {
            return "item.circulation_networks.inspection_tool.submode.configuration." + this.name().toLowerCase();
        }
    }

    @FunctionalInterface
    public interface ExecuteFunction {
        @Nonnull
        EnumActionResult execute(@Nonnull EntityPlayerMP player, @Nonnull World world, @Nonnull BlockPos pos, int subMode);
    }

    @FunctionalInterface
    public interface ModeRun {
        @Nonnull
        EnumActionResult send(@Nonnull EntityPlayerMP player, @Nonnull INode node);
    }
}