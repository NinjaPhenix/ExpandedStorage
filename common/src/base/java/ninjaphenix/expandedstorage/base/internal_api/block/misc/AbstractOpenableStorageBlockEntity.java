package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

@Internal
@Experimental
public abstract class AbstractOpenableStorageBlockEntity extends AbstractStorageBlockEntity implements WorldlyContainer {
    private final ResourceLocation blockId;
    private final ContainerOpenersCounter openersCounter;
    protected Component containerName;
    private int slots;
    private NonNullList<ItemStack> inventory;
    private int[] slotsForFace;

    public AbstractOpenableStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, ResourceLocation blockId) {
        super(blockEntityType, pos, state);
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                AbstractOpenableStorageBlockEntity.this.onOpen(level, pos, state);
            }

            @Override
            protected void onClose(Level level, BlockPos pos, BlockState state) {
                AbstractOpenableStorageBlockEntity.this.onClose(level, pos, state);
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int i, int j) {
                // Does this respect method overriding?
                AbstractOpenableStorageBlockEntity.this.openerCountChanged(level, pos, state, i, j);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof AbstractContainerMenu_<?>) {
                    return AbstractOpenableStorageBlockEntity.this.isOwnContainer(((AbstractContainerMenu_<?>) player.containerMenu).getContainer());
                } else {
                    return false;
                }
            }
        };
        this.blockId = blockId;
        if (blockId != null) {
            this.initialise(blockId);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int i, int j) {

    }

    protected boolean isOwnContainer(Container container) {
        return container == this;
    }

    protected void onOpen(Level level, BlockPos pos, BlockState state) {

    }

    protected void onClose(Level level, BlockPos pos, BlockState state) {

    }

    public final void recheckOpen() {
        openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
    }

    private void initialise(ResourceLocation blockId) {
        if (Registry.BLOCK.get(blockId) instanceof AbstractOpenableStorageBlock block) {
            slots = block.getSlotCount();
            slotsForFace = new int[slots];
            Arrays.setAll(slotsForFace, IntUnaryOperator.identity());
            inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
            containerName = block.getContainerName();
        }
    }

    @Override
    public Component getDefaultName() {
        return containerName;
    }

    public final ResourceLocation getBlockId() {
        return blockId;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.getBlockState().getBlock() instanceof AbstractOpenableStorageBlock block) {
            this.initialise(block.blockId());
            ContainerHelper.loadAllItems(tag, inventory);
        } else {
            throw new IllegalStateException("Block Entity attached to wrong block.");
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        return tag;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return slotsForFace;
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction face) {
        return this.canPlaceItem(slot, stack);
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return slots;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = ContainerHelper.removeItem(inventory, slot, count);
        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 64;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }
}
