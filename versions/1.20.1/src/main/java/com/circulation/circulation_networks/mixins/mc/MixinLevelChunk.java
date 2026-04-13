package com.circulation.circulation_networks.mixins.mc;

import com.circulation.circulation_networks.utils.EventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk {

    @Shadow
    @Final
    Level level;

    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos p_62912_);

    @Inject(method = "addAndRegisterBlockEntity", at = @At("TAIL"))
    private void addAndRegisterBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity != null) {
            EventHooks.onBlockEntityValidate(this.level, blockEntity.getBlockPos(), blockEntity);
        }
    }

    @Redirect(method = {"setBlockEntity", "removeBlockEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"))
    private void removeBlockEntity(BlockEntity blockEntity) {
        if (blockEntity != null) {
            EventHooks.onBlockEntityInvalidate(this.level, blockEntity.getBlockPos(), blockEntity);
            blockEntity.setRemoved();
        }
    }
}
