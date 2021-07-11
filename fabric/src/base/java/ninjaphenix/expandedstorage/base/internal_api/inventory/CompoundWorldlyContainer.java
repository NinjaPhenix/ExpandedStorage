package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
@SuppressWarnings("ClassCanBeRecord")
public final class CompoundWorldlyContainer implements WorldlyContainer {
    private final WorldlyContainer first;
    private final WorldlyContainer second;

    public CompoundWorldlyContainer(WorldlyContainer first, WorldlyContainer second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        int firstContainerSize = first.getContainerSize();
        int[] firstSlots = first.getSlotsForFace(direction);
        int[] secondSlots = second.getSlotsForFace(direction);
        int[] combinedSlots = new int[firstSlots.length + secondSlots.length];
        int index = 0;
        for (int slot : firstSlots) {
            combinedSlots[index++] = slot;
        }
        for (int slot : secondSlots) {
            combinedSlots[index++] = slot + firstContainerSize;
        }
        return combinedSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        if (slot >= first.getContainerSize()) {
            return second.canPlaceItemThroughFace(slot - first.getContainerSize(), stack, face);
        }
        return first.canPlaceItemThroughFace(slot, stack, face);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        if (slot >= first.getContainerSize()) {
            return second.canTakeItemThroughFace(slot - first.getContainerSize(), stack, face);
        }
        return first.canTakeItemThroughFace(slot, stack, face);
    }

    @Override
    public int getContainerSize() {
        return first.getContainerSize() + second.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return first.isEmpty() && second.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return first.stillValid(player) && second.stillValid(player);
    }

    @Override
    public void clearContent() {
        first.clearContent();
        second.clearContent();
    }

    @Override
    public void setChanged() {
        first.setChanged();
        second.setChanged();
    }

    @Override
    public void startOpen(Player player) {
        first.startOpen(player);
        second.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        first.stopOpen(player);
        second.stopOpen(player);
    }

    public boolean consistsPartlyOf(WorldlyContainer container) {
        return first == container || second == container;
    }

    public int getMaxStackSize() {
        return first.getMaxStackSize();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= first.getContainerSize()) {
            return second.getItem(slot - first.getContainerSize());
        }
        return first.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot >= first.getContainerSize()) {
            return second.removeItem(slot - first.getContainerSize(), count);
        }
        return first.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot >= first.getContainerSize()) {
            return second.removeItemNoUpdate(slot - first.getContainerSize());
        }
        return first.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= first.getContainerSize()) {
            second.setItem(slot - first.getContainerSize(), stack);
        } else {
            first.setItem(slot, stack);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= first.getContainerSize()) {
            return second.canPlaceItem(slot - first.getContainerSize(), stack);
        }
        return first.canPlaceItem(slot, stack);
    }
}
