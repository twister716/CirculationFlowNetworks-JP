package com.circulation.circulation_networks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityMultiblockShell extends TileEntity implements IWorldNameable {

    @Nullable
    private BlockPos originPos;

    @NotNull
    public BlockPos getOriginPos() {
        return originPos != null ? originPos : pos;
    }

    public void setOriginPos(@NotNull BlockPos pos) {
        this.originPos = pos;
        markDirty();
        if (world != null && !world.isRemote) {
            BlockPos currentPos = this.pos;
            world.notifyBlockUpdate(currentPos, getBlockType().getDefaultState(), getBlockType().getDefaultState(), 3);
        }
    }

    public boolean canRedirect() {
        return originPos != null && !originPos.equals(pos);
    }

    @Nullable
    public TileEntity getOriginTileEntity() {
        if (canRedirect() && world != null && world.isBlockLoaded(originPos)) {
            return world.getTileEntity(originPos);
        }
        return null;
    }

    @Override
    @NotNull
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (originPos != null) {
            compound.setInteger("OriginX", originPos.getX());
            compound.setInteger("OriginY", originPos.getY());
            compound.setInteger("OriginZ", originPos.getZ());
        }
        return compound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("OriginX")) {
            originPos = new BlockPos(
                compound.getInteger("OriginX"),
                compound.getInteger("OriginY"),
                compound.getInteger("OriginZ")
            );
        } else {
            originPos = null;
        }
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
    }

    @Override
    @NotNull
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(@NotNull NetworkManager net, @NotNull SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(@NotNull NBTTagCompound tag) {
        readFromNBT(tag);
    }

    // IWorldNameable

    @Override
    @NotNull
    public String getName() {
        TileEntity origin = getOriginTileEntity();
        if (origin instanceof IWorldNameable nameable) {
            return nameable.getName();
        }
        return "tile.multiblock_shell.name";
    }

    @Override
    public boolean hasCustomName() {
        TileEntity origin = getOriginTileEntity();
        if (origin instanceof IWorldNameable nameable) {
            return nameable.hasCustomName();
        }
        return false;
    }

    @Override
    @NotNull
    public ITextComponent getDisplayName() {
        TileEntity origin = getOriginTileEntity();
        if (origin instanceof IWorldNameable nameable) {
            return nameable.getDisplayName();
        }
        return new TextComponentTranslation("tile.multiblock_shell.name");
    }

    // Capability proxy

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        TileEntity origin = getOriginTileEntity();
        if (origin != null) {
            return origin.hasCapability(capability, facing);
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        TileEntity origin = getOriginTileEntity();
        if (origin != null) {
            return origin.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }
}
