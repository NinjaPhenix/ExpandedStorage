package ninjaphenix.expandedstorage.barrel;

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
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Collections;
import java.util.Set;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
        // Init tiers
        OpenableTier ironTier = new OpenableTier(Utils.IRON_TIER, BarrelCommon.BLOCK_TYPE, Utils.IRON_STACK_COUNT);
        OpenableTier goldTier = new OpenableTier(Utils.GOLD_TIER, BarrelCommon.BLOCK_TYPE, Utils.GOLD_STACK_COUNT);
        OpenableTier diamondTier = new OpenableTier(Utils.DIAMOND_TIER, BarrelCommon.BLOCK_TYPE, Utils.DIAMOND_STACK_COUNT);
        OpenableTier obsidianTier = new OpenableTier(Utils.OBSIDIAN_TIER, BarrelCommon.BLOCK_TYPE, Utils.OBSIDIAN_STACK_COUNT);
        OpenableTier netheriteTier = new OpenableTier(Utils.NETHERITE_TIER, BarrelCommon.BLOCK_TYPE, Utils.NETHERITE_STACK_COUNT);
        // Init and register opening stats
        ResourceLocation ironOpenStat = BaseCommon.registerStat(Utils.resloc("open_iron_barrel"));
        ResourceLocation goldOpenStat = BaseCommon.registerStat(Utils.resloc("open_gold_barrel"));
        ResourceLocation diamondOpenStat = BaseCommon.registerStat(Utils.resloc("open_diamond_barrel"));
        ResourceLocation obsidianOpenStat = BaseCommon.registerStat(Utils.resloc("open_obsidian_barrel"));
        ResourceLocation netheriteOpenStat = BaseCommon.registerStat(Utils.resloc("open_netherite_barrel"));
        // Init block properties
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
        BarrelBlock ironBarrelBlock = this.barrelBlock(Utils.resloc("iron_barrel"), ironOpenStat, ironTier, ironProperties);
        BarrelBlock goldBarrelBlock = this.barrelBlock(Utils.resloc("gold_barrel"), goldOpenStat, goldTier, goldProperties);
        BarrelBlock diamondBarrelBlock = this.barrelBlock(Utils.resloc("diamond_barrel"), diamondOpenStat, diamondTier, diamondProperties);
        BarrelBlock obsidianBarrelBlock = this.barrelBlock(Utils.resloc("obsidian_barrel"), obsidianOpenStat, obsidianTier, obsidianProperties);
        BarrelBlock netheriteBarrelBlock = this.barrelBlock(Utils.resloc("netherite_barrel"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<BarrelBlock> blocks = ImmutableSet.copyOf(new BarrelBlock[]{ironBarrelBlock, goldBarrelBlock, diamondBarrelBlock, obsidianBarrelBlock, netheriteBarrelBlock});
        // Init items
        BlockItem ironBarrelItem = this.barrelItem(ironTier, ironBarrelBlock);
        BlockItem goldBarrelItem = this.barrelItem(goldTier, goldBarrelBlock);
        BlockItem diamondBarrelItem = this.barrelItem(diamondTier, diamondBarrelBlock);
        BlockItem obsidianBarrelItem = this.barrelItem(obsidianTier, obsidianBarrelBlock);
        BlockItem netheriteBarrelItem = this.barrelItem(netheriteTier, netheriteBarrelBlock);
        //Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{ironBarrelItem, goldBarrelItem, diamondBarrelItem, obsidianBarrelItem, netheriteBarrelItem});
        // Init block entity type
        BlockEntityType<BarrelBlockEntity> blockEntityType = new BlockEntityType<>(() -> new BarrelBlockEntity(BarrelCommon.getBlockEntityType(), null), Collections.unmodifiableSet(blocks), null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, BarrelCommon.BLOCK_TYPE, blockEntityType);
        BarrelCommon.setBlockEntityType(blockEntityType);
        // Register chest module icon & upgrade behaviours
        BarrelCommon.registerTabIcon(netheriteBarrelItem);
        BarrelCommon.registerUpgradeBehaviours(TagRegistry.block(new ResourceLocation("c", "wooden_barrels")));
        // Do client side stuff
        if (PlatformUtils.getInstance().isClient()) {
            blocks.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()));
        }
    }

    private BarrelBlock barrelBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        BarrelBlock block = Registry.register(Registry.BLOCK, blockId, new BarrelBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots()));
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private BlockItem barrelItem(OpenableTier tier, BarrelBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        return Registry.register(Registry.ITEM, block.blockId(), new BlockItem(block, itemProperties));
    }
}
