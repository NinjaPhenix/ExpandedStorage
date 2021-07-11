package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public enum CursedChestType implements StringRepresentable {
    TOP("top", -1),
    BOTTOM("bottom", -1),
    FRONT("front", 0),
    BACK("back", 2),
    LEFT("left", 1),
    RIGHT("right", 3),
    SINGLE("single", -1);

    private final String name;
    private final int offset;

    CursedChestType(String name, int offset) {
        this.name = name;
        this.offset = offset;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int offset() {
        return offset;
    }

    public CursedChestType getOpposite() {
        if (this == CursedChestType.TOP) {
            return CursedChestType.BOTTOM;
        } else if (this == CursedChestType.BOTTOM) {
            return CursedChestType.TOP;
        } else if (this == CursedChestType.FRONT) {
            return CursedChestType.BACK;
        } else if (this == CursedChestType.BACK) {
            return CursedChestType.FRONT;
        } else if (this == CursedChestType.LEFT) {
            return CursedChestType.RIGHT;
        } else if (this == CursedChestType.RIGHT) {
            return CursedChestType.LEFT;
        }
        throw new IllegalStateException("CursedChestType.SINGLE CursedChestType has no opposite");
    }
}
