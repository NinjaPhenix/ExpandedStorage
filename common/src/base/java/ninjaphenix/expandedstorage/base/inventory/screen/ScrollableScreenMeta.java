package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ScrollableScreenMeta extends ScreenMeta {
    public final int blankSlots, totalRows;

    public ScrollableScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        totalRows = Mth.ceil((double) totalSlots / width);
        blankSlots = totalRows * width - totalSlots;
    }
}
