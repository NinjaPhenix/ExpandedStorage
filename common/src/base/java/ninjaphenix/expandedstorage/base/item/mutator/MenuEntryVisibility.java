package ninjaphenix.expandedstorage.base.item.mutator;

import net.minecraft.world.level.block.state.BlockState;

public interface MenuEntryVisibility {
    boolean usable(BlockState state);
}
