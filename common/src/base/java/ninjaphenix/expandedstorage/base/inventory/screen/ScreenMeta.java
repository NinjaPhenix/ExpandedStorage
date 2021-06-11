package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public abstract class ScreenMeta {
    public final int width, height, totalSlots, textureWidth, textureHeight;
    public final ResourceLocation texture;

    protected ScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        this.width = width;
        this.height = height;
        this.totalSlots = totalSlots;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }
}
