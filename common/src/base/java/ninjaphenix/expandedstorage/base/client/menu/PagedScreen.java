package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.client.menu.widget.PageButton;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.inventory.PagedContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.screen.PagedScreenMeta;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PagedScreen extends AbstractScreen<PagedContainerMenu, PagedScreenMeta> {
    private final Set<Image> blankArea = new LinkedHashSet<>();
    private final int offset;
    private PageButton leftPageButton;
    private PageButton rightPageButton;
    private int page;
    private TranslatableComponent currentPageText;
    private float pageTextX;

    public PagedScreen(PagedContainerMenu screenHandler, Inventory playerInventory, Component title) {
        super(screenHandler, playerInventory, title, (screenMeta) -> (screenMeta.width * 18 + 14) / 2 - 80);
        imageWidth = 14 + 18 * screenMeta.width;
        imageHeight = 17 + 97 + 18 * screenMeta.height;
        int max = 0;
        var config = PlatformUtils.getInstance().getButtonOffsetConfig();
        var mods = PlatformUtils.getInstance().getLoadedModIds();
        for (var buttonOffset : config) {
            if (buttonOffset.areModsPresent(mods)) {
                if (buttonOffset.offset() < max) {
                    max = buttonOffset.offset();
                }
            }
        }
        this.offset = max;
    }

    private void setPage(int oldPage, int newPage) {
        if (newPage == 0 || newPage > screenMeta.pages) {
            return;
        }
        page = newPage;
        if (newPage > oldPage) {
            if (page == screenMeta.pages) {
                rightPageButton.setActive(false);
                int blanked = screenMeta.blankSlots;
                if (blanked > 0) {
                    int rows = Mth.intFloorDiv(blanked, screenMeta.width);
                    int remainder = (blanked - screenMeta.width * rows);
                    int yTop = topPos + Utils.CONTAINER_HEADER_HEIGHT + (screenMeta.height - 1) * Utils.SLOT_SIZE;
                    int xLeft = leftPos + Utils.CONTAINER_PADDING_WIDTH;
                    for (int i = 0; i < rows; i++) {
                        blankArea.add(new Image(xLeft, yTop, screenMeta.width * Utils.SLOT_SIZE, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight));
                        yTop -= Utils.SLOT_SIZE;
                    }
                    if (remainder > 0) {
                        int xRight = leftPos + Utils.CONTAINER_PADDING_WIDTH + screenMeta.width * Utils.SLOT_SIZE;
                        int width = remainder * Utils.SLOT_SIZE;
                        blankArea.add(new Image(xRight - width, yTop, width, Utils.SLOT_SIZE,
                                Utils.CONTAINER_PADDING_WIDTH, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight));
                    }
                }
            }
            if (!leftPageButton.active) {
                leftPageButton.setActive(true);
            }
        } else if (newPage < oldPage) {
            if (page == 1) {
                leftPageButton.setActive(false);
            }
            blankArea.clear();
            if (!rightPageButton.active) {
                rightPageButton.setActive(true);
            }
        }
        int slotsPerPage = screenMeta.width * screenMeta.height;
        int oldMin = slotsPerPage * (oldPage - 1);
        int oldMax = Math.min(oldMin + slotsPerPage, screenMeta.totalSlots);
        menu.moveSlotRange(oldMin, oldMax, -2000);
        int newMin = slotsPerPage * (newPage - 1);
        int newMax = Math.min(newMin + slotsPerPage, screenMeta.totalSlots);
        menu.moveSlotRange(newMin, newMax, 2000);
        this.setPageText();
    }

    private void setPageText() {
        currentPageText = new TranslatableComponent("screen.expandedstorage.page_x_y", page, screenMeta.pages);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (stack == null) {
            return;
        } // Not sure why this can be null, but don't render in case it is.
        super.render(stack, mouseX, mouseY, delta);
        if (screenMeta.pages != 1) {
            leftPageButton.renderTooltip(stack, mouseX, mouseY);
            rightPageButton.renderTooltip(stack, mouseX, mouseY);
        }
    }

    @Override
    protected void init() {
        super.init();
        if (screenMeta.pages != 1) {
            int pageButtonsXOffset = offset;
            page = 1;
            this.setPageText();
            leftPageButton = new PageButton(leftPos + imageWidth - 61 + pageButtonsXOffset, topPos + imageHeight - 96, 0,
                    new TranslatableComponent("screen.expandedstorage.prev_page"), button -> this.setPage(page, page - 1),
                    this::renderButtonTooltip);
            leftPageButton.active = false;
            this.addButton(leftPageButton);
            rightPageButton = new PageButton(leftPos + imageWidth - 19 + pageButtonsXOffset, topPos + imageHeight - 96, 1,
                    new TranslatableComponent("screen.expandedstorage.next_page"), button -> this.setPage(page, page + 1),
                    this::renderButtonTooltip);
            this.addButton(rightPageButton);
            pageTextX = (1 + leftPageButton.x + rightPageButton.x - rightPageButton.getWidth() / 2F) / 2F;
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        super.renderBg(stack, delta, mouseX, mouseY);
        blankArea.forEach(image -> image.render(stack));
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        if (screenMeta.pages != 1) {
            int currentPage = page;
            if (currentPage != 1) {
                menu.resetSlotPositions(false);
                super.resize(client, width, height);
                blankArea.clear();
                this.setPage(1, currentPage);
                return;
            }
        }
        super.resize(client, width, height);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        if (currentPageText != null) {
            font.draw(stack, currentPageText.getVisualOrderText(), pageTextX - leftPos, imageHeight - 94, 0x404040);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (screenMeta.pages != 1) {
            if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                this.setPage(page, Screen.hasShiftDown() ? screenMeta.pages : page + 1);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                this.setPage(page, Screen.hasShiftDown() ? 1 : page - 1);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.emptyList();
    }
}
