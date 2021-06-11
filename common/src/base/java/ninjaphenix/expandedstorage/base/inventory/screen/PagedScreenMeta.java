package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public final class PagedScreenMeta extends ScreenMeta {
    public final int blankSlots, pages;

    public PagedScreenMeta(int width, int height, int pages, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        this.pages = pages;
        blankSlots = pages * width * height - totalSlots;
    }
}
