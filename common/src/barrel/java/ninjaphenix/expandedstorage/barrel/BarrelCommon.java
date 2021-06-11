package ninjaphenix.expandedstorage.barrel;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class BarrelCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("barrel");
    private static final int ICON_SUITABILITY = 998;
    private static BlockEntityType<BarrelBlockEntity> blockEntityType;

    private BarrelCommon() {

    }

    public static BlockEntityType<BarrelBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void registerContent(Consumer<Set<BarrelBlock>> blockReg,
                                Consumer<Set<BlockItem>> itemReg,
                                Consumer<BlockEntityType<BarrelBlockEntity>> blockEntityTypeConsumer,
                                net.minecraft.tags.Tag<Block> woodenBarrelTag) {
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
                                                                            .strength(5.0F, 6.0F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties goldProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                            .strength(3.0F, 6.0F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties diamondProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                               .strength(5.0F, 6.0F)
                                                                               .sound(SoundType.WOOD);
        BlockBehaviour.Properties obsidianProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                                .strength(50.0F, 1200.0F)
                                                                                .sound(SoundType.WOOD);
        BlockBehaviour.Properties netheriteProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                                 .strength(50.0F, 1200.0F)
                                                                                 .sound(SoundType.WOOD);
        // Init blocks
        BarrelBlock ironBarrelBlock = BarrelCommon.barrelBlock(Utils.resloc("iron_barrel"), ironOpenStat, ironTier, ironProperties);
        BarrelBlock goldBarrelBlock = BarrelCommon.barrelBlock(Utils.resloc("gold_barrel"), goldOpenStat, goldTier, goldProperties);
        BarrelBlock diamondBarrelBlock = BarrelCommon.barrelBlock(Utils.resloc("diamond_barrel"), diamondOpenStat, diamondTier, diamondProperties);
        BarrelBlock obsidianBarrelBlock = BarrelCommon.barrelBlock(Utils.resloc("obsidian_barrel"), obsidianOpenStat, obsidianTier, obsidianProperties);
        BarrelBlock netheriteBarrelBlock = BarrelCommon.barrelBlock(Utils.resloc("netherite_barrel"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<BarrelBlock> blocks = ImmutableSet.copyOf(new BarrelBlock[]{ironBarrelBlock, goldBarrelBlock, diamondBarrelBlock, obsidianBarrelBlock, netheriteBarrelBlock});
        blockReg.accept(blocks);
        // Init items
        BlockItem ironBarrelItem = BarrelCommon.barrelItem(ironTier, ironBarrelBlock);
        BlockItem goldBarrelItem = BarrelCommon.barrelItem(goldTier, goldBarrelBlock);
        BlockItem diamondBarrelItem = BarrelCommon.barrelItem(diamondTier, diamondBarrelBlock);
        BlockItem obsidianBarrelItem = BarrelCommon.barrelItem(obsidianTier, obsidianBarrelBlock);
        BlockItem netheriteBarrelItem = BarrelCommon.barrelItem(netheriteTier, netheriteBarrelBlock);
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{ironBarrelItem, goldBarrelItem, diamondBarrelItem, obsidianBarrelItem, netheriteBarrelItem});
        itemReg.accept(items);
        // Init block entity type
        BlockEntityType<BarrelBlockEntity> blockEntityType = PlatformUtils.getInstance().createBlockEntityType((pos, state) -> new BarrelBlockEntity(BarrelCommon.getBlockEntityType(), pos, state), Collections.unmodifiableSet(blocks), null);
        BarrelCommon.blockEntityType = blockEntityType;
        blockEntityTypeConsumer.accept(blockEntityType);
        // Register chest module icon & upgrade behaviours
        BaseApi.getInstance().offerTabIcon(netheriteBarrelItem, BarrelCommon.ICON_SUITABILITY);
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof BarrelBlock || woodenBarrelTag.contains(block);
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, BarrelCommon::tryUpgradeBlock);
    }

    private static BarrelBlock barrelBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        BarrelBlock block = new BarrelBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots());
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private static BlockItem barrelItem(OpenableTier tier, BarrelBlock block) {
        return new BlockItem(block, tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB)));
    }

    public static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        boolean condition = block instanceof BarrelBlock;
        if ((condition && ((BarrelBlock) block).blockTier() == from) || !condition && from == Utils.WOOD_TIER.key()) {
            var toBlock = (AbstractOpenableStorageBlock) BaseApi.getInstance().getTieredBlock(BarrelCommon.BLOCK_TYPE, to);
            var inventory = NonNullList.withSize(toBlock.getSlotCount(), ItemStack.EMPTY);
            var tag = level.getBlockEntity(pos).save(new CompoundTag());
            var code = LockCode.fromTag(tag);
            ContainerHelper.loadAllItems(tag, inventory);
            level.removeBlockEntity(pos);
            var newState = toBlock.defaultBlockState().setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
            if (level.setBlockAndUpdate(pos, newState)) {
                var newEntity = (AbstractOpenableStorageBlockEntity) level.getBlockEntity(pos);
                var newTag = newEntity.save(new CompoundTag());
                ContainerHelper.saveAllItems(newTag, inventory);
                code.addToTag(newTag);
                newEntity.load(newTag);
                context.getItemInHand().shrink(1);
                return true;
            }
        }
        return false;
    }
}
