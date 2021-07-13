package ninjaphenix.expandedstorage.shulkerboxes;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.client.renderer.RenderType;
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
import ninjaphenix.expandedstorage.base.wrappers.PlatformUtils;
import ninjaphenix.expandedstorage.shulkerboxes.block.ShulkerBoxBlock;
import ninjaphenix.expandedstorage.shulkerboxes.block.misc.ShulkerBoxBlockEntity;

import java.util.Collections;
import java.util.Set;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
        // todo: different shulker box colours
        // Init tiers
        OpenableTier ironTier = new OpenableTier(Utils.IRON_TIER, ShulkerCommon.BLOCK_TYPE, Utils.IRON_STACK_COUNT);
        OpenableTier goldTier = new OpenableTier(Utils.GOLD_TIER, ShulkerCommon.BLOCK_TYPE, Utils.GOLD_STACK_COUNT);
        OpenableTier diamondTier = new OpenableTier(Utils.DIAMOND_TIER, ShulkerCommon.BLOCK_TYPE, Utils.DIAMOND_STACK_COUNT);
        OpenableTier obsidianTier = new OpenableTier(Utils.OBSIDIAN_TIER, ShulkerCommon.BLOCK_TYPE, Utils.OBSIDIAN_STACK_COUNT);
        OpenableTier netheriteTier = new OpenableTier(Utils.NETHERITE_TIER, ShulkerCommon.BLOCK_TYPE, Utils.NETHERITE_STACK_COUNT);
        // Init and register opening stats
        ResourceLocation ironOpenStat = BaseCommon.registerStat(Utils.resloc("open_iron_shulker_box"));
        ResourceLocation goldOpenStat = BaseCommon.registerStat(Utils.resloc("open_gold_shulker_box"));
        ResourceLocation diamondOpenStat = BaseCommon.registerStat(Utils.resloc("open_diamond_shulker_box"));
        ResourceLocation obsidianOpenStat = BaseCommon.registerStat(Utils.resloc("open_obsidian_shulker_box"));
        ResourceLocation netheriteOpenStat = BaseCommon.registerStat(Utils.resloc("open_netherite_shulker_box"));
        // Init block properties
        // todo: fix properties.
        BlockBehaviour.Properties ironProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                      .breakByTool(FabricToolTags.AXES, Tiers.STONE.getLevel())
                                                                      .requiresCorrectToolForDrops() // End of FBS
                                                                      .strength(5.0F, 6.0F)
                                                                      .sound(SoundType.WOOD);
        BlockBehaviour.Properties goldProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                      .breakByTool(FabricToolTags.AXES, Tiers.STONE.getLevel())
                                                                      .requiresCorrectToolForDrops() // End of FBS
                                                                      .strength(3.0F, 6.0F)
                                                                      .sound(SoundType.WOOD);
        BlockBehaviour.Properties diamondProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                         .breakByTool(FabricToolTags.AXES, Tiers.IRON.getLevel())
                                                                         .requiresCorrectToolForDrops() // End of FBS
                                                                         .strength(5.0F, 6.0F)
                                                                         .sound(SoundType.WOOD);
        BlockBehaviour.Properties obsidianProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                          .breakByTool(FabricToolTags.AXES, Tiers.DIAMOND.getLevel())
                                                                          .requiresCorrectToolForDrops() // End of FBS
                                                                          .strength(50.0F, 1200.0F)
                                                                          .sound(SoundType.WOOD);
        BlockBehaviour.Properties netheriteProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
                                                                           .breakByTool(FabricToolTags.AXES, Tiers.DIAMOND.getLevel())
                                                                           .requiresCorrectToolForDrops() // End of FBS
                                                                           .strength(50.0F, 1200.0F)
                                                                           .sound(SoundType.WOOD);
        // Init blocks
        ShulkerBoxBlock ironShulkerBox = this.shulkerBoxBlock(Utils.resloc("iron_shulker_box"), ironOpenStat, ironTier, ironProperties);
        ShulkerBoxBlock goldShulkerBox = this.shulkerBoxBlock(Utils.resloc("gold_shulker_box"), goldOpenStat, goldTier, goldProperties);
        ShulkerBoxBlock diamondShulkerBox = this.shulkerBoxBlock(Utils.resloc("diamond_shulker_box"), diamondOpenStat, diamondTier, diamondProperties);
        ShulkerBoxBlock obsidianShulkerBox = this.shulkerBoxBlock(Utils.resloc("obsidian_shulker_box"), obsidianOpenStat, obsidianTier, obsidianProperties);
        ShulkerBoxBlock netheriteShulkerBox = this.shulkerBoxBlock(Utils.resloc("netherite_shulker_box"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<ShulkerBoxBlock> blocks = ImmutableSet.copyOf(new ShulkerBoxBlock[]{ironShulkerBox, goldShulkerBox, diamondShulkerBox, obsidianShulkerBox, netheriteShulkerBox});
        // Init items
        BlockItem ironShulkerBoxItem = this.shulkerBoxItem(ironTier, ironShulkerBox);
        BlockItem goldShulkerBoxItem = this.shulkerBoxItem(goldTier, goldShulkerBox);
        BlockItem diamondShulkerBoxItem = this.shulkerBoxItem(diamondTier, diamondShulkerBox);
        BlockItem obsidianShulkerBoxItem = this.shulkerBoxItem(obsidianTier, obsidianShulkerBox);
        BlockItem netheriteShulkerBoxItem = this.shulkerBoxItem(netheriteTier, netheriteShulkerBox);
        //Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{ironShulkerBoxItem, goldShulkerBoxItem, diamondShulkerBoxItem, obsidianShulkerBoxItem, netheriteShulkerBoxItem});
        // Init block entity type
        BlockEntityType<ShulkerBoxBlockEntity> blockEntityType = new BlockEntityType<>(() -> new ShulkerBoxBlockEntity(ShulkerCommon.getBlockEntityType(), null), Collections.unmodifiableSet(blocks), null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ShulkerCommon.BLOCK_TYPE, blockEntityType);
        ShulkerCommon.setBlockEntityType(blockEntityType);
        // Register chest module icon & upgrade behaviours
        ShulkerCommon.registerTabIcon(netheriteShulkerBoxItem);
        ShulkerCommon.registerUpgradeBehaviours(null);
        // Do client side stuff
        if (PlatformUtils.getInstance().isClient()) {
            blocks.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()));
        }
    }

    private ShulkerBoxBlock shulkerBoxBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        ShulkerBoxBlock block = Registry.register(Registry.BLOCK, blockId, new ShulkerBoxBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots()));
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private BlockItem shulkerBoxItem(OpenableTier tier, ShulkerBoxBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        return Registry.register(Registry.ITEM, block.blockId(), new BlockItem(block, itemProperties));
    }
}
