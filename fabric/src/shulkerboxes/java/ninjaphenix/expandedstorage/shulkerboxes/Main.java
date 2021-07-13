package ninjaphenix.expandedstorage.shulkerboxes;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.shulkerboxes.block.ShulkerBoxBlock;
import ninjaphenix.expandedstorage.shulkerboxes.block.misc.ShulkerBoxBlockEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Main implements ModuleInitializer {
    @Override
    public void initialize() {
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
        BlockBehaviour.StatePredicate isShulkerBoxClosed = (state, level, pos) -> {
            //noinspection CodeBlock2Expr
            return level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity entity && entity.isClosed();
        };
        Set<ShulkerBoxBlock> blocks = new HashSet<>();
        // Init block properties
        for (int colorId = -1; colorId < 16; colorId++) {
            String prefix = "";
            MaterialColor color = Material.SHULKER_SHELL.getColor();
            if (colorId != -1) {
                DyeColor dye = DyeColor.byId(colorId);
                prefix = dye.getName() + "_";
                color = dye.getMaterialColor();
            }
            BlockBehaviour.Properties ironProperties = FabricBlockSettings.of(Material.SHULKER_SHELL, color)
                                                                          .breakByTool(FabricToolTags.PICKAXES, Tiers.STONE.getLevel())
                                                                          .requiresCorrectToolForDrops() // End of FBS
                                                                          .strength(5.0F, 6.0F)
                                                                          .dynamicShape()
                                                                          .noCollission()
                                                                          .isSuffocating(isShulkerBoxClosed)
                                                                          .isViewBlocking(isShulkerBoxClosed);
            BlockBehaviour.Properties goldProperties = FabricBlockSettings.of(Material.SHULKER_SHELL, color)
                                                                          .breakByTool(FabricToolTags.PICKAXES, Tiers.STONE.getLevel())
                                                                          .requiresCorrectToolForDrops() // End of FBS
                                                                          .strength(3.0F, 6.0F)
                                                                          .dynamicShape()
                                                                          .noCollission()
                                                                          .isSuffocating(isShulkerBoxClosed)
                                                                          .isViewBlocking(isShulkerBoxClosed);
            BlockBehaviour.Properties diamondProperties = FabricBlockSettings.of(Material.SHULKER_SHELL, color)
                                                                             .breakByTool(FabricToolTags.PICKAXES, Tiers.IRON.getLevel())
                                                                             .requiresCorrectToolForDrops() // End of FBS
                                                                             .strength(5.0F, 6.0F)
                                                                             .dynamicShape()
                                                                             .noCollission()
                                                                             .isSuffocating(isShulkerBoxClosed)
                                                                             .isViewBlocking(isShulkerBoxClosed);
            BlockBehaviour.Properties obsidianProperties = FabricBlockSettings.of(Material.SHULKER_SHELL, color)
                                                                              .breakByTool(FabricToolTags.PICKAXES, Tiers.DIAMOND.getLevel())
                                                                              .requiresCorrectToolForDrops() // End of FBS
                                                                              .strength(50.0F, 1200.0F)
                                                                              .dynamicShape()
                                                                              .noCollission()
                                                                              .isSuffocating(isShulkerBoxClosed)
                                                                              .isViewBlocking(isShulkerBoxClosed);
            BlockBehaviour.Properties netheriteProperties = FabricBlockSettings.of(Material.SHULKER_SHELL, color)
                                                                               .breakByTool(FabricToolTags.PICKAXES, Tiers.DIAMOND.getLevel())
                                                                               .requiresCorrectToolForDrops() // End of FBS
                                                                               .strength(50.0F, 1200.0F)
                                                                               .dynamicShape()
                                                                               .noCollission()
                                                                               .isSuffocating(isShulkerBoxClosed)
                                                                               .isViewBlocking(isShulkerBoxClosed);
            ShulkerBoxBlock ironShulkerBox = this.shulkerBoxBlock(Utils.resloc(prefix + "iron_shulker_box"), ironOpenStat, ironTier, ironProperties);
            ShulkerBoxBlock goldShulkerBox = this.shulkerBoxBlock(Utils.resloc(prefix + "gold_shulker_box"), goldOpenStat, goldTier, goldProperties);
            ShulkerBoxBlock diamondShulkerBox = this.shulkerBoxBlock(Utils.resloc(prefix + "diamond_shulker_box"), diamondOpenStat, diamondTier, diamondProperties);
            ShulkerBoxBlock obsidianShulkerBox = this.shulkerBoxBlock(Utils.resloc(prefix + "obsidian_shulker_box"), obsidianOpenStat, obsidianTier, obsidianProperties);
            ShulkerBoxBlock netheriteShulkerBox = this.shulkerBoxBlock(Utils.resloc(prefix + "netherite_shulker_box"), netheriteOpenStat, netheriteTier, netheriteProperties);
            blocks.add(ironShulkerBox);
            blocks.add(goldShulkerBox);
            blocks.add(diamondShulkerBox);
            blocks.add(obsidianShulkerBox);
            blocks.add(netheriteShulkerBox);
           this.shulkerBoxItem(ironTier, ironShulkerBox);
           this.shulkerBoxItem(goldTier, goldShulkerBox);
           this.shulkerBoxItem(diamondTier, diamondShulkerBox);
           this.shulkerBoxItem(obsidianTier, obsidianShulkerBox);
           this.shulkerBoxItem(netheriteTier, netheriteShulkerBox);
        }
        // Init block entity type
        BlockEntityType<ShulkerBoxBlockEntity> blockEntityType = new BlockEntityType<>(() -> new ShulkerBoxBlockEntity(ShulkerCommon.getBlockEntityType(), null), Collections.unmodifiableSet(blocks), null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ShulkerCommon.BLOCK_TYPE, blockEntityType);
        ShulkerCommon.setBlockEntityType(blockEntityType);
        // Register chest module icon & upgrade behaviours
        ShulkerCommon.registerTabIcon(Registry.BLOCK.get(Utils.resloc("netherite_shulker_box")).asItem());
        ShulkerCommon.registerUpgradeBehaviours(null);
    }

    private ShulkerBoxBlock shulkerBoxBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        ShulkerBoxBlock block = Registry.register(Registry.BLOCK, blockId, new ShulkerBoxBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots()));
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private void shulkerBoxItem(OpenableTier tier, ShulkerBoxBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        Registry.register(Registry.ITEM, block.blockId(), new BlockItem(block, itemProperties));
    }
}
