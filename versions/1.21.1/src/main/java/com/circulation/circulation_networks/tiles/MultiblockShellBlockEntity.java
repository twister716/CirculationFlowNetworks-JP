package com.circulation.circulation_networks.tiles;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockShellBlockEntity extends BlockEntity implements Nameable {

    @Nullable
    private BlockPos originPos;

    public MultiblockShellBlockEntity(BlockPos pos, BlockState state) {
        super(CFNBlockEntityTypes.MULTIBLOCK_SHELL, pos, state);
    }

    public static <T, C> void registerCapabilityProxy(RegisterCapabilitiesEvent event, BlockCapability<T, C> capability, Block shellBlock) {
        event.registerBlock(capability, (level, pos, state, be, context) -> {
            if (be instanceof MultiblockShellBlockEntity shell && shell.canRedirect()) {
                return level.getCapability(capability, shell.getOriginPos(), context);
            }
            return null;
        }, shellBlock);
    }

    @NotNull
    public BlockPos getOriginPos() {
        return originPos != null ? originPos : worldPosition;
    }

    public void setOriginPos(@NotNull BlockPos pos) {
        this.originPos = pos;
        setChanged();
    }

    public boolean canRedirect() {
        return originPos != null && !originPos.equals(worldPosition);
    }

    @Nullable
    public BlockEntity getOriginBlockEntity() {
        if (canRedirect() && level != null && level.isLoaded(originPos)) {
            return level.getBlockEntity(originPos);
        }
        return null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (originPos != null) {
            tag.putInt("OriginX", originPos.getX());
            tag.putInt("OriginY", originPos.getY());
            tag.putInt("OriginZ", originPos.getZ());
        }
    }

    // Nameable

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("OriginX")) {
            originPos = new BlockPos(
                tag.getInt("OriginX"),
                tag.getInt("OriginY"),
                tag.getInt("OriginZ")
            );
        }
    }

    @Override
    @NotNull
    public Component getName() {
        BlockEntity origin = getOriginBlockEntity();
        if (origin instanceof Nameable nameable) {
            return nameable.getName();
        }
        return Component.translatable("block.circulation_networks.multiblock_shell");
    }

    @Override
    public boolean hasCustomName() {
        BlockEntity origin = getOriginBlockEntity();
        if (origin instanceof Nameable nameable) {
            return nameable.hasCustomName();
        }
        return false;
    }

    @Override
    @Nullable
    public Component getCustomName() {
        BlockEntity origin = getOriginBlockEntity();
        if (origin instanceof Nameable nameable) {
            return nameable.getCustomName();
        }
        return null;
    }

    // Capability proxy — registered statically via RegisterCapabilitiesEvent

    @Override
    @NotNull
    public Component getDisplayName() {
        BlockEntity origin = getOriginBlockEntity();
        if (origin instanceof Nameable nameable) {
            return nameable.getDisplayName();
        }
        return getName();
    }
}
