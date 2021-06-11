package ninjaphenix.expandedstorage.base.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.client.menu.AbstractScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.Collections;
import java.util.stream.Collectors;

public class ReiCompat implements REIPluginV0 {
    private static Rectangle toReiRect(Rect2i rect) {
        return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public ResourceLocation getPluginIdentifier() {
        return Utils.resloc("rei_plugin");
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler.getInstance().registerExclusionZones(AbstractScreen.class, () -> {
            if (Minecraft.getInstance().screen instanceof AbstractScreen<?, ?> screen) {
                return screen.getExclusionZones().stream().map(ReiCompat::toReiRect).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }
}
