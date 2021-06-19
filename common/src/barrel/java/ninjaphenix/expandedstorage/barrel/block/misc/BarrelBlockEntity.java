package ninjaphenix.expandedstorage.barrel.block.misc;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;

public class BarrelBlockEntity extends AbstractOpenableStorageBlockEntity {
    private int viewerCount;

    public BarrelBlockEntity(BlockEntityType<BarrelBlockEntity> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType, blockId);
    }

    public void checkViewerCount() {
        //noinspection ConstantConditions
        viewerCount = AbstractOpenableStorageBlockEntity.countViewers(level, this, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        if (viewerCount > 0) {
            this.scheduleViewCountCheck();
        } else {
            BlockState state = this.getBlockState();
            if (!(state.getBlock() instanceof BarrelBlock)) {
                this.setRemoved();
                return;
            }
            if (state.getValue(BlockStateProperties.OPEN)) {
                this.playSound(state, SoundEvents.BARREL_CLOSE);
                this.setOpen(state, false);
            }
        }
    }

    //@Override
    //public void startOpen(Player player) {
    //    if (!player.isSpectator()) {
    //        if (viewerCount < 0) {
    //            viewerCount = 0;
    //        }
    //        ++viewerCount;
    //        BlockState state = this.getBlockState();
    //        if (!state.getValue(BlockStateProperties.OPEN)) {
    //            this.playSound(state, SoundEvents.BARREL_OPEN);
    //            this.setOpen(state, true);
    //        }
    //        this.scheduleViewCountCheck();
    //    }
    //}

    //@Override
    //public void stopOpen(Player player) {
    //    if (!player.isSpectator()) {
    //        --viewerCount;
    //    }
    //}

    private void setOpen(BlockState state, boolean open) {
        //noinspection ConstantConditions
        level.setBlock(this.getBlockPos(), state.setValue(BlockStateProperties.OPEN, open), 3);
    }

    private void playSound(BlockState state, SoundEvent sound) {
        Vec3i facingVector = state.getValue(BlockStateProperties.FACING).getNormal();
        double X = worldPosition.getX() + 0.5D + facingVector.getX() / 2.0D;
        double Y = worldPosition.getY() + 0.5D + facingVector.getY() / 2.0D;
        double Z = worldPosition.getZ() + 0.5D + facingVector.getZ() / 2.0D;
        //noinspection ConstantConditions
        level.playSound(null, X, Y, Z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    private void scheduleViewCountCheck() {
        //noinspection ConstantConditions
        level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
    }
}
