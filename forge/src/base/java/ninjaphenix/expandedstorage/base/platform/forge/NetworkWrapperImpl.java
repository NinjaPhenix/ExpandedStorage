package ninjaphenix.expandedstorage.base.platform.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ServerContainerMenuFactory;
import ninjaphenix.expandedstorage.base.inventory.PagedContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.ScrollableContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.SingleContainerMenu;
import ninjaphenix.expandedstorage.base.network.ContainerTypeUpdateMessage;
import ninjaphenix.expandedstorage.base.network.OpenSelectScreenMessage;
import ninjaphenix.expandedstorage.base.network.RemovePlayerPreferenceCallbackMessage;
import ninjaphenix.expandedstorage.base.network.RequestOpenSelectScreenMessage;
import ninjaphenix.expandedstorage.base.platform.ConfigWrapper;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class NetworkWrapperImpl implements NetworkWrapper {
    private final Map<UUID, Consumer<ResourceLocation>> preferenceCallbacks = new HashMap<>();
    private final Map<UUID, ResourceLocation> playerPreferences = new HashMap<>();
    private final Map<ResourceLocation, ServerContainerMenuFactory> containerFactories = Utils.unmodifiableMap(map -> {
        map.put(Utils.SINGLE_CONTAINER_TYPE, SingleContainerMenu::new);
        map.put(Utils.SCROLL_CONTAINER_TYPE, ScrollableContainerMenu::new);
        map.put(Utils.PAGE_CONTAINER_TYPE, PagedContainerMenu::new);
    });
    private SimpleChannel channel;

    private NetworkWrapperImpl() {

    }

    public static NetworkWrapperImpl getInstance() {
        return new NetworkWrapperImpl();
    }

    @SubscribeEvent
    public static void onPlayerConnected(ClientPlayerNetworkEvent.LoggedInEvent event) {
        NetworkWrapper.getInstance().c2s_sendTypePreference(ConfigWrapper.getInstance().getPreferredContainerType());
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) { // Probably called on both sides.
            NetworkWrapper.getInstance().s_setPlayerContainerType(player, Utils.UNSET_CONTAINER_TYPE);
        }
    }

    public void initialise() {
        String channelVersion = "2";
        channel = NetworkRegistry.newSimpleChannel(Utils.resloc("channel"), () -> channelVersion, channelVersion::equals, channelVersion::equals);

        channel.registerMessage(0, ContainerTypeUpdateMessage.class, ContainerTypeUpdateMessage::encode, ContainerTypeUpdateMessage::decode, ContainerTypeUpdateMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(1, RequestOpenSelectScreenMessage.class, RequestOpenSelectScreenMessage::encode, RequestOpenSelectScreenMessage::decode, RequestOpenSelectScreenMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(2, RemovePlayerPreferenceCallbackMessage.class, RemovePlayerPreferenceCallbackMessage::encode, RemovePlayerPreferenceCallbackMessage::decode, RemovePlayerPreferenceCallbackMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        channel.registerMessage(3, OpenSelectScreenMessage.class, OpenSelectScreenMessage::encode, OpenSelectScreenMessage::decode, OpenSelectScreenMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        if (PlatformUtils.getInstance().isClient()) {
            MinecraftForge.EVENT_BUS.addListener(NetworkWrapperImpl::onPlayerConnected);
        }
        MinecraftForge.EVENT_BUS.addListener(NetworkWrapperImpl::onPlayerDisconnected);
    }

    public void c2s_removeTypeSelectCallback() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener != null && channel.isRemotePresent(listener.getConnection())) {
            //noinspection InstantiationOfUtilityClass
            channel.sendToServer(new RemovePlayerPreferenceCallbackMessage());
        }
    }

    public void c2s_openTypeSelectScreen() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener != null && channel.isRemotePresent(listener.getConnection())) {
            //noinspection InstantiationOfUtilityClass
            channel.sendToServer(new RequestOpenSelectScreenMessage());
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
            NetworkHooks.openGui(player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return menuFactory.displayName();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player1) {
                    return menuFactory.createMenu(windowId, inventory, player1);
                }
            }, buffer -> menuFactory.writeClientData(player, buffer));
        } else {
            s2c_openSelectScreen(player, (type) -> s2c_openMenu(player, menuFactory));
        }
    }

    public void s2c_openSelectScreen(ServerPlayer player, Consumer<ResourceLocation> playerPreferenceCallback) {
        if (playerPreferenceCallback != null) {
            preferenceCallbacks.put(player.getUUID(), playerPreferenceCallback);
        }
        ServerGamePacketListenerImpl listener = player.connection;
        if (listener != null && channel.isRemotePresent(listener.getConnection())) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), new OpenSelectScreenMessage(containerFactories.keySet()));
        }
    }

    public AbstractContainerMenu createMenu(int windowId, BlockPos pos, Container container, Inventory inventory, Component containerName) {
        UUID uuid = inventory.player.getUUID();
        ResourceLocation playerPreference;
        if (playerPreferences.containsKey(uuid) && containerFactories.containsKey(playerPreference = playerPreferences.get(uuid))) {
            return containerFactories.get(playerPreference).create(windowId, pos, container, inventory, containerName);
        }
        return null;
    }

    public boolean validContainerType(ResourceLocation containerType) {
        return containerType != null && containerFactories.containsKey(containerType);
    }

    @Override
    public void c2s_sendTypePreference(ResourceLocation selection) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener != null && channel.isRemotePresent(listener.getConnection())) {
            channel.sendToServer(new ContainerTypeUpdateMessage(selection));
        }
    }

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

    public void removeTypeSelectCallback(ServerPlayer player) {
        preferenceCallbacks.remove(player.getUUID());
    }
}
