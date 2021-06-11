package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.inventory.ScrollableContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.screen.ScrollableScreenMeta;
import ninjaphenix.expandedstorage.base.platform.ConfigWrapper;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;

public final class ScrollableScreen extends AbstractScreen<ScrollableContainerMenu, ScrollableScreenMeta> {
    protected final boolean hasScrollbar;
    private final boolean scrollingUnrestricted;
    private boolean isDragging;
    private int topRow;

    public ScrollableScreen(ScrollableContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.width * 18 + 14) / 2 - 80);
        hasScrollbar = screenMeta.totalRows != screenMeta.height;
        imageWidth = 14 + 18 * screenMeta.width;
        imageHeight = 17 + 97 + 18 * screenMeta.height;
        scrollingUnrestricted = ConfigWrapper.getInstance().isScrollingUnrestricted();
    }

    @Override
    protected void init() {
        super.init();
        if (hasScrollbar) {
            isDragging = false;
            topRow = 0;
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        super.renderBg(stack, delta, mouseX, mouseY);
        if (hasScrollbar) {
            int containerSlotsHeight = screenMeta.height * 18;
            int scrollbarHeight = containerSlotsHeight + (screenMeta.width > 9 ? 34 : 24);
            GuiComponent.blit(stack, leftPos + imageWidth - 4, topPos, imageWidth, 0, 22, scrollbarHeight, screenMeta.textureWidth, screenMeta.textureHeight);
            int yOffset = Mth.floor((containerSlotsHeight - 17) * (((double) topRow) / (screenMeta.totalRows - screenMeta.height)));
            GuiComponent.blit(stack, leftPos + imageWidth - 2, topPos + yOffset + 18, imageWidth, scrollbarHeight, 12, 15, screenMeta.textureWidth, screenMeta.textureHeight);
        }
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        int scrollbarTopPos = topPos + 18;
        int scrollbarLeftPos = leftPos + imageWidth - 2;
        return mouseX >= scrollbarLeftPos && mouseY >= scrollbarTopPos && mouseX < scrollbarLeftPos + 12 && mouseY < scrollbarTopPos + screenMeta.height * 18;
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button) && !this.isMouseOverScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (hasScrollbar) {
            if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                if (topRow != screenMeta.totalRows - screenMeta.height) {
                    if (Screen.hasShiftDown()) {
                        this.setTopRow(topRow, Math.min(topRow + screenMeta.height, screenMeta.totalRows - screenMeta.height));
                    } else {
                        this.setTopRow(topRow, topRow + 1);
                    }
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                if (topRow != 0) {
                    if (Screen.hasShiftDown()) {
                        this.setTopRow(topRow, Math.max(topRow - screenMeta.height, 0));
                    } else {
                        this.setTopRow(topRow, topRow - 1);
                    }
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasScrollbar && this.isMouseOverScrollbar(mouseX, mouseY) && button == 0) {
            isDragging = true;
            this.updateTopRow(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (hasScrollbar && isDragging) {
            this.updateTopRow(mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateTopRow(double mouseY) {
        this.setTopRow(topRow, Mth.floor(Mth.clampedLerp(0, screenMeta.totalRows - screenMeta.height, (mouseY - (topPos + 18)) / (screenMeta.height * 18))));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (hasScrollbar && (scrollingUnrestricted || this.isMouseOverScrollbar(mouseX, mouseY))) {
            int newTop;
            if (delta < 0) {
                newTop = Math.min(topRow + (Screen.hasShiftDown() ? screenMeta.height : 1), screenMeta.totalRows - screenMeta.height);
            } else {
                newTop = Math.max(topRow - (Screen.hasShiftDown() ? screenMeta.height : 1), 0);
            }
            this.setTopRow(topRow, newTop);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRow(int oldTopRow, int newTopRow) {
        if (oldTopRow == newTopRow) {
            return;
        }
        topRow = newTopRow;
        int delta = newTopRow - oldTopRow;
        int rows = Math.abs(delta);
        if (rows < screenMeta.height) {
            int setAmount = rows * screenMeta.width;
            int movableAmount = (screenMeta.height - rows) * screenMeta.width;
            if (delta > 0) {
                int setOutBegin = oldTopRow * screenMeta.width;
                int movableBegin = newTopRow * screenMeta.width;
                int setInBegin = movableBegin + movableAmount;
                menu.setSlotRange(setOutBegin, setOutBegin + setAmount, index -> -2000);
                menu.moveSlotRange(movableBegin, setInBegin, -18 * rows);
                menu.setSlotRange(setInBegin, Math.min(setInBegin + setAmount, screenMeta.totalSlots),
                        index -> 18 * Mth.intFloorDiv(index - movableBegin + screenMeta.width, screenMeta.width));
            } else {
                int setInBegin = newTopRow * screenMeta.width;
                int movableBegin = oldTopRow * screenMeta.width;
                int setOutBegin = movableBegin + movableAmount;
                menu.setSlotRange(setInBegin, setInBegin + setAmount,
                        index -> 18 * Mth.intFloorDiv(index - setInBegin + screenMeta.width, screenMeta.width));
                menu.moveSlotRange(movableBegin, setOutBegin, 18 * rows);
                menu.setSlotRange(setOutBegin, Math.min(setOutBegin + setAmount, screenMeta.totalSlots), index -> -2000);
            }
        } else {
            int oldMin = oldTopRow * screenMeta.width;
            menu.setSlotRange(oldMin, Math.min(oldMin + screenMeta.width * screenMeta.height, screenMeta.totalSlots), index -> -2000);
            int newMin = newTopRow * screenMeta.width;
            menu.setSlotRange(newMin, newMin + screenMeta.width * screenMeta.height,
                    index -> 18 + 18 * Mth.intFloorDiv(index - newMin, screenMeta.width));
        }
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        if (hasScrollbar) {
            int row = topRow;
            super.resize(client, width, height);
            this.setTopRow(topRow, row);
        } else {
            super.resize(client, width, height);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (hasScrollbar && isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public List<Rect2i> getExclusionZones() {
        if (hasScrollbar) {
            int height = screenMeta.height * 18 + (screenMeta.width > 9 ? 34 : 24);
            return Collections.singletonList(new Rect2i(leftPos + imageWidth - 4, topPos, 22, height));
        }
        return Collections.emptyList();
    }
}
