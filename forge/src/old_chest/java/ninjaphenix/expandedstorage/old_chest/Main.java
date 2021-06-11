package ninjaphenix.expandedstorage.old_chest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

import java.util.Collections;
import java.util.Set;

public class Main {
    public Main() {
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
        BlockBehaviour.Properties woodProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                            .strength(2.5F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties ironProperties = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL)
                                                                            .strength(5.0F, 6.0F)
                                                                            .sound(SoundType.METAL);
        BlockBehaviour.Properties goldProperties = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                                                                            .strength(3.0F, 6.0F)
                                                                            .sound(SoundType.METAL);
        BlockBehaviour.Properties diamondProperties = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND)
                                                                               .strength(5.0F, 6.0F)
                                                                               .sound(SoundType.METAL);
        BlockBehaviour.Properties obsidianProperties = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
                                                                                .strength(50.0F, 1200.0F);
        BlockBehaviour.Properties netheriteProperties = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
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
        blockEntityType.setRegistryName(OldChestCommon.BLOCK_TYPE);
        // Register content
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(BlockEntityType.class, (RegistryEvent.Register<BlockEntityType<?>> event) -> {
            event.getRegistry().register(blockEntityType);
            OldChestCommon.setBlockEntityType(blockEntityType);
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
        OldChestCommon.registerTabIcon(netheriteChestItem);
        OldChestCommon.registerUpgradeBehaviours();
    }

    private BlockItem oldChestItem(OpenableTier tier, OldChestBlock block) {
        Item.Properties itemProperties = tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB));
        BlockItem item = new BlockItem(block, itemProperties);
        item.setRegistryName(block.blockId());
        return item;
    }

    private OldChestBlock oldChestBlock(ResourceLocation id, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        tier.blockProperties().apply(properties);
        OldChestBlock block = new OldChestBlock(properties, id, tier.key(), stat, tier.slots());
        block.setRegistryName(id);
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }
}
