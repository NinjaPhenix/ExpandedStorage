package ninjaphenix.expandedstorage.shulkerboxes.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.shulkerboxes.ShulkerCommon;
import ninjaphenix.expandedstorage.shulkerboxes.block.misc.ShulkerBoxBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxBlock extends AbstractOpenableStorageBlock {
    public ShulkerBoxBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier, ResourceLocation openStat, int slots) {
        super(properties, blockId, blockTier, openStat, slots);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new ShulkerBoxBlockEntity(ShulkerCommon.getBlockEntityType(), this.blockId());
    }

    @Override
    public ResourceLocation blockType() {
        return ShulkerCommon.BLOCK_TYPE;
    }
}
