package ninjaphenix.expandedstorage.base.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner.BlockType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.base.internal_api.item.MutationMode;
import ninjaphenix.expandedstorage.chest.ChestCommon;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
import static net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.CHEST_TYPE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class StorageMutator extends Item {
    public StorageMutator(Item.Properties properties) {
        super(properties);
    }

    private static MutationMode getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("mode", Tag.TAG_BYTE)) {
            tag.putByte("mode", (byte) 0);
        }
        return MutationMode.from(tag.getByte("mode"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BarrelBlock) {
            return this.useModifierOnBlock(context, state, pos, BlockType.SINGLE);
        } else if (block instanceof AbstractChestBlock) {
            return this.useModifierOnBlock(context, state, pos, ChestBlock.getBlockType(state));
        } else {
            return this.useOnBlock(context, state, context.getClickedPos());
        }
    }

    protected InteractionResult useOnBlock(UseOnContext context, BlockState state, BlockPos pos) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        Block block = state.getBlock();
        if (block instanceof net.minecraft.world.level.block.AbstractChestBlock) {
            if (StorageMutator.getMode(stack) == MutationMode.ROTATE) {
                if (state.hasProperty(CHEST_TYPE)) {
                    ChestType chestType = state.getValue(CHEST_TYPE);
                    if (chestType != ChestType.SINGLE) {
                        if (!level.isClientSide()) {
                            BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                            BlockState otherState = level.getBlockState(otherPos);
                            level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_180).setValue(CHEST_TYPE, state.getValue(CHEST_TYPE).getOpposite()));
                            level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_180).setValue(CHEST_TYPE, otherState.getValue(CHEST_TYPE).getOpposite()));
                        }
                        //noinspection ConstantConditions
                        player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                        return InteractionResult.SUCCESS;
                    }
                }
                level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90));
                //noinspection ConstantConditions
                player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                return InteractionResult.SUCCESS;
            }
        }
        if (block instanceof net.minecraft.world.level.block.ChestBlock) {
            MutationMode mode = StorageMutator.getMode(stack);
            if (mode == MutationMode.MERGE) {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("pos")) {
                    BlockPos otherPos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                    BlockState otherState = level.getBlockState(otherPos);
                    if (otherState.getBlock() == state.getBlock() &&
                            otherState.getValue(HORIZONTAL_FACING) == state.getValue(HORIZONTAL_FACING) &&
                            otherState.getValue(CHEST_TYPE) == ChestType.SINGLE) {
                        if (!level.isClientSide()) {
                            BlockPos offset = otherPos.subtract(pos);
                            Direction direction = Direction.fromNormal(offset.getX(), offset.getY(), offset.getZ());
                            if (direction != null) {
                                CursedChestType type = ChestBlock.getChestType(state.getValue(HORIZONTAL_FACING), direction);
                                Predicate<BlockEntity> isRandomizable = b -> b instanceof RandomizableContainerBlockEntity;
                                convertContainer(level, state, pos, BaseApi.getInstance().getTieredBlock(ChestCommon.BLOCK_TYPE, Utils.WOOD_TIER.key()), Utils.WOOD_STACK_COUNT, type, isRandomizable);
                                convertContainer(level, otherState, otherPos, BaseApi.getInstance().getTieredBlock(ChestCommon.BLOCK_TYPE, Utils.WOOD_TIER.key()), Utils.WOOD_STACK_COUNT, type.getOpposite(), isRandomizable);
                                tag.remove("pos");
                                //noinspection ConstantConditions
                                player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.storage_mutator.merge_end"), true);
                            }
                        }
                        //noinspection ConstantConditions
                        player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    if (!level.isClientSide()) {
                        tag.put("pos", NbtUtils.writeBlockPos(pos));
                        //noinspection ConstantConditions
                        player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.storage_mutator.merge_start"), true);
                    }
                    //noinspection ConstantConditions
                    player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                    return InteractionResult.SUCCESS;
                }
            } else if (mode == MutationMode.SPLIT) {
                ChestType chestType = state.getValue(CHEST_TYPE);
                if (chestType != ChestType.SINGLE) {
                    if (!level.isClientSide()) {
                        BlockPos otherPos = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
                        BlockState otherState = level.getBlockState(otherPos);
                        level.setBlockAndUpdate(pos, state.setValue(CHEST_TYPE, ChestType.SINGLE));
                        level.setBlockAndUpdate(otherPos, otherState.setValue(CHEST_TYPE, ChestType.SINGLE));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (block instanceof net.minecraft.world.level.block.BarrelBlock) {
            if (StorageMutator.getMode(stack) == MutationMode.ROTATE) {
                if (!level.isClientSide()) {
                    Direction direction = state.getValue(FACING);
                    level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.from3DDataValue(direction.get3DDataValue() + 1)));
                }
                //noinspection ConstantConditions
                player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    private void convertContainer(Level level, BlockState state, BlockPos pos, Block block, int slotCount, @Nullable CursedChestType type, Predicate<BlockEntity> check) {
        BlockEntity targetBlockEntity = level.getBlockEntity(pos);
        if (check.test(targetBlockEntity)) {
            NonNullList<ItemStack> invData = NonNullList.withSize(slotCount, ItemStack.EMPTY);
            //noinspection ConstantConditions
            ContainerHelper.loadAllItems(targetBlockEntity.save(new CompoundTag()), invData);
            level.removeBlockEntity(pos);
            BlockState newState = block.defaultBlockState();
            if (state.hasProperty(WATERLOGGED)) {
                newState = newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
            }
            if (state.hasProperty(FACING)) {
                newState = newState.setValue(FACING, state.getValue(FACING));
            } else if (state.hasProperty(HORIZONTAL_FACING)) {
                newState = newState.setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING));
            }
            if (type != null) {
                newState = newState.setValue(ChestBlock.CURSED_CHEST_TYPE, type);
            }
            level.setBlockAndUpdate(pos, newState);
            BlockEntity newEntity = level.getBlockEntity(pos);
            //noinspection ConstantConditions
            newEntity.load(ContainerHelper.saveAllItems(newEntity.save(new CompoundTag()), invData));
        }
    }

    protected InteractionResult useModifierOnBlock(UseOnContext context, BlockState state, BlockPos pos, @SuppressWarnings("unused") BlockType type) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Block block = state.getBlock();
        switch (StorageMutator.getMode(context.getItemInHand())) {
            case MERGE -> {
                if (block instanceof AbstractChestBlock<?> chestBlock && state.getValue(ChestBlock.CURSED_CHEST_TYPE) == CursedChestType.SINGLE) {
                    CompoundTag tag = stack.getOrCreateTag();
                    if (tag.contains("pos")) {
                        BlockPos otherPos = NbtUtils.readBlockPos(tag.getCompound("pos"));
                        BlockState otherState = level.getBlockState(otherPos);
                        Direction facing = state.getValue(HORIZONTAL_FACING);
                        if (block == otherState.getBlock()
                                && facing == otherState.getValue(HORIZONTAL_FACING)
                                && otherState.getValue(AbstractChestBlock.CURSED_CHEST_TYPE) == CursedChestType.SINGLE) {
                            if (!level.isClientSide()) {
                                BlockPos offset = otherPos.subtract(pos);
                                Direction direction = Direction.fromNormal(offset.getX(), offset.getY(), offset.getZ());
                                if (direction != null) {
                                    CursedChestType chestType = AbstractChestBlock.getChestType(state.getValue(HORIZONTAL_FACING), direction);
                                    Predicate<BlockEntity> isStorage = b -> b instanceof AbstractOpenableStorageBlockEntity;
                                    this.convertContainer(level, state, pos, block, chestBlock.getSlotCount(), chestType, isStorage);
                                    this.convertContainer(level, otherState, otherPos, block, chestBlock.getSlotCount(), chestType.getOpposite(), isStorage);
                                    tag.remove("pos");
                                    //noinspection ConstantConditions
                                    player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.storage_mutator.merge_end"), true);
                                }
                            }
                            //noinspection ConstantConditions
                            player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        if (!level.isClientSide()) {
                            tag.put("pos", NbtUtils.writeBlockPos(pos));
                            //noinspection ConstantConditions
                            player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.storage_mutator.merge_start"), true);
                        }
                        //noinspection ConstantConditions
                        player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            case SPLIT -> {
                if (block instanceof AbstractChestBlock && state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE) != CursedChestType.SINGLE) {
                    if (!level.isClientSide()) {
                        BlockPos otherPos = pos.relative(AbstractChestBlock.getDirectionToAttached(state));
                        BlockState otherState = level.getBlockState(otherPos);
                        level.setBlockAndUpdate(pos, state.setValue(AbstractChestBlock.CURSED_CHEST_TYPE, CursedChestType.SINGLE));
                        level.setBlockAndUpdate(otherPos, otherState.setValue(AbstractChestBlock.CURSED_CHEST_TYPE, CursedChestType.SINGLE));
                    }
                    //noinspection ConstantConditions
                    player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                    return InteractionResult.SUCCESS;
                }
            }
            case ROTATE -> {
                if (state.hasProperty(FACING)) {
                    if (!level.isClientSide()) {
                        Direction direction = state.getValue(FACING);
                        level.setBlockAndUpdate(pos, state.setValue(FACING, Direction.from3DDataValue(direction.get3DDataValue() + 1)));
                    }
                    //noinspection ConstantConditions
                    player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                    return InteractionResult.SUCCESS;
                } else if (state.hasProperty(HORIZONTAL_FACING)) {
                    if (block instanceof AbstractChestBlock) {
                        if (!level.isClientSide()) {
                            switch (state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE)) {
                                case SINGLE -> level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90));
                                case TOP, BOTTOM -> {
                                    level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_90));
                                    BlockPos otherPos = pos.relative(AbstractChestBlock.getDirectionToAttached(state));
                                    BlockState otherState = level.getBlockState(otherPos);
                                    level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_90));
                                }
                                case FRONT, BACK, LEFT, RIGHT -> {
                                    level.setBlockAndUpdate(pos, state.rotate(CLOCKWISE_180).setValue(AbstractChestBlock.CURSED_CHEST_TYPE, state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE).getOpposite()));
                                    BlockPos otherPos = pos.relative(AbstractChestBlock.getDirectionToAttached(state));
                                    BlockState otherState = level.getBlockState(otherPos);
                                    level.setBlockAndUpdate(otherPos, otherState.rotate(CLOCKWISE_180).setValue(AbstractChestBlock.CURSED_CHEST_TYPE, otherState.getValue(AbstractChestBlock.CURSED_CHEST_TYPE).getOpposite()));
                                }
                            }
                        }
                        //noinspection ConstantConditions
                        player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = this.useModifierInAir(level, player, hand);
        if (result.getResult() == InteractionResult.SUCCESS) {
            player.getCooldowns().addCooldown(this, Utils.QUARTER_SECOND);
        }
        return result;
    }

    private InteractionResultHolder<ItemStack> useModifierInAir(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);
            CompoundTag tag = stack.getOrCreateTag();
            MutationMode nextMode = StorageMutator.getMode(stack).next();
            tag.putByte("mode", nextMode.toByte());
            if (tag.contains("pos")) {
                tag.remove("pos");
            }
            if (!level.isClientSide()) {
                player.displayClientMessage(new TranslatableComponent("tooltip.expandedstorage.storage_mutator.description_" + nextMode, Utils.ALT_USE), true);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        StorageMutator.getMode(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        StorageMutator.getMode(stack);
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(tab)) {
            stacks.add(this.getDefaultInstance());
        }
    }

    private MutableComponent getToolModeComponent(MutationMode mode) {
        return new TranslatableComponent("tooltip.expandedstorage.storage_mutator.tool_mode",
                new TranslatableComponent("tooltip.expandedstorage.storage_mutator." + mode));
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "item.expandedstorage.storage_mutator";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        MutationMode mode = StorageMutator.getMode(stack);
        list.add(this.getToolModeComponent(mode).withStyle(ChatFormatting.GRAY));
        list.add(Utils.translation("tooltip.expandedstorage.storage_mutator.description_" + mode, Utils.ALT_USE).withStyle(ChatFormatting.GRAY));
    }
}
