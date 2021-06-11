package ninjaphenix.expandedstorage.old_chest.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;

public final class OldChestBlockEntity extends AbstractOpenableStorageBlockEntity {
    public OldChestBlockEntity(BlockEntityType<OldChestBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state, ((OldChestBlock) state.getBlock()).blockId());
    }
}
