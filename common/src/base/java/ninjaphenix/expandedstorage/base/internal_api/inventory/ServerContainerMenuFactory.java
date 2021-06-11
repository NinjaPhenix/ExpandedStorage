package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public interface ServerContainerMenuFactory {
    AbstractContainerMenu create(int windowId, BlockPos pos, Container container, Inventory playerInventory, Component displayName);
}
