package ninjaphenix.expandedstorage.base.client.menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class StorageMutatorScreen extends Screen {
    public StorageMutatorScreen(Component component) {
        super(component);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
