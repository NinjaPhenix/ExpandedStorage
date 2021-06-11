package ninjaphenix.expandedstorage.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkEvent;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class ContainerTypeUpdateMessage {
    private final ResourceLocation containerType;

    public ContainerTypeUpdateMessage(ResourceLocation containerType) {
        this.containerType = containerType;
    }

    public static void encode(ContainerTypeUpdateMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.containerType);
    }

    public static ContainerTypeUpdateMessage decode(FriendlyByteBuf buffer) {
        ResourceLocation containerType = buffer.readResourceLocation();
        return new ContainerTypeUpdateMessage(containerType);
    }

    public static void handle(ContainerTypeUpdateMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            ResourceLocation containerType = message.containerType;
            context.enqueueWork(() -> NetworkWrapper.getInstance().s_setPlayerContainerType(player, containerType));
            context.setPacketHandled(true);
        }
    }
}
