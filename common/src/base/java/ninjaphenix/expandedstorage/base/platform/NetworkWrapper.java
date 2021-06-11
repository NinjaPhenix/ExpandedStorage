package ninjaphenix.expandedstorage.base.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;

import java.util.function.Consumer;

public interface NetworkWrapper {
    /**
     * Should be private, do not use.
     */
    @Deprecated
    LazyLoadedValue<NetworkWrapper> instance = new LazyLoadedValue<>(() -> Utils.getClassInstance(NetworkWrapper.class, "ninjaphenix.expandedstorage.base.platform", "NetworkWrapperImpl"));

    static NetworkWrapper getInstance() {
        return instance.get();
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
