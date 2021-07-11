package ninjaphenix.expandedstorage.old_chest.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.old_chest.OldChestCommon;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

public final class OldChestBlock extends AbstractChestBlock<OldChestBlockEntity> {
    public OldChestBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier,
                         ResourceLocation openStat, int slots) {
        super(properties, blockId, blockTier, openStat, slots);
    }

    @Override
    protected BlockEntityType<OldChestBlockEntity> blockEntityType() {
        return OldChestCommon.getBlockEntityType();
    }

    @Override
    public ResourceLocation blockType() {
        return OldChestCommon.BLOCK_TYPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter getter) {
        return new OldChestBlockEntity(OldChestCommon.getBlockEntityType(), this.blockId());
    }
}
