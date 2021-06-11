package ninjaphenix.expandedstorage.chest;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;

@SuppressWarnings("ClassCanBeRecord")
public final class TextureCollection {
    private final ResourceLocation single;
    private final ResourceLocation left;
    private final ResourceLocation right;
    private final ResourceLocation top;
    private final ResourceLocation bottom;
    private final ResourceLocation front;
    private final ResourceLocation back;

    public TextureCollection(ResourceLocation single, ResourceLocation left, ResourceLocation right,
                             ResourceLocation top, ResourceLocation bottom, ResourceLocation front, ResourceLocation back) {
        this.single = single;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.front = front;
        this.back = back;
    }

    ResourceLocation getTexture(CursedChestType type) {
        if (type == CursedChestType.TOP) {
            return this.top;
        } else if (type == CursedChestType.BOTTOM) {
            return this.bottom;
        } else if (type == CursedChestType.FRONT) {
            return this.front;
        } else if (type == CursedChestType.BACK) {
            return this.back;
        } else if (type == CursedChestType.LEFT) {
            return this.left;
        } else if (type == CursedChestType.RIGHT) {
            return this.right;
        } else if (type == CursedChestType.SINGLE) {
            return this.single;
        }
        throw new IllegalArgumentException("TextureCollection#getTexture received an unknown CursedChestType.");
    }
}
