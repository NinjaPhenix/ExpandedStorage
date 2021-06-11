package ninjaphenix.expandedstorage.old_chest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class OldChestCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("old_cursed_chest");
    private static final int ICON_SUITABILITY = 999;
    private static BlockEntityType<OldChestBlockEntity> blockEntityType;

    private OldChestCommon() {

    }

    public static BlockEntityType<OldChestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void registerContent(Consumer<Set<OldChestBlock>> blockReg,
                                Consumer<Set<BlockItem>> itemReg,
                                Consumer<BlockEntityType<OldChestBlockEntity>> blockEntityTypeConsumer) {
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
        OldChestBlock woodChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_wood_chest"), woodOpenStat, woodTier, woodProperties);
        OldChestBlock ironChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_iron_chest"), ironOpenStat, ironTier, ironProperties);
        OldChestBlock goldChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_gold_chest"), goldOpenStat, goldTier, goldProperties);
        OldChestBlock diamondChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_diamond_chest"), diamondOpenStat, diamondTier, diamondProperties);
        OldChestBlock obsidianChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_obsidian_chest"), obsidianOpenStat, obsidianTier, obsidianProperties);
        OldChestBlock netheriteChestBlock = OldChestCommon.oldChestBlock(Utils.resloc("old_netherite_chest"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<OldChestBlock> blocks = ImmutableSet.copyOf(new OldChestBlock[]{woodChestBlock, ironChestBlock, goldChestBlock, diamondChestBlock, obsidianChestBlock, netheriteChestBlock});
        blockReg.accept(blocks);
        // Init items
        BlockItem woodChestItem = OldChestCommon.oldChestItem(woodTier, woodChestBlock);
        BlockItem ironChestItem = OldChestCommon.oldChestItem(ironTier, ironChestBlock);
        BlockItem goldChestItem = OldChestCommon.oldChestItem(goldTier, goldChestBlock);
        BlockItem diamondChestItem = OldChestCommon.oldChestItem(diamondTier, diamondChestBlock);
        BlockItem obsidianChestItem = OldChestCommon.oldChestItem(obsidianTier, obsidianChestBlock);
        BlockItem netheriteChestItem = OldChestCommon.oldChestItem(netheriteTier, netheriteChestBlock);
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{woodChestItem, ironChestItem, goldChestItem, diamondChestItem, obsidianChestItem, netheriteChestItem});
        itemReg.accept(items);
        // Init block entity type
        BlockEntityType<OldChestBlockEntity> blockEntityType = PlatformUtils.getInstance().createBlockEntityType((pos, state) -> new OldChestBlockEntity(OldChestCommon.getBlockEntityType(), pos, state), Collections.unmodifiableSet(blocks), null);
        OldChestCommon.blockEntityType = blockEntityType;
        blockEntityTypeConsumer.accept(blockEntityType);
        // Register chest module icon & upgrade behaviours
        BaseApi.getInstance().offerTabIcon(netheriteChestItem, OldChestCommon.ICON_SUITABILITY);
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof OldChestBlock;
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, OldChestCommon::tryUpgradeBlock);
    }

    private static BlockItem oldChestItem(OpenableTier tier, OldChestBlock block) {
        return new BlockItem(block, tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB)));
    }

    private static OldChestBlock oldChestBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        OldChestBlock block = new OldChestBlock(tier.blockProperties().apply(properties), blockId, tier.key(), stat, tier.slots());
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack handStack = context.getItemInHand();
        if (OldChestBlock.getBlockType(state) == DoubleBlockCombiner.BlockType.SINGLE) {
            OldChestCommon.upgradeSingleBlock(level, state, pos, from, to);
            handStack.shrink(1);
            return true;
        } else if (handStack.getCount() > 1 || (player != null && player.isCreative())) {
            BlockPos otherPos = pos.relative(OldChestBlock.getDirectionToAttached(state));
            BlockState otherState = level.getBlockState(otherPos);
            OldChestCommon.upgradeSingleBlock(level, state, pos, from, to);
            OldChestCommon.upgradeSingleBlock(level, otherState, otherPos, from, to);
            handStack.shrink(2);
            return true;
        }
        return false;
    }

    private static void upgradeSingleBlock(Level level, BlockState state, BlockPos pos, ResourceLocation from, ResourceLocation to) {
        if (((OldChestBlock) state.getBlock()).blockTier() == from) {
            var toBlock = (AbstractOpenableStorageBlock) BaseApi.getInstance().getTieredBlock(OldChestCommon.BLOCK_TYPE, to);
            var inventory = NonNullList.withSize(toBlock.getSlotCount(), ItemStack.EMPTY);
            var tag = level.getBlockEntity(pos).save(new CompoundTag());
            var code = LockCode.fromTag(tag);
            ContainerHelper.loadAllItems(tag, inventory);
            level.removeBlockEntity(pos);
            var newState = toBlock.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)).setValue(OldChestBlock.CURSED_CHEST_TYPE, state.getValue(OldChestBlock.CURSED_CHEST_TYPE));
            if (level.setBlockAndUpdate(pos, newState)) {
                var newEntity = (AbstractOpenableStorageBlockEntity) level.getBlockEntity(pos);
                var newTag = newEntity.save(new CompoundTag());
                ContainerHelper.saveAllItems(newTag, inventory);
                code.addToTag(newTag);
                newEntity.load(newTag);
            }
        }
    }
}
