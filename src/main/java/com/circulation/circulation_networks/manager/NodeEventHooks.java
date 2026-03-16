package com.circulation.circulation_networks.manager;

import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.events.AddNodeEvent;
import com.circulation.circulation_networks.events.RemoveNodeEvent;
//? if <1.20 {
import net.minecraft.tileentity.TileEntity;
//?} else {
/*import net.minecraft.world.level.block.entity.BlockEntity;
*///?}
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

final class NodeEventHooks {

    //?if <1.20 {
    private static final EventBus eventBus;
     //?} else {
    /*private static final IEventBus eventBus;
    *///?}

    static {
        //?if <1.21 {
        eventBus = MinecraftForge.EVENT_BUS;
        //?} else {
        /* eventBus = NeoForge.EVENT_BUS;
         *///?}
    }

    static void postRemoveNodePre(INode node) {
        eventBus.post(new RemoveNodeEvent.Pre(node));
    }

    static void postRemoveNodePost(INode node) {
        eventBus.post(new RemoveNodeEvent.Post(node));
    }

    //? if <1.20 {
    static boolean postAddNodePre(INode node, TileEntity tileEntity) {
        return eventBus.post(new AddNodeEvent.Pre(node, tileEntity));
    }

    static void postAddNodePost(INode node, TileEntity tileEntity) {
        eventBus.post(new AddNodeEvent.Post(node, tileEntity));
    }
    //?} else {
    /*static boolean postAddNodePre(INode node, BlockEntity blockEntity) {
        //? if <1.21 {
        return eventBus.post(new AddNodeEvent.Pre(node, blockEntity));
        //?} else {
        /^ return eventBus.post(new AddNodeEvent.Pre(node, blockEntity)).isCanceled();
         ^///?}
    }

    static void postAddNodePost(INode node, BlockEntity blockEntity) {
        eventBus.post(new AddNodeEvent.Post(node, blockEntity));
    }
    *///?}
}