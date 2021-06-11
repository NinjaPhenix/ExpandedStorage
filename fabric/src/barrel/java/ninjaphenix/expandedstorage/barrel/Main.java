package ninjaphenix.expandedstorage.barrel;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Set;

public final class Main implements ModuleInitializer {
    private static void registerBlockEntityType(BlockEntityType<BarrelBlockEntity> blockEntityType) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, BarrelCommon.BLOCK_TYPE, blockEntityType);
    }

    private static void registerBlocks(Set<BarrelBlock> blocks) {
        blocks.forEach(block -> Registry.register(Registry.BLOCK, block.blockId(), block));
        if (PlatformUtils.getInstance().isClient()) {
            blocks.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()));
        }
    }

    private static void registerItems(Set<BlockItem> items) {
        items.forEach(item -> Registry.register(Registry.ITEM, ((BarrelBlock) item.getBlock()).blockId(), item));
    }

    @Override
    public void initialize() {
        BarrelCommon.registerContent(Main::registerBlocks, Main::registerItems, Main::registerBlockEntityType, TagRegistry.block(new ResourceLocation("c", "wooden_barrels")));
    }
}
