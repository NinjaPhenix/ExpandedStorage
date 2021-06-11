package ninjaphenix.expandedstorage.base.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class OpenSelectScreenMessage {
    private final Set<ResourceLocation> containerTypeOptions;

    public OpenSelectScreenMessage(Set<ResourceLocation> containerTypeOptions) {
        this.containerTypeOptions = containerTypeOptions;
    }

    public static void encode(OpenSelectScreenMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.containerTypeOptions.size());
        message.containerTypeOptions.forEach(buffer::writeResourceLocation);
    }

    public static OpenSelectScreenMessage decode(FriendlyByteBuf buffer) {
        Set<ResourceLocation> containerTypeOptions = new HashSet<>();
        int options = buffer.readInt();
        for (int i = 0; i < options; i++) {
            containerTypeOptions.add(buffer.readResourceLocation());
        }
        return new OpenSelectScreenMessage(containerTypeOptions);
    }

    public static void handle(OpenSelectScreenMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        message.openScreen(context);
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT) // Required otherwise PickScreen will be classloaded always, even when wrapped in DistExecutor
    private void openScreen(NetworkEvent.Context context) {
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(new PickScreen(containerTypeOptions, null)));
    }
}
