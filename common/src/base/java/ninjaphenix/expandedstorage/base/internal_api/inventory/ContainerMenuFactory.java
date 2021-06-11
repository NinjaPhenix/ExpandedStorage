package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public interface ContainerMenuFactory {
    void writeClientData(ServerPlayer player, FriendlyByteBuf buffer);

    Component displayName();

    boolean canPlayerOpen(ServerPlayer player);

    AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player);
}
