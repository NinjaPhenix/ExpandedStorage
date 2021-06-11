package ninjaphenix.expandedstorage.old_chest;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
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
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

import java.util.Collections;
import java.util.Set;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
        // Init tiers
        OpenableTier woodTier = new OpenableTier(Utils.WOOD_TIER, OldChestCommon.BLOCK_TYPE, Utils.WOOD_STACK_COUNT);
        OpenableTier ironTier = new OpenableTier(Utils.IRON_TIER, OldChestCommon.BLOCK_TYPE, Utils.IRON_STACK_COUNT);
        OpenableTier goldTier = new OpenableTier(Utils.GOLD_TIER, OldChestCommon.BLOCK_TYPE, Utils.GOLD_STACK_COUNT);
        OpenableTier diamondTier = new OpenableTier(Utils.DIAMOND_TIER, OldChestCommon.BLOCK_TYPE, Utils.DIAMOND_STACK_COUNT);
        OpenableTier obsidianTier = new OpenableTier(Utils.OBSIDIAN_TIER, OldChestCommon.BLOCK_TYPE, Utils.OBSIDIAN_STACK_COUNT);
        OpenableTier netheriteTier = new OpenableTier(Utils.NETHERITE_TIER, OldChestCommon.BLOCK_TYPE, Utils.NETHERITE_STACK_COUNT);
        // Init and register opening stats
        ResourceLocation woodOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_wood_chest"));
        ResourceLocation ironOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_iron_chest"));
        ResourceLocation goldOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_gold_chest"));
        ResourceLocation diamondOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_diamond_chest"));
        ResourceLocation obsidianOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_obsidian_chest"));
        ResourceLocation netheriteOpenStat = BaseCommon.registerStat(Utils.resloc("open_old_netherite_chest"));
        // Init block properties
        BlockBehaviour.Properties woodProperties = FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
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
        // Init blocks
        OldChestBlock woodChestBlock = this.oldChestBlock(Utils.resloc("old_wood_chest"), woodOpenStat, woodTier, woodProperties);
        OldChestBlock ironChestBlock = this.oldChestBlock(Utils.resloc("old_iron_chest"), ironOpenStat, ironTier, ironProperties);
        OldChestBlock goldChestBlock = this.oldChestBlock(Utils.resloc("old_gold_chest"), goldOpenStat, goldTier, goldProperties);
        OldChestBlock diamondChestBlock = this.oldChestBlock(Utils.resloc("old_diamond_chest"), diamondOpenStat, diamondTier, diamondProperties);
        OldChestBlock obsidianChestBlock = this.oldChestBlock(Utils.resloc("old_obsidian_chest"), obsidianOpenStat, obsidianTier, obsidianProperties);
        OldChestBlock netheriteChestBlock = this.oldChestBlock(Utils.resloc("old_netherite_chest"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<OldChestBlock> blocks = ImmutableSet.copyOf(new OldChestBlock[]{woodChestBlock, ironChestBlock, goldChestBlock, diamondChestBlock, obsidianChestBlock, netheriteChestBlock});
        // Init items
        BlockItem woodChestItem = this.oldChestItem(woodTier, woodChestBlock);
        BlockItem ironChestItem = this.oldChestItem(ironTier, ironChestBlock);
        BlockItem goldChestItem = this.oldChestItem(goldTier, goldChestBlock);
        BlockItem diamondChestItem = this.oldChestItem(diamondTier, diamondChestBlock);
        BlockItem obsidianChestItem = this.oldChestItem(obsidianTier, obsidianChestBlock);
        BlockItem netheriteChestItem = this.oldChestItem(netheriteTier, netheriteChestBlock);
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{woodChestItem, ironChestItem, goldChestItem, diamondChestItem, obsidianChestItem, netheriteChestItem});
        // Init block entity type
        BlockEntityType<OldChestBlockEntity> blockEntityType = new BlockEntityType<>(() -> new OldChestBlockEntity(OldChestCommon.getBlockEntityType(), null), Collections.unmodifiableSet(blocks), null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, OldChestCommon.BLOCK_TYPE, blockEntityType);
        OldChestCommon.setBlockEntityType(blockEntityType);
        // Register chest module icon & upgrade behaviours
        OldChestCommon.registerTabIcon(netheriteChestItem);
        OldChestCommon.registerUpgradeBehaviours();
    }

    private BlockItem oldChestItem(OpenableTier tier, OldChestBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        return Registry.register(Registry.ITEM, block.blockId(), new BlockItem(block, itemProperties));
    }

    private OldChestBlock oldChestBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        tier.blockProperties().apply(properties);
        OldChestBlock block = Registry.register(Registry.BLOCK, blockId, new OldChestBlock(properties, blockId, tier.key(), stat, tier.slots()));
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }
}
