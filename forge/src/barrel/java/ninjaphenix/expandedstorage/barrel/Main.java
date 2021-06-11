package ninjaphenix.expandedstorage.barrel;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Collections;
import java.util.Set;

public final class Main {
    public Main() {
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
        BlockBehaviour.Properties ironProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                            .harvestTool(ToolType.AXE)
                                                                            .harvestLevel(Tiers.STONE.getLevel())
                                                                            .requiresCorrectToolForDrops()
                                                                            .strength(5.0F, 6.0F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties goldProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                            .harvestTool(ToolType.AXE)
                                                                            .harvestLevel(Tiers.STONE.getLevel())
                                                                            .requiresCorrectToolForDrops()
                                                                            .strength(3.0F, 6.0F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties diamondProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                               .harvestTool(ToolType.AXE)
                                                                               .harvestLevel(Tiers.IRON.getLevel())
                                                                               .requiresCorrectToolForDrops()
                                                                               .strength(5.0F, 6.0F)
                                                                               .sound(SoundType.WOOD);
        BlockBehaviour.Properties obsidianProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                                .harvestTool(ToolType.AXE)
                                                                                .harvestLevel(Tiers.DIAMOND.getLevel())
                                                                                .requiresCorrectToolForDrops()
                                                                                .strength(50.0F, 1200.0F)
                                                                                .sound(SoundType.WOOD);
        BlockBehaviour.Properties netheriteProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                                 .harvestTool(ToolType.AXE)
                                                                                 .harvestLevel(Tiers.DIAMOND.getLevel())
                                                                                 .requiresCorrectToolForDrops()
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
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{ironBarrelItem, goldBarrelItem, diamondBarrelItem, obsidianBarrelItem, netheriteBarrelItem});
        // Init block entity type
        BlockEntityType<BarrelBlockEntity> blockEntityType = new BlockEntityType<>(() -> new BarrelBlockEntity(BarrelCommon.getBlockEntityType(), null), Collections.unmodifiableSet(blocks), null);
        blockEntityType.setRegistryName(BarrelCommon.BLOCK_TYPE);
        // Register content
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(BlockEntityType.class, (RegistryEvent.Register<BlockEntityType<?>> event) -> {
            event.getRegistry().register(blockEntityType);
            BarrelCommon.setBlockEntityType(blockEntityType);
        });
        modEventBus.addGenericListener(Block.class, (RegistryEvent.Register<Block> event) -> {
            IForgeRegistry<Block> registry = event.getRegistry();
            blocks.forEach(registry::register);
        });
        modEventBus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            items.forEach(registry::register);
        });
        // Register chest module icon & upgrade behaviours
        BarrelCommon.registerTabIcon(netheriteBarrelItem);
        BarrelCommon.registerUpgradeBehaviours(BlockTags.createOptional(new ResourceLocation("forge", "barrels/wooden")));
        // Do client side stuff
        if (PlatformUtils.getInstance().isClient()) {
            modEventBus.addListener((FMLClientSetupEvent event) -> {
                blocks.forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped()));
            });
        }
    }

    private BarrelBlock barrelBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        BarrelBlock block = new BarrelBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots());
        block.setRegistryName(blockId);
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private BlockItem barrelItem(OpenableTier tier, BarrelBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        BlockItem item = new BlockItem(block, itemProperties);
        item.setRegistryName(block.blockId());
        return item;
    }
}
