package com.circulation.circulation_networks.events;

import com.circulation.circulation_networks.api.node.INode;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

// Shared node-add event whose attached world object may come from a block entity or a virtual host.
public class AddNodeEvent extends NodeEvent {

    @Nullable
    private final BlockEntity blockEntity;

    public AddNodeEvent(INode node, @Nullable BlockEntity blockEntity) {
        super(node);
        this.blockEntity = blockEntity;
    }

    public @Nullable BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public static class Pre extends AddNodeEvent {

        private boolean canceled;

        public Pre(INode node, BlockEntity blockEntity) {
            super(node, blockEntity);
        }

        public boolean isCancelable() {
            return true;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public static class Post extends AddNodeEvent {

        public Post(INode node, @Nullable BlockEntity blockEntity) {
            super(node, blockEntity);
        }
    }
}
