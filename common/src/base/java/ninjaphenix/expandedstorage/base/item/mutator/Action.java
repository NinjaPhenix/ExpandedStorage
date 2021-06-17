package ninjaphenix.expandedstorage.base.item.mutator;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Action implements MenuEntryVisibility {
    public abstract void perform(Level level, BlockPos pos, BlockState state);
    public abstract Component name();
}
