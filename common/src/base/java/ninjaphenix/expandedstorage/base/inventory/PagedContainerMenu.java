package ninjaphenix.expandedstorage.base.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.inventory.screen.PagedScreenMeta;

public final class PagedContainerMenu extends AbstractContainerMenu_<PagedScreenMeta> {
    // @formatter:off
    private static final ImmutableMap<Integer, PagedScreenMeta> SIZES = ImmutableMap.<Integer, PagedScreenMeta>builder()
            .put(27, new PagedScreenMeta(9, 3, 1, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new PagedScreenMeta(9, 6, 1, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new PagedScreenMeta(9, 6, 2, 81, getTexture("shared", 9, 6), 208, 240)) // Gold
            .put(108, new PagedScreenMeta(9, 6, 2, 108, getTexture("shared", 9, 6), 208, 240)) // Diamond / Large Iron
            .put(135, new PagedScreenMeta(9, 6, 3, 135, getTexture("shared", 9, 6), 208, 240)) // Netherite
            .put(162, new PagedScreenMeta(9, 6, 3, 162, getTexture("shared", 9, 6), 208, 240)) // Large Gold
            .put(216, new PagedScreenMeta(9, 6, 4, 216, getTexture("shared", 9, 6), 208, 240)) // Large Diamond
            .put(270, new PagedScreenMeta(9, 6, 5, 270, getTexture("shared", 9, 6), 208, 240)) // Large Netherite
            .build();
    // @formatter:on

    public PagedContainerMenu(int windowId, BlockPos pos, Container container, Inventory playerInventory, Component displayName) {
        super(BaseCommon.PAGE_MENU_TYPE, windowId, pos, container, playerInventory, displayName,
                AbstractContainerMenu_.getNearestScreenMeta(container.getContainerSize(), PagedContainerMenu.SIZES));
        this.resetSlotPositions(true);
        int left = (screenMeta.width * Utils.SLOT_SIZE + 14) / 2 - 80;
        int top = Utils.SLOT_SIZE + 14 + (screenMeta.height * Utils.SLOT_SIZE);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(playerInventory, y * 9 + x + 9, left + Utils.SLOT_SIZE * x, top + y * Utils.SLOT_SIZE));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, left + Utils.SLOT_SIZE * x, top + 58));
        }
    }

    public void resetSlotPositions(boolean createSlots) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            int slotXPos = i % screenMeta.width;
            int slotYPos = Mth.ceil((((double) (i - slotXPos)) / screenMeta.width));
            int realYPos = slotYPos >= screenMeta.height ? (Utils.SLOT_SIZE * (slotYPos % screenMeta.height)) - 2000 : slotYPos * Utils.SLOT_SIZE;
            if (createSlots) {
                this.addSlot(new Slot(container, i, slotXPos * Utils.SLOT_SIZE + 8, realYPos + Utils.SLOT_SIZE));
            } else {
                slots.get(i).y = realYPos + Utils.SLOT_SIZE;
            }
        }
    }

    public void moveSlotRange(int minSlotIndex, int maxSlotIndex, int yDifference) {
        for (int i = minSlotIndex; i < maxSlotIndex; i++) {
            slots.get(i).y += yDifference;
        }
    }

    public static final class Factory implements ClientContainerMenuFactory<PagedContainerMenu> {
        @Override
        public PagedContainerMenu create(int windowId, Inventory inventory, FriendlyByteBuf buffer) {
            if (buffer == null) {
                return null;
            }
            return new PagedContainerMenu(windowId, buffer.readBlockPos(), new SimpleContainer(buffer.readInt()), inventory, null);
        }
    }
}
