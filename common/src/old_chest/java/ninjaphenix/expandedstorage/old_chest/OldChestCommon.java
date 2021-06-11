package ninjaphenix.expandedstorage.old_chest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

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

    static void setBlockEntityType(BlockEntityType<OldChestBlockEntity> type) {
        if (OldChestCommon.blockEntityType == null) {
            OldChestCommon.blockEntityType = type;
        }
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
                newEntity.load(newState, newTag);
            }
        }
    }

    public static void registerUpgradeBehaviours() {
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof OldChestBlock;
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, OldChestCommon::tryUpgradeBlock);
    }

    static void registerTabIcon(Item item) {
        BaseApi.getInstance().offerTabIcon(item, OldChestCommon.ICON_SUITABILITY);
    }
}
