package ninjaphenix.expandedstorage.chest;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.client.ChestBlockEntityRenderer;

import java.util.Set;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
        // Init tiers
        OpenableTier woodTier = new OpenableTier(Utils.WOOD_TIER, ChestCommon.BLOCK_TYPE, Utils.WOOD_STACK_COUNT);
        OpenableTier pumpkinTier = woodTier;
        OpenableTier christmasTier = woodTier;
        OpenableTier ironTier = new OpenableTier(Utils.IRON_TIER, ChestCommon.BLOCK_TYPE, Utils.IRON_STACK_COUNT);
        OpenableTier goldTier = new OpenableTier(Utils.GOLD_TIER, ChestCommon.BLOCK_TYPE, Utils.GOLD_STACK_COUNT);
        OpenableTier diamondTier = new OpenableTier(Utils.DIAMOND_TIER, ChestCommon.BLOCK_TYPE, Utils.DIAMOND_STACK_COUNT);
        OpenableTier obsidianTier = new OpenableTier(Utils.OBSIDIAN_TIER, ChestCommon.BLOCK_TYPE, Utils.OBSIDIAN_STACK_COUNT);
        OpenableTier netheriteTier = new OpenableTier(Utils.NETHERITE_TIER, ChestCommon.BLOCK_TYPE, Utils.NETHERITE_STACK_COUNT);
        // Init and register opening stats
        ResourceLocation woodOpenStat = BaseCommon.registerStat(Utils.resloc("open_wood_chest"));
        ResourceLocation pumpkinOpenStat = BaseCommon.registerStat(Utils.resloc("open_pumpkin_chest"));
        ResourceLocation christmasOpenStat = BaseCommon.registerStat(Utils.resloc("open_christmas_chest"));
        ResourceLocation ironOpenStat = BaseCommon.registerStat(Utils.resloc("open_iron_chest"));
        ResourceLocation goldOpenStat = BaseCommon.registerStat(Utils.resloc("open_gold_chest"));
        ResourceLocation diamondOpenStat = BaseCommon.registerStat(Utils.resloc("open_diamond_chest"));
        ResourceLocation obsidianOpenStat = BaseCommon.registerStat(Utils.resloc("open_obsidian_chest"));
        ResourceLocation netheriteOpenStat = BaseCommon.registerStat(Utils.resloc("open_netherite_chest"));
        // Init block properties
        BlockBehaviour.Properties woodProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                      .breakByTool(FabricToolTags.AXES, Tiers.WOOD.getLevel())
                                                                      .strength(2.5F) // End of FBS
                                                                      .sound(SoundType.WOOD);
        BlockBehaviour.Properties pumpkinProperties = FabricBlockSettings.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE)
                                                                         .breakByTool(FabricToolTags.AXES, Tiers.WOOD.getLevel())
                                                                         .strength(1.0F) // End of FBS
                                                                         .sound(SoundType.WOOD);
        BlockBehaviour.Properties christmasProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                           .breakByTool(FabricToolTags.AXES, Tiers.WOOD.getLevel())
                                                                           .strength(2.5F) // End of FBS
                                                                           .sound(SoundType.WOOD);
        BlockBehaviour.Properties ironProperties = FabricBlockSettings.of(Material.METAL, MaterialColor.METAL)
                                                                      .breakByTool(FabricToolTags.PICKAXES, Tiers.STONE.getLevel())
                                                                      .requiresCorrectToolForDrops() // End of FBS
                                                                      .strength(5.0F, 6.0F)
                                                                      .sound(SoundType.METAL);
        BlockBehaviour.Properties goldProperties = FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD)
                                                                      .breakByTool(FabricToolTags.PICKAXES, Tiers.STONE.getLevel())
                                                                      .requiresCorrectToolForDrops() // End of FBS
                                                                      .strength(3.0F, 6.0F)
                                                                      .sound(SoundType.METAL);
        BlockBehaviour.Properties diamondProperties = FabricBlockSettings.of(Material.METAL, MaterialColor.DIAMOND)
                                                                         .breakByTool(FabricToolTags.PICKAXES, Tiers.IRON.getLevel())
                                                                         .requiresCorrectToolForDrops() // End of FBS
                                                                         .strength(5.0F, 6.0F)
                                                                         .sound(SoundType.METAL);
        BlockBehaviour.Properties obsidianProperties = FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                                          .breakByTool(FabricToolTags.PICKAXES, Tiers.DIAMOND.getLevel())
                                                                          .requiresCorrectToolForDrops() // End of FBS
                                                                          .strength(50.0F, 1200.0F);
        BlockBehaviour.Properties netheriteProperties = FabricBlockSettings.of(Material.METAL, MaterialColor.COLOR_BLACK)
                                                                           .breakByTool(FabricToolTags.PICKAXES, Tiers.DIAMOND.getLevel())
                                                                           .requiresCorrectToolForDrops() // End of FBS
                                                                           .strength(50.0F, 1200.0F)
                                                                           .sound(SoundType.NETHERITE_BLOCK);
        // Init and register blocks
        ChestBlock woodChestBlock = this.chestBlock(Utils.resloc("wood_chest"), woodOpenStat, woodTier, woodProperties);
        ChestBlock pumpkinChestBlock = this.chestBlock(Utils.resloc("pumpkin_chest"), pumpkinOpenStat, pumpkinTier, pumpkinProperties);
        ChestBlock christmasChestBlock = this.chestBlock(Utils.resloc("christmas_chest"), christmasOpenStat, christmasTier, christmasProperties);
        ChestBlock ironChestBlock = this.chestBlock(Utils.resloc("iron_chest"), ironOpenStat, ironTier, ironProperties);
        ChestBlock goldChestBlock = this.chestBlock(Utils.resloc("gold_chest"), goldOpenStat, goldTier, goldProperties);
        ChestBlock diamondChestBlock = this.chestBlock(Utils.resloc("diamond_chest"), diamondOpenStat, diamondTier, diamondProperties);
        ChestBlock obsidianChestBlock = this.chestBlock(Utils.resloc("obsidian_chest"), obsidianOpenStat, obsidianTier, obsidianProperties);
        ChestBlock netheriteChestBlock = this.chestBlock(Utils.resloc("netherite_chest"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<ChestBlock> blocks = ImmutableSet.copyOf(new ChestBlock[]{woodChestBlock, pumpkinChestBlock, christmasChestBlock, ironChestBlock, goldChestBlock, diamondChestBlock, obsidianChestBlock, netheriteChestBlock});
        // Init and register items
        BlockItem woodChestItem = this.chestItem(woodTier, woodChestBlock);
        BlockItem pumpkinChestItem = this.chestItem(pumpkinTier, pumpkinChestBlock);
        BlockItem christmasChestItem = this.chestItem(christmasTier, christmasChestBlock);
        BlockItem ironChestItem = this.chestItem(ironTier, ironChestBlock);
        BlockItem goldChestItem = this.chestItem(goldTier, goldChestBlock);
        BlockItem diamondChestItem = this.chestItem(diamondTier, diamondChestBlock);
        BlockItem obsidianChestItem = this.chestItem(obsidianTier, obsidianChestBlock);
        BlockItem netheriteChestItem = this.chestItem(netheriteTier, netheriteChestBlock);
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{woodChestItem, pumpkinChestItem, christmasChestItem, ironChestItem, goldChestItem, diamondChestItem, obsidianChestItem, netheriteChestItem});
        // Init and register block entity type
        BlockEntityType<ChestBlockEntity> blockEntityType = new BlockEntityType<>(() -> new ChestBlockEntity(ChestCommon.getBlockEntityType(), null), ImmutableSet.copyOf(blocks), null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ChestCommon.BLOCK_TYPE, blockEntityType);
        ChestCommon.setBlockEntityType(blockEntityType);
        // Register chest module icon & upgrade behaviours
        ChestCommon.registerTabIcon(netheriteChestItem);
        ChestCommon.registerUpgradeBehaviours(TagRegistry.block(new ResourceLocation("c", "wooden_chests")));
        // Do client side stuff
        if (PlatformUtils.getInstance().isClient()) {
            Client.initialise(blocks, items);
        }
    }

    private ChestBlock chestBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        tier.blockProperties().apply(properties.dynamicShape());
        ChestBlock block = Registry.register(Registry.BLOCK, blockId, new ChestBlock(properties, blockId, tier.key(), stat, tier.slots()));
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private BlockItem chestItem(OpenableTier tier, ChestBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        return Registry.register(Registry.ITEM, block.blockId(), new BlockItem(block, itemProperties));
    }

    private static class Client {
        private static void initialise(Set<ChestBlock> blocks, Set<BlockItem> items) {
            Set<ResourceLocation> textures = ChestCommon.registerChestTextures(blocks);
            ClientSpriteRegistryCallback.event(Sheets.CHEST_SHEET).register((atlasTexture, registry) -> textures.forEach(registry::register));

            BlockEntityRendererRegistry.INSTANCE.register(ChestCommon.getBlockEntityType(), ChestBlockEntityRenderer::new);

            items.forEach(item -> {
                ChestBlockEntity renderEntity = new ChestBlockEntity(ChestCommon.getBlockEntityType(), ((ChestBlock) item.getBlock()).blockId());
                BuiltinItemRendererRegistry.INSTANCE.register(item, (itemStack, transform, stack, source, light, overlay) ->
                        BlockEntityRenderDispatcher.instance.renderItem(renderEntity, stack, source, light, overlay));
            });
        }
    }
}
