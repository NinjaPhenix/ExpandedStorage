package ninjaphenix.expandedstorage.barrel.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;

public class BarrelBlockEntity extends AbstractOpenableStorageBlockEntity {

    public BarrelBlockEntity(BlockEntityType<BarrelBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state, ((BarrelBlock) state.getBlock()).blockId());
    }

    private void playSound(BlockState state, SoundEvent sound) {
        Vec3i facingVector = state.getValue(BlockStateProperties.FACING).getNormal();
        double X = worldPosition.getX() + 0.5D + facingVector.getX() / 2.0D;
        double Y = worldPosition.getY() + 0.5D + facingVector.getY() / 2.0D;
        double Z = worldPosition.getZ() + 0.5D + facingVector.getZ() / 2.0D;
        //noinspection ConstantConditions
        level.playSound(null, X, Y, Z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void onOpen(Level level, BlockPos pos, BlockState state) {
        this.playSound(state, SoundEvents.BARREL_OPEN);
        this.updateBlockState(state, true);
    }

    @Override
    protected void onClose(Level level, BlockPos pos, BlockState state) {
        this.playSound(state, SoundEvents.BARREL_CLOSE);
        this.updateBlockState(state, false);
    }

    private void updateBlockState(BlockState state, boolean open) {
        level.setBlock(this.getBlockPos(), state.setValue(BlockStateProperties.OPEN, open), Block.UPDATE_ALL);
    }
}
