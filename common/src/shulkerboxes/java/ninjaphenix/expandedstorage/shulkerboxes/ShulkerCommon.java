package ninjaphenix.expandedstorage.shulkerboxes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.shulkerboxes.block.ShulkerBoxBlock;
import ninjaphenix.expandedstorage.shulkerboxes.block.misc.ShulkerBoxBlockEntity;

import java.util.function.Predicate;

public final class ShulkerCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("shulkerboxes");
    private static final int ICON_SUITABILITY = 997;
    private static BlockEntityType<ShulkerBoxBlockEntity> blockEntityType;

    private ShulkerCommon() {

    }

    public static BlockEntityType<ShulkerBoxBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void setBlockEntityType(BlockEntityType<ShulkerBoxBlockEntity> blockEntityType) {
        if (ShulkerCommon.blockEntityType == null) {
            ShulkerCommon.blockEntityType = blockEntityType;
        }
    }

    public static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        //Level level = context.getLevel();
        //BlockPos pos = context.getClickedPos();
        //BlockState state = level.getBlockState(pos);
        //Block block = state.getBlock();
        //boolean isExpandedStorageBarrel = block instanceof BarrelBlock;
        //var containerSize = !isExpandedStorageBarrel ? Utils.WOOD_STACK_COUNT : ((BarrelBlock) BaseApi.getInstance().getTieredBlock(BarrelCommon.BLOCK_TYPE, ((BarrelBlock) block).blockTier())).getSlotCount();
        //if (isExpandedStorageBarrel && ((BarrelBlock) block).blockTier() == from || !isExpandedStorageBarrel && from == Utils.WOOD_TIER.key()) {
        //    var blockEntity = level.getBlockEntity(pos);
        //    var tag = blockEntity.save(new CompoundTag());
        //    boolean verifiedSize = blockEntity instanceof Container container && container.getContainerSize() == containerSize;
        //    if (!verifiedSize) { // Cannot verify container size, we'll let it upgrade if it has or has less than 27 items
        //        if (tag.contains("Items", Utils.NBT_LIST_TYPE)) {
        //            var items = tag.getList("Items", Utils.NBT_COMPOUND_TYPE);
        //            if (items.size() <= containerSize) {
        //                verifiedSize = true;
        //            }
        //        }
        //    }
        //    if (verifiedSize) {
        //        var toBlock = (AbstractOpenableStorageBlock) BaseApi.getInstance().getTieredBlock(BarrelCommon.BLOCK_TYPE, to);
        //        var inventory = NonNullList.withSize(toBlock.getSlotCount(), ItemStack.EMPTY);
        //        var code = LockCode.fromTag(tag);
        //        ContainerHelper.loadAllItems(tag, inventory);
        //        level.removeBlockEntity(pos);
        //        var newState = toBlock.defaultBlockState().setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
        //        if (level.setBlockAndUpdate(pos, newState)) {
        //            var newEntity = (AbstractOpenableStorageBlockEntity) level.getBlockEntity(pos);
        //            var newTag = newEntity.save(new CompoundTag());
        //            ContainerHelper.saveAllItems(newTag, inventory);
        //            code.addToTag(newTag);
        //            newEntity.load(newState, newTag);
        //            context.getItemInHand().shrink(1);
        //            return true;
        //        }
        //    }
        //}
        return false;
    }

    public static void registerTabIcon(BlockItem item) {
        BaseApi.getInstance().offerTabIcon(item, ShulkerCommon.ICON_SUITABILITY);
    }

    public static void registerUpgradeBehaviours(net.minecraft.tags.Tag<Block> tag) {
        // todo: make a tag for shulker boxes?
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof ShulkerBoxBlock || block instanceof net.minecraft.world.level.block.ShulkerBoxBlock || tag != null && tag.contains(block);
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, ShulkerCommon::tryUpgradeBlock);
    }
}
