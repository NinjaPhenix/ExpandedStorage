package ninjaphenix.expandedstorage.base.client.menu.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

public class PageButton extends Button {
    private static final ResourceLocation TEXTURE = Utils.resloc("textures/gui/page_buttons.png");
    private final int textureOffset;

    public PageButton(int x, int y, int textureOffset, Component text, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, 12, 12, text, onPress, onTooltip);
        this.textureOffset = textureOffset;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.setFocused(false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        Minecraft.getInstance().getTextureManager().bind(PageButton.TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiComponent.blit(stack, x, y, textureOffset * 12, this.getYImage(this.isHovered()) * 12, width, height, 32, 48);
    }

    public void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (active) {
            if (isHovered) {
                this.renderToolTip(stack, mouseX, mouseY);
            } else if (this.isFocused()) {
                this.renderToolTip(stack, x, y);
            }
        }
    }
}
