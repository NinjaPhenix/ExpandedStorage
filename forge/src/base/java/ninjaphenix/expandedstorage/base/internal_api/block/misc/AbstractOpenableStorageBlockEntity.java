package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Internal
@Experimental
public abstract class AbstractOpenableStorageBlockEntity extends AbstractStorageBlockEntity implements ICapabilityProvider {
    private final ResourceLocation blockId;
    protected Component containerName;
    private int slots;
    private NonNullList<ItemStack> inventory;
    private LazyOptional<IItemHandler> itemHandler;
    private final Supplier<Container> container = Suppliers.memoize(() -> new Container() {
        @Override
        public int getContainerSize() {
            return slots;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return inventory.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = ContainerHelper.removeItem(inventory, slot, amount);
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
        public void setChanged() {
            AbstractOpenableStorageBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return AbstractOpenableStorageBlockEntity.this.canContinueUse(player);
        }

        @Override
        public void clearContent() {
            inventory.clear();
        }

        @Override
        public void startOpen(Player player) {
            AbstractOpenableStorageBlockEntity.this.startOpen(player);
        }

        @Override
        public void stopOpen(Player player) {
            AbstractOpenableStorageBlockEntity.this.stopOpen(player);
        }
    });

    public AbstractOpenableStorageBlockEntity(BlockEntityType<?> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType);
        this.blockId = blockId;
        if (blockId != null) {
            this.initialise(blockId);
        }
    }

    protected void startOpen(Player player) {

    }

    protected void stopOpen(Player player) {

    }

    protected static int countViewers(Level level, Container container, int x, int y, int z) {
        return level.getEntitiesOfClass(Player.class, new AABB(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6)).stream()
                    .filter(player -> player.containerMenu instanceof AbstractContainerMenu_<?>)
                    .map(player -> ((AbstractContainerMenu_<?>) player.containerMenu).getContainer())
                    .filter(openContainer -> openContainer == container ||
                            openContainer instanceof CompoundContainer compoundContainer && compoundContainer.contains(container))
                    .mapToInt(inv -> 1).sum();
    }

    public Container getContainerWrapper() {
        return container.get();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) {
                itemHandler = LazyOptional.of(() -> this.createItemHandler(this.getLevel(), this.getBlockState(), this.getBlockPos(), side));
            }
            return itemHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    @NotNull
    protected IItemHandler createItemHandler(Level level, BlockState state, BlockPos pos, @Nullable Direction side) {
        return AbstractOpenableStorageBlockEntity.createGenericItemHandler(this);
    }

    public static IItemHandler createGenericItemHandler(AbstractOpenableStorageBlockEntity entity) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return entity.slots;
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return entity.inventory.get(slot);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                var stackInSlot = entity.inventory.get(slot);
                if (stackInSlot.isEmpty()) {
                    if (!simulate) {
                        entity.inventory.set(slot, stack);
                        entity.setChanged();
                    }
                } else if(ItemHandlerHelper.canItemStacksStack(stackInSlot, stack)) {
                    var limit = Math.min(stackInSlot.getMaxStackSize(), 64);
                    var diff = limit - stackInSlot.getCount();
                    if (diff != 0) {
                        if (stack.getCount() > diff) {
                            if (!simulate) {
                                stackInSlot.setCount(limit);
                                entity.setChanged();
                            }
                            return simulate ? stack.copy().split(stack.getCount() - diff) : stack.split(stack.getCount() - diff);
                        } else {
                            if (!simulate) {
                                stackInSlot.setCount(stackInSlot.getCount() + stack.getCount());
                                entity.setChanged();
                            }
                        }
                    } else {
                        return stack;
                    }
                } else {
                    return stack;
                }
                return ItemStack.EMPTY;
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                var stackInSlot = entity.inventory.get(slot);
                if (stackInSlot.isEmpty()) {
                    return ItemStack.EMPTY;
                } else if (amount >= stackInSlot.getCount()) {
                    if (!simulate) {
                        entity.inventory.set(slot, ItemStack.EMPTY);
                    }
                    return stackInSlot;
                } else {
                    var copy = simulate ? stackInSlot.copy() : stackInSlot;
                    return copy.split(amount);
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return true;
            }
        };
    }

    private void initialise(ResourceLocation blockId) {
        if (ForgeRegistries.BLOCKS.getValue(blockId) instanceof AbstractOpenableStorageBlock block) {
            slots = block.getSlotCount();
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
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        if (state.getBlock() instanceof AbstractOpenableStorageBlock block) {
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

    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    public int getItemCount() {
        return slots;
    }

    public boolean canContinueUse(Player player) {
        //noinspection ConstantConditions
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 64;
    }
}
