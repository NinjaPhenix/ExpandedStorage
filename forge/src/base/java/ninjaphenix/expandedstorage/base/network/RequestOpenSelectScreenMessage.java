package ninjaphenix.expandedstorage.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;

import java.util.function.Supplier;

public final class RequestOpenSelectScreenMessage {
    public static void encode(RequestOpenSelectScreenMessage message, FriendlyByteBuf buffer) {

    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static RequestOpenSelectScreenMessage decode(FriendlyByteBuf buffer) {
        return new RequestOpenSelectScreenMessage();
    }

    public static void handle(RequestOpenSelectScreenMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        NetworkEvent.Context context = wrappedContext.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            if (player.containerMenu instanceof AbstractContainerMenu_<?> menu) {
                context.enqueueWork(() -> NetworkWrapper.getInstance().s2c_openSelectScreen(player, (type) -> NetworkHooks.openGui(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return menu.getDisplayName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player1) {
                        return NetworkWrapper.getInstance().createMenu(windowId, menu.pos, menu.getContainer(), inventory, menu.getDisplayName());
                    }
                }, buffer -> buffer.writeBlockPos(menu.pos).writeInt(menu.getContainer().getContainerSize()))));
            } else {
                context.enqueueWork(() -> NetworkWrapper.getInstance().s2c_openSelectScreen(player, null));
            }
            context.setPacketHandled(true);
        }
    }
}
