package ninjaphenix.expandedstorage.base.client.menu.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScreenPickButton extends Button {
    private final ResourceLocation texture;

    public ScreenPickButton(int x, int y, int width, int height, ResourceLocation texture, Component message, OnPress pressAction, OnTooltip tooltipRenderer) {
        super(x, y, width, height, message, pressAction, tooltipRenderer);
        this.texture = texture;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        GuiComponent.blit(stack, x, y, 0, this.isHovered() ? height : 0, width, height, width, height * 2);
    }

    public void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (isHovered) {
            this.renderToolTip(stack, mouseX, mouseY);
        } else if (this.isFocused()) {
            this.renderToolTip(stack, x, y);
        }
    }
}
