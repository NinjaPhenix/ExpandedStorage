package ninjaphenix.expandedstorage.base.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.HashSet;
import java.util.Set;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (ConfigScreenFactory<Screen>) parent -> {
            Set<ResourceLocation> values = new HashSet<>();
            values.add(Utils.SINGLE_CONTAINER_TYPE);
            values.add(Utils.PAGE_CONTAINER_TYPE);
            values.add(Utils.SCROLL_CONTAINER_TYPE);
            return new PickScreen(values, parent);
        };
    }
}
