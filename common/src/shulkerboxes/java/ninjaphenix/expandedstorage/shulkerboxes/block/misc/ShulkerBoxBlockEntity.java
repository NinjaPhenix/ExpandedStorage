package ninjaphenix.expandedstorage.shulkerboxes.block.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;

public class ShulkerBoxBlockEntity extends AbstractOpenableStorageBlockEntity {
    public ShulkerBoxBlockEntity(BlockEntityType<?> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType, blockId);
    }
}
