package ninjaphenix.expandedstorage.chest.block.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;

public final class ChestBlockEntity extends AbstractOpenableStorageBlockEntity implements TickableBlockEntity {
    private int viewerCount;
    private float lastAnimationAngle;
    private float animationAngle;
    private int ticksOpen;

    public ChestBlockEntity(BlockEntityType<ChestBlockEntity> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType, blockId);
    }

    private static int tickViewerCount(Level level, ChestBlockEntity entity, int ticksOpen, int x, int y, int z, int viewCount) {
        if (!level.isClientSide() && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) {
            return AbstractOpenableStorageBlockEntity.countViewers(level, entity, x, y, z);
        }
        return viewCount;
    }

    @Override
    public boolean triggerEvent(int event, int value) {
        if (event == ChestBlock.SET_OPEN_COUNT_EVENT) {
            viewerCount = value;
            return true;
        }
        return super.triggerEvent(event, value);
    }

    // Client only
    public float getLidOpenness(float f) {
        return Mth.lerp(f, lastAnimationAngle, animationAngle);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void tick() {
        viewerCount = tickViewerCount(level, this, ++ticksOpen, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), viewerCount);
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) {
            this.playSound(SoundEvents.CHEST_OPEN);
        }
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F) {
            animationAngle = Mth.clamp(animationAngle + (viewerCount > 0 ? 0.1F : -0.1F), 0, 1);
            if (animationAngle < 0.5F && lastAnimationAngle >= 0.5F) {
                this.playSound(SoundEvents.CHEST_CLOSE);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void playSound(SoundEvent soundEvent) {
        BlockState state = this.getBlockState();
        DoubleBlockCombiner.BlockType mergeType = ChestBlock.getBlockType(state);
        Vec3 soundPos;
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) {
            soundPos = Vec3.atCenterOf(worldPosition);
        } else if (mergeType == DoubleBlockCombiner.BlockType.FIRST) {
            soundPos = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(ChestBlock.getDirectionToAttached(state).getNormal()).scale(0.5D));
        } else {
            return;
        }
        level.playSound(null, soundPos.x(), soundPos.y(), soundPos.z(), soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        if (viewerCount < 0) {
            viewerCount = 0;
        }
        viewerCount++;
        this.onInvOpenOrClose();
    }

    @Override
    public void stopOpen(final Player player) {
        if (player.isSpectator()) {
            return;
        }
        viewerCount--;
        this.onInvOpenOrClose();
    }

    @SuppressWarnings("ConstantConditions")
    private void onInvOpenOrClose() {
        if (this.getBlockState().getBlock() instanceof ChestBlock block) {
            level.blockEvent(worldPosition, block, ChestBlock.SET_OPEN_COUNT_EVENT, viewerCount);
            level.updateNeighborsAt(worldPosition, block);
        }
    }
}
