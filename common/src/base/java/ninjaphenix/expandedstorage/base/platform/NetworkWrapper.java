package ninjaphenix.expandedstorage.base.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.expandedstorage.base.BaseImpl;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;

import java.util.function.Consumer;

public interface NetworkWrapper {
    static NetworkWrapper getInstance() {
        return BaseImpl.getInstance().getNetworkWrapper();
    }

    void initialise();

    void c2s_removeTypeSelectCallback();

    void c2s_openTypeSelectScreen();

    void c2s_setSendTypePreference(ResourceLocation selection);

    void s2c_openMenu(ServerPlayer player, ContainerMenuFactory menuFactory);

    void s2c_openSelectScreen(ServerPlayer player, Consumer<ResourceLocation> playerPreferenceCallback);

    AbstractContainerMenu createMenu(int windowId, BlockPos blockPos, Container container, Inventory playerInventory, Component containerName);

    boolean validContainerType(ResourceLocation containerType);

    void c2s_sendTypePreference(ResourceLocation selection);

    void s_setPlayerContainerType(ServerPlayer player, ResourceLocation selection);

    void removeTypeSelectCallback(ServerPlayer player);
}
