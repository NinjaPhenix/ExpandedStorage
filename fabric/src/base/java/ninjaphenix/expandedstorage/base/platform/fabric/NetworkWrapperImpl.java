package ninjaphenix.expandedstorage.base.platform.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ServerContainerMenuFactory;
import ninjaphenix.expandedstorage.base.inventory.PagedContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.ScrollableContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.SingleContainerMenu;
import ninjaphenix.expandedstorage.base.platform.ConfigWrapper;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class NetworkWrapperImpl implements NetworkWrapper {
    private static final ResourceLocation OPEN_SELECT_SCREEN = Utils.resloc("open_select_screen");
    private static final ResourceLocation UPDATE_PLAYER_PREFERENCE = Utils.resloc("update_player_preference");
    private static final ResourceLocation REMOVE_TYPE_SELECT_CALLBACK = Utils.resloc("remove_type_select_callback");

    private final Map<UUID, Consumer<ResourceLocation>> preferenceCallbacks = new HashMap<>();
    private final Map<UUID, ResourceLocation> playerPreferences = new HashMap<>();
    private final Map<ResourceLocation, ServerContainerMenuFactory> containerFactories = Utils.unmodifiableMap(map -> {
        map.put(Utils.SINGLE_CONTAINER_TYPE, SingleContainerMenu::new);
        map.put(Utils.SCROLL_CONTAINER_TYPE, ScrollableContainerMenu::new);
        map.put(Utils.PAGE_CONTAINER_TYPE, PagedContainerMenu::new);
    });

    private NetworkWrapperImpl() {

    }

    public static NetworkWrapper getInstance() {
        return new NetworkWrapperImpl();
    }

    public void initialise() {
        if (PlatformUtils.getInstance().isClient()) {
            new Client().initialise();
        }
        // Register Server Receivers
        ServerPlayConnectionEvents.INIT.register((listener_init, server_unused) -> {
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.OPEN_SELECT_SCREEN, this::s_handleOpenSelectScreen);
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, this::s_handleUpdatePlayerPreference);
            ServerPlayNetworking.registerReceiver(listener_init, NetworkWrapperImpl.REMOVE_TYPE_SELECT_CALLBACK, this::s_handleRemoveTypeSelectCallback);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> this.s_setPlayerContainerType(listener.player, Utils.UNSET_CONTAINER_TYPE));
    }

    public void c2s_removeTypeSelectCallback() {
        if (ClientPlayNetworking.canSend(NetworkWrapperImpl.REMOVE_TYPE_SELECT_CALLBACK)) {
            ClientPlayNetworking.send(NetworkWrapperImpl.REMOVE_TYPE_SELECT_CALLBACK, new FriendlyByteBuf(Unpooled.buffer()));
        }
    }

    public void c2s_openTypeSelectScreen() {
        if (ClientPlayNetworking.canSend(NetworkWrapperImpl.OPEN_SELECT_SCREEN)) {
            ClientPlayNetworking.send(NetworkWrapperImpl.OPEN_SELECT_SCREEN, new FriendlyByteBuf(Unpooled.buffer()));
        }
    }

    public void c2s_setSendTypePreference(ResourceLocation selection) {
        if (ConfigWrapper.getInstance().setPreferredContainerType(selection)) {
            this.c2s_sendTypePreference(selection);
        }
    }

    public void s2c_openMenu(ServerPlayer player, ContainerMenuFactory menuFactory) {
        UUID uuid = player.getUUID();
        if (playerPreferences.containsKey(uuid) && this.validContainerType(playerPreferences.get(uuid))) {
            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
                    menuFactory.writeClientData(player, buffer);
                }

                @Override
                public Component getDisplayName() {
                    return menuFactory.displayName();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return menuFactory.createMenu(windowId, inventory, player);
                }
            });
        } else {
            this.s2c_openSelectScreen(player, (type) -> s2c_openMenu(player, menuFactory));
        }
    }

    public void s2c_openSelectScreen(ServerPlayer player, Consumer<ResourceLocation> playerPreferenceCallback) {
        if (ServerPlayNetworking.canSend(player, NetworkWrapperImpl.OPEN_SELECT_SCREEN)) {
            if (playerPreferenceCallback != null) {
                preferenceCallbacks.put(player.getUUID(), playerPreferenceCallback);
            }
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeInt(containerFactories.size());
            containerFactories.keySet().forEach(buffer::writeResourceLocation);
            ServerPlayNetworking.send(player, NetworkWrapperImpl.OPEN_SELECT_SCREEN, buffer);
        }
        // else illegal state
    }

    public AbstractContainerMenu createMenu(int windowId, BlockPos pos, Container container, Inventory inventory, Component containerName) {
        UUID uuid = inventory.player.getUUID();
        ResourceLocation playerPreference;
        if (playerPreferences.containsKey(uuid) && containerFactories.containsKey(playerPreference = playerPreferences.get(uuid))) {
            return containerFactories.get(playerPreference).create(windowId, pos, container, inventory, containerName);
        }
        return null;
    }

    @Override
    public void s_setPlayerContainerType(ServerPlayer player, ResourceLocation containerType) {
        UUID uuid = player.getUUID();
        if (containerFactories.containsKey(containerType)) {
            playerPreferences.put(uuid, containerType);
            if (preferenceCallbacks.containsKey(uuid)) {
                preferenceCallbacks.remove(uuid).accept(containerType);
            }
        } else {
            playerPreferences.remove(uuid);
            preferenceCallbacks.remove(uuid);
        }
    }

    private void s_handleUpdatePlayerPreference(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener,
                                                FriendlyByteBuf buffer, PacketSender sender) {
        ResourceLocation containerType = buffer.readResourceLocation();
        server.submit(() -> this.s_setPlayerContainerType(player, containerType));
    }

    private void s_handleRemoveTypeSelectCallback(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener,
                                                  FriendlyByteBuf buffer, PacketSender sender) {
        server.submit(() -> this.removeTypeSelectCallback(player));
    }

    private void s_handleOpenSelectScreen(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener,
                                          FriendlyByteBuf buffer, PacketSender sender) {
        if (player.containerMenu instanceof AbstractContainerMenu_<?> menu) {
            server.submit(() -> this.s2c_openSelectScreen(player, (type) -> player.openMenu(new ExtendedScreenHandlerFactory() {
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return NetworkWrapperImpl.this.createMenu(windowId, menu.pos, menu.getContainer(), inventory, menu.getDisplayName());
                }

                @Override
                public Component getDisplayName() {
                    return menu.getDisplayName();
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
                    buffer.writeBlockPos(menu.pos).writeInt(menu.getContainer().getContainerSize());
                }
            })));
        } else {
            server.submit(() -> this.s2c_openSelectScreen(player, null));
        }
    }

    public boolean validContainerType(ResourceLocation containerType) {
        return containerType != null && containerFactories.containsKey(containerType);
    }

    @Override
    public void c2s_sendTypePreference(ResourceLocation selection) {
        if (ClientPlayNetworking.canSend(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE)) {
            ClientPlayNetworking.send(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(selection));
        }
    }

    @Override
    public void removeTypeSelectCallback(ServerPlayer player) {
        preferenceCallbacks.remove(player.getUUID());
    }

    private class Client {
        public void initialise() {
            ClientPlayConnectionEvents.INIT.register((listener_init, client) -> ClientPlayNetworking.registerReceiver(NetworkWrapperImpl.OPEN_SELECT_SCREEN, this::c_handleOpenSelectScreen));
            ClientPlayConnectionEvents.JOIN.register((listener_play, sender, client) -> sender.sendPacket(NetworkWrapperImpl.UPDATE_PLAYER_PREFERENCE, new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(ConfigWrapper.getInstance().getPreferredContainerType())));
        }

        private void c_handleOpenSelectScreen(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
            int count = buffer.readInt();
            HashSet<ResourceLocation> allowed = new HashSet<>();
            for (int i = 0; i < count; i++) {
                ResourceLocation containerType = buffer.readResourceLocation();
                if (containerFactories.containsKey(containerType)) {
                    allowed.add(containerType);
                }
            }
            minecraft.submit(() -> minecraft.setScreen(new PickScreen(allowed, null)));
        }
    }
}
