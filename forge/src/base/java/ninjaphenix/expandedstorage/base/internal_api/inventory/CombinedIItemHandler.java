package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public final class CombinedIItemHandler implements IItemHandler {

    private final IItemHandler first;
    private final IItemHandler second;
    private final int firstSize;
    private final int totalSize;

    public CombinedIItemHandler(IItemHandler first, IItemHandler second) {
        this.first = first;
        this.second = second;
        this.firstSize = first.getSlots();
        this.totalSize = firstSize + second.getSlots();
    }

    @Override
    public int getSlots() {
        return totalSize;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= firstSize) {
            return second.getStackInSlot(slot - firstSize);
        }
        return first.getStackInSlot(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot >= firstSize) {
            return second.insertItem(slot - firstSize, stack, simulate);
        }
        return first.insertItem(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= firstSize) {
            return second.extractItem(slot - firstSize, amount, simulate);
        }
        return first.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot >= firstSize) {
            return second.getSlotLimit(slot - firstSize);
        }
        return first.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot >= firstSize) {
            return second.isItemValid(slot - firstSize, stack);
        }
        return first.isItemValid(slot, stack);
    }
}
