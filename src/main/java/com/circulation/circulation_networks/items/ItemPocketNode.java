package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.manager.PocketNodeManager;
import com.circulation.circulation_networks.manager.PocketNodeManager.RegisterPocketNodeResult;
//~ mc_imports
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//? if <1.20 {
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
//?} else {
/*import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
*///?}

import org.jetbrains.annotations.NotNull;

public class ItemPocketNode extends BaseItem {

    private final NodeType<?> nodeType;

    //? if <1.20 {
    public ItemPocketNode(String name, NodeType<?> nodeType) {
        super(name);
        this.nodeType = nodeType;
    }
    //?} else {
    /*public ItemPocketNode(NodeType<?> nodeType, Properties properties) {
        super(properties);
        this.nodeType = nodeType;
    }
    *///?}

    //? if <1.20 {
    private static void sendFeedback(EntityPlayerMP player, String key) {
        player.sendMessage(new TextComponentTranslation(key));
    }
    //?}

    //? if <1.20 {
    @Override
    public @NotNull EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack stack = player.getHeldItem(hand);
        RegisterPocketNodeResult result = PocketNodeManager.INSTANCE.registerPocketNodeDetailed(worldIn, pos, nodeType, facing, null);
        if (!result.isSuccess()) {
            if (result == RegisterPocketNodeResult.OCCUPIED) {
                sendFeedback(serverPlayer, "message.circulation_networks.pocket_node_occupied");
            }
            return EnumActionResult.FAIL;
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer player, @NotNull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
    //?} else {
    /*@Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        RegisterPocketNodeResult result = PocketNodeManager.INSTANCE.registerPocketNodeDetailed(
            context.getLevel(),
            context.getClickedPos(),
            nodeType,
            context.getClickedFace(),
            null
        );
        if (!result.isSuccess()) {
            if (result == RegisterPocketNodeResult.OCCUPIED) {
                sendFeedback(serverPlayer, "message.circulation_networks.pocket_node_occupied");
            }
            return InteractionResult.FAIL;
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    private static void sendFeedback(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
    }
    *///?}
}