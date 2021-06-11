package ninjaphenix.expandedstorage.old_chest;

import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

import java.util.Set;

public final class Main implements ModuleInitializer {
    private static void registerBlockEntityType(BlockEntityType<OldChestBlockEntity> blockEntityType) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, OldChestCommon.BLOCK_TYPE, blockEntityType);
    }

    private static void registerBlocks(Set<OldChestBlock> blocks) {
        blocks.forEach(block -> Registry.register(Registry.BLOCK, block.blockId(), block));
    }

    private static void registerItems(Set<BlockItem> items) {
        items.forEach(item -> Registry.register(Registry.ITEM, ((OldChestBlock) item.getBlock()).blockId(), item));
    }

    @Override
    public void initialize() {
        OldChestCommon.registerContent(Main::registerBlocks, Main::registerItems, Main::registerBlockEntityType);
    }
}
