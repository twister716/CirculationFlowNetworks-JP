package com.circulation.circulation_networks.mixins.mc;

import com.circulation.circulation_networks.utils.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk extends ChunkAccess {

    @Shadow
    @Final
    Level level;
    @Unique
    private BlockEntity cfn$blockEntity;

    public MixinLevelChunk(ChunkPos p_187621_, UpgradeData p_187622_, LevelHeightAccessor p_187623_, Registry<Biome> p_187624_, long p_187625_, @Nullable LevelChunkSection[] p_187626_, @Nullable BlendingData p_187627_) {
        super(p_187621_, p_187622_, p_187623_, p_187624_, p_187625_, p_187626_, p_187627_);
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;clearRemoved()V", shift = At.Shift.AFTER))
    private void setBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        var pos = blockEntity.getBlockPos();
        var rb = this.blockEntities.get(pos);
        if (rb != null) {
            EventHooks.onBlockEntityInvalidate(this.level, pos, rb);
            rb.setRemoved();
            rb.setLevel(null);
        }
        if (blockEntity != null) {
            EventHooks.onBlockEntityValidate(this.level, pos, blockEntity);
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE))
    private void getRlockEntity(BlockPos blockPos, CallbackInfo ci) {
        cfn$blockEntity = this.blockEntities.get(blockPos);
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V", shift = At.Shift.BEFORE))
    private void removeBlockEntity(BlockPos blockPos, CallbackInfo ci) {
        EventHooks.onBlockEntityInvalidate(this.level, blockPos, cfn$blockEntity);
    }

    @Inject(method = "removeBlockEntity", at = @At("TAIL"))
    private void removeBlockEntityR(BlockPos blockPos, CallbackInfo ci) {
        cfn$blockEntity = null;
    }
}