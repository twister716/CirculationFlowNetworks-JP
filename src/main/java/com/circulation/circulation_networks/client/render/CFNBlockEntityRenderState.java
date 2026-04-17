package com.circulation.circulation_networks.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CFNBlockEntityRenderState<T extends BlockEntity> extends BlockEntityRenderState {

    @Nullable
    public T blockEntity;
    public float partialTick;
}
