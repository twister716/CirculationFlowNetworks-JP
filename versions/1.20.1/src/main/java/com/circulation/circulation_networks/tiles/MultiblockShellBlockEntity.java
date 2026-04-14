package com.circulation.circulation_networks.tiles;

import com.circulation.circulation_networks.registry.CFNBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockShellBlockEntity extends BlockEntity implements Nameable {

    @Nullable
    private BlockPos originPos;

    public MultiblockShellBlockEntity(BlockPos pos, BlockState state) {
        super(CFNBlockEntityTypes.MULTIBLOCK_SHELL, pos, state);
    }

    public void setOriginPos(@NotNull BlockPos pos) {
        this.originPos = pos;
        setChanged();
    }

    @NotNull
    public BlockPos getOriginPos() {
        return originPos != null ? originPos : worldPosition;
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (originPos != null) {
            tag.putInt("OriginX", originPos.getX());
            tag.putInt("OriginY", originPos.getY());
            tag.putInt("OriginZ", originPos.getZ());
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("OriginX")) {
            originPos = new BlockPos(
                tag.getInt("OriginX"),
                tag.getInt("OriginY"),
                tag.getInt("OriginZ")
            );
        }
    }

    // Nameable

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

    @Override
    @NotNull
    public Component getDisplayName() {
        BlockEntity origin = getOriginBlockEntity();
        if (origin instanceof Nameable nameable) {
            return nameable.getDisplayName();
        }
        return getName();
    }

    // Capability proxy

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        BlockEntity origin = getOriginBlockEntity();
        if (origin != null) {
            return origin.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }
}
