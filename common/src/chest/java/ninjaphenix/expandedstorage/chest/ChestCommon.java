package ninjaphenix.expandedstorage.chest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.base.internal_api.tier.OpenableTier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.internal_api.ChestApi;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ChestCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("cursed_chest");
    private static final int ICON_SUITABILITY = 1000;
    private static BlockEntityType<ChestBlockEntity> blockEntityType;

    private ChestCommon() {

    }

    static void registerContent(Consumer<Set<ChestBlock>> blockReg,
                                Consumer<Set<BlockItem>> itemReg,
                                Consumer<BlockEntityType<ChestBlockEntity>> blockEntityTypeConsumer,
                                net.minecraft.tags.Tag<Block> woodenChestTag) {
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
        BlockBehaviour.Properties woodProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                                                                            .strength(2.5F)
                                                                            .sound(SoundType.WOOD);
        BlockBehaviour.Properties pumpkinProperties = BlockBehaviour.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE)
                                                                               .strength(1.0F)
                                                                               .sound(SoundType.WOOD);
        BlockBehaviour.Properties christmasProperties = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
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
        // Init and register blocks
        ChestBlock woodChestBlock = ChestCommon.chestBlock(Utils.resloc("wood_chest"), woodOpenStat, woodTier, woodProperties);
        ChestBlock pumpkinChestBlock = ChestCommon.chestBlock(Utils.resloc("pumpkin_chest"), pumpkinOpenStat, pumpkinTier, pumpkinProperties);
        ChestBlock christmasChestBlock = ChestCommon.chestBlock(Utils.resloc("christmas_chest"), christmasOpenStat, christmasTier, christmasProperties);
        ChestBlock ironChestBlock = ChestCommon.chestBlock(Utils.resloc("iron_chest"), ironOpenStat, ironTier, ironProperties);
        ChestBlock goldChestBlock = ChestCommon.chestBlock(Utils.resloc("gold_chest"), goldOpenStat, goldTier, goldProperties);
        ChestBlock diamondChestBlock = ChestCommon.chestBlock(Utils.resloc("diamond_chest"), diamondOpenStat, diamondTier, diamondProperties);
        ChestBlock obsidianChestBlock = ChestCommon.chestBlock(Utils.resloc("obsidian_chest"), obsidianOpenStat, obsidianTier, obsidianProperties);
        ChestBlock netheriteChestBlock = ChestCommon.chestBlock(Utils.resloc("netherite_chest"), netheriteOpenStat, netheriteTier, netheriteProperties);
        Set<ChestBlock> blocks = ImmutableSet.copyOf(new ChestBlock[]{woodChestBlock, pumpkinChestBlock, christmasChestBlock, ironChestBlock, goldChestBlock, diamondChestBlock, obsidianChestBlock, netheriteChestBlock});
        blockReg.accept(blocks);
        // Init and register items
        BlockItem woodChestItem = ChestCommon.chestItem(woodTier, woodChestBlock);
        BlockItem pumpkinChestItem = ChestCommon.chestItem(pumpkinTier, pumpkinChestBlock);
        BlockItem christmasChestItem = ChestCommon.chestItem(christmasTier, christmasChestBlock);
        BlockItem ironChestItem = ChestCommon.chestItem(ironTier, ironChestBlock);
        BlockItem goldChestItem = ChestCommon.chestItem(goldTier, goldChestBlock);
        BlockItem diamondChestItem = ChestCommon.chestItem(diamondTier, diamondChestBlock);
        BlockItem obsidianChestItem = ChestCommon.chestItem(obsidianTier, obsidianChestBlock);
        BlockItem netheriteChestItem = ChestCommon.chestItem(netheriteTier, netheriteChestBlock);
        Set<BlockItem> items = ImmutableSet.copyOf(new BlockItem[]{woodChestItem, pumpkinChestItem, christmasChestItem, ironChestItem, goldChestItem, diamondChestItem, obsidianChestItem, netheriteChestItem});
        itemReg.accept(items);
        // Init and register block entity type
        BlockEntityType<ChestBlockEntity> blockEntityType = PlatformUtils.getInstance().createBlockEntityType((pos, state) -> new ChestBlockEntity(ChestCommon.getBlockEntityType(), pos, state), Collections.unmodifiableSet(blocks), null);
        ChestCommon.blockEntityType = blockEntityType;
        blockEntityTypeConsumer.accept(blockEntityType);
        // Register chest module icon & upgrade behaviours
        BaseApi.getInstance().offerTabIcon(netheriteChestItem, ChestCommon.ICON_SUITABILITY);
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof ChestBlock || woodenChestTag.contains(block);
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, ChestCommon::tryUpgradeBlock);
    }

    public static BlockEntityType<ChestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    private static ChestBlock chestBlock(ResourceLocation blockId, ResourceLocation stat, OpenableTier tier, BlockBehaviour.Properties properties) {
        ChestBlock block = new ChestBlock(tier.blockProperties().apply(properties.dynamicShape()), blockId, tier.key(), stat, tier.slots());
        BaseApi.getInstance().registerTieredBlock(block);
        return block;
    }

    private static BlockItem chestItem(OpenableTier tier, ChestBlock block) {
        return new BlockItem(block, tier.itemProperties().apply(new Item.Properties().tab(Utils.TAB)));
    }

    static Set<ResourceLocation> registerChestTextures(Set<ChestBlock> blocks) {
        Set<ResourceLocation> textures = new HashSet<>();
        blocks.forEach(block -> {
            ResourceLocation id = block.blockId();
            ChestApi.INSTANCE.declareChestTextures(
                    id, Utils.resloc("entity/" + id.getPath() + "/single"),
                    Utils.resloc("entity/" + id.getPath() + "/left"),
                    Utils.resloc("entity/" + id.getPath() + "/right"),
                    Utils.resloc("entity/" + id.getPath() + "/top"),
                    Utils.resloc("entity/" + id.getPath() + "/bottom"),
                    Utils.resloc("entity/" + id.getPath() + "/front"),
                    Utils.resloc("entity/" + id.getPath() + "/back"));
            Arrays.stream(CursedChestType.values())
                  .map(type -> ChestApi.INSTANCE.getChestTexture(id, type))
                  .forEach(textures::add);
        });
        return textures;
    }

    private static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack handStack = context.getItemInHand();
        if (state.getBlock() instanceof ChestBlock block) {
            if (OldChestBlock.getBlockType(state) == DoubleBlockCombiner.BlockType.SINGLE) {
                ChestCommon.upgradeSingleBlock(level, state, pos, from, to);
                handStack.shrink(1);
                return true;
            } else if (handStack.getCount() > 1 || (player != null && player.isCreative())) {
                BlockPos otherPos = pos.relative(OldChestBlock.getDirectionToAttached(state));
                BlockState otherState = level.getBlockState(otherPos);
                ChestCommon.upgradeSingleBlock(level, state, pos, from, to);
                ChestCommon.upgradeSingleBlock(level, otherState, otherPos, from, to);
                handStack.shrink(2);
                return true;
            }
        } else {
            if (net.minecraft.world.level.block.ChestBlock.getBlockType(state) == DoubleBlockCombiner.BlockType.SINGLE) {
                ChestCommon.upgradeSingleBlock(level, state, pos, from, to);
                handStack.shrink(1);
                return true;
            } else if (handStack.getCount() > 1 || (player != null && player.isCreative())) {
                BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                BlockState otherState = level.getBlockState(otherPos);
                ChestCommon.upgradeSingleBlock(level, state, pos, from, to);
                ChestCommon.upgradeSingleBlock(level, otherState, otherPos, from, to);
                handStack.shrink(2);
                return true;
            }
        }

        return false;
    }

    private static void upgradeSingleBlock(Level level, BlockState state, BlockPos pos, ResourceLocation from, ResourceLocation to) {
        Block block = state.getBlock();
        boolean condition = block instanceof ChestBlock;
        if ((condition && ((ChestBlock) block).blockTier() == from) || !condition && from == Utils.WOOD_TIER.key()) {
            var toBlock = (AbstractOpenableStorageBlock) BaseApi.getInstance().getTieredBlock(ChestCommon.BLOCK_TYPE, to);
            var inventory = NonNullList.withSize(toBlock.getSlotCount(), ItemStack.EMPTY);
            var tag = level.getBlockEntity(pos).save(new CompoundTag());
            var code = LockCode.fromTag(tag);
            ContainerHelper.loadAllItems(tag, inventory);
            level.removeBlockEntity(pos);
            // Needs fixing up to check for vanilla states.
            var newState = toBlock.defaultBlockState()
                                  .setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
                                  .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
            if (state.hasProperty(ChestBlock.CURSED_CHEST_TYPE)) {
                newState = newState.setValue(ChestBlock.CURSED_CHEST_TYPE, state.getValue(ChestBlock.CURSED_CHEST_TYPE));
            } else if (state.hasProperty(BlockStateProperties.CHEST_TYPE)) {
                ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
                newState = newState.setValue(ChestBlock.CURSED_CHEST_TYPE, type == ChestType.LEFT ? CursedChestType.RIGHT : type == ChestType.RIGHT ? CursedChestType.LEFT : CursedChestType.SINGLE);
            }
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
