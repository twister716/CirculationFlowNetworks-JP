package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.MultiblockShellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class MultiblockShellBlock extends Block implements EntityBlock {

    public MultiblockShellBlock() {
        super(BlockBehaviour.Properties.of()
                                       .mapColor(MapColor.METAL)
                                       .strength(1.5F, 6.0F)
                                       .noOcclusion()
                                       .pushReaction(PushReaction.BLOCK)
                                       .isViewBlocking((s, l, p) -> false));
    }

    @Nullable
    private static BlockPos getOriginPos(@NotNull BlockGetter level, @NotNull BlockPos shellPos) {
        BlockEntity be = level.getBlockEntity(shellPos);
        if (be instanceof MultiblockShellBlockEntity shell && shell.canRedirect()) {
            return shell.getOriginPos();
        }
        return null;
    }

    @Nullable
    private static BlockState getOriginState(@NotNull BlockGetter level, @NotNull BlockPos shellPos) {
        BlockPos origin = getOriginPos(level, shellPos);
        return origin != null ? level.getBlockState(origin) : null;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MultiblockShellBlockEntity(pos, state);
    }

    @Override
    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        return Collections.emptyList();
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level,
                                  @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    @NotNull
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                 @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            BlockHitResult newHit = new BlockHitResult(hit.getLocation(), hit.getDirection(), originPos, hit.isInside());
            return originState.use(level, player, hand, newHit);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onDestroyedByPlayer(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                       @NotNull Player player, boolean willHarvest, @NotNull FluidState fluid) {
        if (willHarvest) {
            return true;
        }
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            level.removeBlock(originPos, false);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, false, fluid);
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos,
                              @NotNull BlockState state, @Nullable BlockEntity be, @NotNull ItemStack stack) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            BlockState originState = level.getBlockState(originPos);
            BlockEntity originBE = level.getBlockEntity(originPos);
            originState.getBlock().playerDestroy(level, player, originPos, originState, originBE, stack);
        } else {
            super.playerDestroy(level, player, pos, state, be, stack);
        }
        level.removeBlock(pos, false);
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                                @NotNull ItemStack stack, boolean dropExperience) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            originState.spawnAfterBreak(level, originPos, stack, dropExperience);
        }
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            level.removeBlock(originPos, false);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void onBlockExploded(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Explosion explosion) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            BlockState originState = level.getBlockState(originPos);
            BlockEntity originBE = level.getBlockEntity(originPos);
            Block.dropResources(originState, level, originPos, originBE);
            level.removeBlock(originPos, false);
        }
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    @NotNull
    public ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            return originState.getBlock().getCloneItemStack(level, originPos, originState);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player,
                                    @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            return originState.getDestroyProgress(player, level, originPos);
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public float getExplosionResistance(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull Explosion explosion) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            return originState.getBlock().getExplosionResistance(originState, level, originPos, explosion);
        }
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            originState.neighborChanged(level, originPos, neighborBlock, neighborPos, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            return originState.getAnalogOutputSignal(level, originPos);
        }
        return 0;
    }

    private VoxelShape proxyShape(BlockGetter level, BlockPos pos, CollisionContext context, ShapeGetter getter) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos == null) return Shapes.block();
        BlockState originState = level.getBlockState(originPos);
        VoxelShape shape = getter.get(originState, level, originPos, context);
        VoxelShape moved = shape.move(
            originPos.getX() - pos.getX(),
            originPos.getY() - pos.getY(),
            originPos.getZ() - pos.getZ()
        );
        return moved.isEmpty() ? Shapes.block() : moved;
    }

    @Override
    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                               @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getShape);
    }

    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getCollisionShape);
    }

    @Override
    @NotNull
    public VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                     @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getVisualShape);
    }

    @Override
    @NotNull
    public VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos == null) return Shapes.block();
        BlockState originState = level.getBlockState(originPos);
        VoxelShape shape = originState.getOcclusionShape(level, originPos);
        VoxelShape moved = shape.move(
            originPos.getX() - pos.getX(),
            originPos.getY() - pos.getY(),
            originPos.getZ() - pos.getZ()
        );
        return moved.isEmpty() ? Shapes.block() : moved;
    }

    @Override
    @NotNull
    public VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos == null) return Shapes.block();
        BlockState originState = level.getBlockState(originPos);
        VoxelShape shape = originState.getBlockSupportShape(level, originPos);
        VoxelShape moved = shape.move(
            originPos.getX() - pos.getX(),
            originPos.getY() - pos.getY(),
            originPos.getZ() - pos.getZ()
        );
        return moved.isEmpty() ? Shapes.block() : moved;
    }

    @Override
    @NotNull
    public VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos == null) return Shapes.block();
        BlockState originState = level.getBlockState(originPos);
        VoxelShape shape = originState.getInteractionShape(level, originPos);
        VoxelShape moved = shape.move(
            originPos.getX() - pos.getX(),
            originPos.getY() - pos.getY(),
            originPos.getZ() - pos.getZ()
        );
        return moved.isEmpty() ? Shapes.block() : moved;
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        BlockPos origin = getOriginPos(context.getLevel(), context.getClickedPos());
        return origin == null;
    }

    @FunctionalInterface
    private interface ShapeGetter {
        VoxelShape get(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);
    }
}
