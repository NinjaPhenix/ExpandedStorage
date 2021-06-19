package ninjaphenix.expandedstorage.chest.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.chest.ChestCommon;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final int SET_OPEN_COUNT_EVENT = 1;
    private static final VoxelShape[] SHAPES = {
            Block.box(1, 0, 0, 15, 14, 15), // Horizontal shapes, depends on orientation and chest type.
            Block.box(1, 0, 1, 16, 14, 15),
            Block.box(1, 0, 1, 15, 14, 16),
            Block.box(0, 0, 1, 15, 14, 15),
            Block.box(1, 0, 1, 15, 14, 15), // Top shape.
            Block.box(1, 0, 1, 15, 16, 15), // Bottom shape.
            Block.box(1, 0, 1, 15, 14, 15)  // Single shape.
    };

    public ChestBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier,
                      ResourceLocation openStat, int slots) {
        super(properties, blockId, blockTier, openStat, slots);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.OPEN, false));
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }

    @Override
    public ResourceLocation blockType() {
        return ChestCommon.BLOCK_TYPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        CursedChestType type = state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE);
        if (type == CursedChestType.TOP) {
            return ChestBlock.SHAPES[4];
        } else if (type == CursedChestType.BOTTOM) {
            return ChestBlock.SHAPES[5];
        } else if (type == CursedChestType.SINGLE) {
            return ChestBlock.SHAPES[6];
        } else {
            int index = (state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() + type.offset()) % 4;
            return ChestBlock.SHAPES[index];
        }
    }

    @NotNull
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new ChestBlockEntity(ChestCommon.getBlockEntityType(), blockId());
    }

    @Override
    protected void appendAdditionalStateDefinitions(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED);
        builder.add(BlockStateProperties.OPEN);
    }

    @Override
    protected BlockEntityType<ChestBlockEntity> blockEntityType() {
        return ChestCommon.getBlockEntityType();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int event, int value) {
        super.triggerEvent(state, level, pos, event, value);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(event, value);
    }

    @Override
    protected boolean isBlocked(LevelAccessor level, BlockPos pos) {
        return net.minecraft.world.level.block.ChestBlock.isChestBlockedAt(level, pos);
    }
}
