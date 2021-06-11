package ninjaphenix.expandedstorage.base.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.client.menu.AbstractScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ExpandedStorageJeiPlugin implements IModPlugin {
    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return Utils.resloc("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractScreen.class, new IGuiContainerHandler<AbstractScreen<?, ?>>() {
            @NotNull
            @Override
            public List<Rect2i> getGuiExtraAreas(AbstractScreen<?, ?> screen) {
                return screen.getExclusionZones();
            }
        });
    }
}
