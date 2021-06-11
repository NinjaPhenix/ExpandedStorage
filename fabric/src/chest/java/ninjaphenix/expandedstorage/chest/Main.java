package ninjaphenix.expandedstorage.chest;

import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.client.ChestBlockEntityRenderer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
        AtomicReference<Set<ChestBlock>> b = new AtomicReference<>();
        AtomicReference<Set<BlockItem>> i = new AtomicReference<>();
        Consumer<Set<ChestBlock>> registerBlocks = (blocks) -> {
            b.set(blocks);
            blocks.forEach(block -> Registry.register(Registry.BLOCK, block.blockId(), block));
        };
        Consumer<Set<BlockItem>> registerItems = (items) -> {
            i.set(items);
            items.forEach(item -> Registry.register(Registry.ITEM, ((ChestBlock) item.getBlock()).blockId(), item));
        };
        Consumer<BlockEntityType<ChestBlockEntity>> registerBlockEntityType = (blockEntityType) -> {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, ChestCommon.BLOCK_TYPE, blockEntityType);
            if (PlatformUtils.getInstance().isClient()) {
                Client.registerChestTextures(b.get());
                Client.registerItemRenderers(i.get());
            }
        };
        ChestCommon.registerContent(registerBlocks, registerItems, registerBlockEntityType, TagRegistry.block(new ResourceLocation("c", "wooden_chests")));
    }

    private static class Client {
        public static void registerChestTextures(Set<ChestBlock> blocks) {
            Set<ResourceLocation> textures = ChestCommon.registerChestTextures(blocks);
            ClientSpriteRegistryCallback.event(Sheets.CHEST_SHEET).register((atlasTexture, registry) -> textures.forEach(registry::register));

            BlockEntityRendererRegistry.INSTANCE.register(ChestCommon.getBlockEntityType(), ChestBlockEntityRenderer::new);
        }

        public static void registerItemRenderers(Set<BlockItem> items) {
            items.forEach(item -> {
                ChestBlockEntity renderEntity = new ChestBlockEntity(ChestCommon.getBlockEntityType(), BlockPos.ZERO, item.getBlock().defaultBlockState());
                BuiltinItemRendererRegistry.INSTANCE.register(item, (itemStack, transform, stack, source, light, overlay) ->
                        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(renderEntity, stack, source, light, overlay));
            });
            ChestBlockEntityRenderer.registerModelLayers();
        }
    }
}
