package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.INodeBlockEntity;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.events.AddNodeEvent;
import com.circulation.circulation_networks.events.BlockEntityLifeCycleEvent;
import com.circulation.circulation_networks.events.RemoveNodeEvent;
//~ mc_imports
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
//? if <1.21 {
import net.minecraftforge.common.MinecraftForge;
//?} else {
/*import net.neoforged.neoforge.common.NeoForge;
 *///?}
//? if <1.20 {
import net.minecraftforge.fml.common.eventhandler.EventBus;
//?} else if < 1.21 {
/*import net.minecraftforge.eventbus.api.IEventBus;
 *///?} else {
/*import net.neoforged.bus.api.IEventBus;
 *///?}

import org.jetbrains.annotations.Nullable;

public final class EventHooks {

    //~ if >=1.20 'EventBus ' -> 'IEventBus ' {
    private static final EventBus eventBus;
    //~}

    static {
        //?if <1.21 {
        eventBus = MinecraftForge.EVENT_BUS;
        //?} else {
        /*eventBus = NeoForge.EVENT_BUS;
         *///?}
    }

    public static void postRemoveNodePre(INode node) {
        eventBus.post(new RemoveNodeEvent.Pre(node));
    }

    public static void postRemoveNodePost(INode node) {
        eventBus.post(new RemoveNodeEvent.Post(node));
    }

    //~ if >=1.20 'TileEntity ' -> 'BlockEntity ' {
    public static boolean postAddNodePre(INode node, @Nullable TileEntity tileEntity) {
        //? if <1.21 {
        return eventBus.post(new AddNodeEvent.Pre(node, tileEntity));
        //?} else {
        /*return eventBus.post(new AddNodeEvent.Pre(node, tileEntity)).isCanceled();
         *///?}
    }

    public static void postAddNodePost(INode node, @Nullable TileEntity tileEntity) {
        eventBus.post(new AddNodeEvent.Post(node, tileEntity));
    }

    //~ if >=1.20 'World ' -> 'Level ' {
    public static void onBlockEntityValidate(World world, BlockPos pos, TileEntity blockEntity) {
        if (blockEntity instanceof INodeBlockEntity nbe) {
            nbe.nodeValidate();
        }
        var event = new BlockEntityLifeCycleEvent.Validate(world, pos, blockEntity);
        BlockEntityLifecycleDispatcher.onValidate(event);
        eventBus.post(event);
    }

    public static void onBlockEntityInvalidate(World world, BlockPos pos, TileEntity blockEntity) {
        if (blockEntity instanceof INodeBlockEntity nbe) {
            nbe.nodeInvalidate();
        }
        var event = new BlockEntityLifeCycleEvent.Invalidate(world, pos, blockEntity);
        BlockEntityLifecycleDispatcher.onInvalidate(event);
        eventBus.post(event);
    }
    //~}
    //~}
}