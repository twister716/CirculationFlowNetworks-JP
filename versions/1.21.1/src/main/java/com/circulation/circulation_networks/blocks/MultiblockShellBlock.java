package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.tiles.MultiblockShellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import java.util.function.BiConsumer;

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
    protected RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    protected float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    @NotNull
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        return Collections.emptyList();
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                               @NotNull BlockPos pos, @NotNull Player player,
                                               @NotNull BlockHitResult hit) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            BlockHitResult newHit = new BlockHitResult(hit.getLocation(), hit.getDirection(), originPos, hit.isInside());
            return originState.useWithoutItem(level, player, newHit);
        }
        return InteractionResult.PASS;
    }

    @Override
    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                              @NotNull Level level, @NotNull BlockPos pos,
                                              @NotNull Player player, @NotNull InteractionHand hand,
                                              @NotNull BlockHitResult hit) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            BlockHitResult newHit = new BlockHitResult(hit.getLocation(), hit.getDirection(), originPos, hit.isInside());
            return originState.useItemOn(stack, level, player, hand, newHit);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
    @NotNull
    public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos,
                                        @NotNull BlockState state, @NotNull Player player) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            originState.getBlock().playerWillDestroy(level, originPos, originState, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
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
    protected void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                                   @NotNull ItemStack stack, boolean dropExperience) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            originState.spawnAfterBreak(level, originPos, stack, dropExperience);
        }
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                            @NotNull BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            level.removeBlock(originPos, false);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void onExplosionHit(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                  @NotNull Explosion explosion, @NotNull BiConsumer<ItemStack, BlockPos> dropConsumer) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null && !level.isEmptyBlock(originPos)) {
            BlockState originState = level.getBlockState(originPos);
            BlockEntity originBE = level.getBlockEntity(originPos);
            Block.dropResources(originState, level, originPos, originBE);
            level.removeBlock(originPos, false);
        }
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    @NotNull
    public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            return originState.getBlock().getCloneItemStack(level, originPos, originState);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected float getDestroyProgress(@NotNull BlockState state, @NotNull Player player,
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
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                   @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        BlockPos originPos = getOriginPos(level, pos);
        if (originPos != null) {
            BlockState originState = level.getBlockState(originPos);
            originState.handleNeighborChanged(level, originPos, neighborBlock, neighborPos, isMoving);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
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
    protected VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                  @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getShape);
    }

    @Override
    @NotNull
    protected VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getCollisionShape);
    }

    @Override
    @NotNull
    protected VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(level, pos, context, BlockState::getVisualShape);
    }

    @Override
    @NotNull
    protected VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
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
    protected VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
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
    protected VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
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
    protected boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        BlockPos origin = getOriginPos(context.getLevel(), context.getClickedPos());
        return origin == null;
    }

    @FunctionalInterface
    private interface ShapeGetter {
        VoxelShape get(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context);
    }
}
