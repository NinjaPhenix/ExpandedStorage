package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.inventory.screen.ScreenMeta;
import ninjaphenix.expandedstorage.base.wrappers.NetworkWrapper;
import ninjaphenix.expandedstorage.base.wrappers.PlatformUtils;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractScreen<T extends AbstractContainerMenu_<R>, R extends ScreenMeta> extends AbstractContainerScreen<T> {
    protected final R screenMeta;
    private final Integer inventoryLabelLeft;

    protected AbstractScreen(T container, Inventory playerInventory, Component title, Function<R, Integer> inventoryLabelLeftFunction) {
        super(container, playerInventory, title);
        screenMeta = container.screenMeta;
        inventoryLabelLeft = inventoryLabelLeftFunction.apply(screenMeta);
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        minecraft.getTextureManager().bind(screenMeta.texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, screenMeta.textureWidth, screenMeta.textureHeight);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 4210752);
        font.draw(stack, inventory.getDisplayName(), inventoryLabelLeft, imageHeight - 96 + 2, 4210752);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean keyRequiresShift = PlatformUtils.getInstance().configKeyRequiresShift();
        if (PlatformUtils.getInstance().getConfigScreenKeyMapping().matches(keyCode, scanCode) &&
                (!keyRequiresShift || keyRequiresShift && Screen.hasShiftDown())) {
            NetworkWrapper.getInstance().c2s_openTypeSelectScreen();
            return true;
        }
        else if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected final void renderButtonTooltip(AbstractButton button, PoseStack stack, int x, int y) {
        this.renderTooltip(stack, button.getMessage(), x, y);
    }

    public abstract List<Rect2i> getExclusionZones();

    @SuppressWarnings("ClassCanBeRecord")
    protected static final class Image {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int textureX;
        private final int textureY;
        private final int textureWidth;
        private final int textureHeight;

        protected Image(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.textureX = textureX;
            this.textureY = textureY;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public void render(PoseStack stack) {
            GuiComponent.blit(stack, x, y, textureX, textureY, width, height, textureWidth, textureHeight);
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }
    }
}
