package ninjaphenix.expandedstorage.chest.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;

public final class ChestBlockEntity extends AbstractOpenableStorageBlockEntity {
    private final ChestLidController chestLidController;

    public ChestBlockEntity(BlockEntityType<ChestBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state, ((ChestBlock) state.getBlock()).blockId());
        chestLidController = new ChestLidController();
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, ChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    private static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        DoubleBlockCombiner.BlockType mergeType = ChestBlock.getBlockType(state);
        Vec3 soundPos;
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) {
            soundPos = Vec3.atCenterOf(pos);
        } else if (mergeType == DoubleBlockCombiner.BlockType.FIRST) {
            soundPos = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(ChestBlock.getDirectionToAttached(state).getNormal()).scale(0.5D));
        } else {
            return;
        }
        level.playSound(null, soundPos.x(), soundPos.y(), soundPos.z(), soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
        ChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_OPEN);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
        ChestBlockEntity.playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
    }

    @Override
    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int i, int j) {
        level.blockEvent(pos, state.getBlock(), 1, j);
    }

    @Override
    protected boolean isOwnContainer(Container container) {
        return container == this || container instanceof CompoundWorldlyContainer compoundContainer && compoundContainer.consistsPartlyOf(this);
    }

    @Override
    public boolean triggerEvent(int event, int value) {
        if (event == ChestBlock.SET_OPEN_COUNT_EVENT) {
            chestLidController.shouldBeOpen(value > 0);
            return true;
        }
        return super.triggerEvent(event, value);
    }

    // Client only
    public float getLidOpenness(float f) {
        return chestLidController.getOpenness(f);
    }
}
