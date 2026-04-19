package com.circulation.circulation_networks.mixins.mc;

import com.circulation.circulation_networks.manager.PocketNodeManager;
import com.circulation.circulation_networks.utils.EventHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LevelChunk.class, remap = false)
public abstract class MixinLevelChunk extends ChunkAccess {

    @Shadow
    @Final
    Level level;

    public MixinLevelChunk(ChunkPos p_187621_, UpgradeData p_187622_, LevelHeightAccessor p_187623_, Registry<Biome> p_187624_, long p_187625_, @Nullable LevelChunkSection[] p_187626_, @Nullable BlendingData p_187627_) {
        super(p_187621_, p_187622_, p_187623_, p_187624_, p_187625_, p_187626_, p_187627_);
    }

    @Inject(method = "addAndRegisterBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshBlockEntities(Ljava/util/Collection;)V", shift = At.Shift.AFTER))
    public void addAndRegisterBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity != null) {
            EventHooks.onBlockEntityValidate(this.level, blockEntity.getBlockPos(), blockEntity);
        }
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V", shift = At.Shift.BEFORE))
    private void setBlockEntity(BlockEntity blockEntity, CallbackInfo ci, @Local(name = "blockentity") BlockEntity blockentity, @Local BlockPos pos) {
        EventHooks.onBlockEntityInvalidate(this.level, pos, blockentity);
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V", shift = At.Shift.BEFORE))
    private void removeBlockEntity(BlockPos blockPos, CallbackInfo ci, @Local(name = "blockentity") BlockEntity blockentity) {
        EventHooks.onBlockEntityInvalidate(this.level, blockPos, blockentity);
    }

    @Inject(method = "setBlockState", at = @At("TAIL"))
    private void setBlockState(BlockPos blockPos, BlockState blockState, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (!this.level.isClientSide && cir.getReturnValue() != null) {
            PocketNodeManager.INSTANCE.onHostBlockStateChanged(this.level, blockPos);
        }
    }
}