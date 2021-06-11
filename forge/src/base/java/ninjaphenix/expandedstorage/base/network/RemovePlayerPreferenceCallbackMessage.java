package ninjaphenix.expandedstorage.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkEvent;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;

import java.util.function.Supplier;

public class RemovePlayerPreferenceCallbackMessage {
    public static void encode(RemovePlayerPreferenceCallbackMessage message, FriendlyByteBuf buffer) {

    }

    public static RemovePlayerPreferenceCallbackMessage decode(FriendlyByteBuf buffer) {
        return null;
    }

    public static void handle(RemovePlayerPreferenceCallbackMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> NetworkWrapper.getInstance().removeTypeSelectCallback(player));
            context.setPacketHandled(true);
        }
    }
}
