package ninjaphenix.expandedstorage.barrel.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.barrel.BarrelCommon;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public final class BarrelBlock extends AbstractOpenableStorageBlock {
    public BarrelBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier, ResourceLocation openStat, int slots) {
        super(properties, blockId, blockTier, openStat, slots);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(BlockStateProperties.OPEN, false));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.OPEN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public ResourceLocation blockType() {
        return BarrelCommon.BLOCK_TYPE;
    }

    @NotNull
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new BarrelBlockEntity(BarrelCommon.getBlockEntityType(), this.blockId());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (level.getBlockEntity(pos) instanceof BarrelBlockEntity entity) {
            entity.checkViewerCount();
        }
    }
}
