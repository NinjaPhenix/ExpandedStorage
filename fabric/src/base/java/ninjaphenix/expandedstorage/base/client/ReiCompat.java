package ninjaphenix.expandedstorage.base.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.client.renderer.Rect2i;
import ninjaphenix.expandedstorage.base.client.menu.AbstractScreen;

public class ReiCompat implements REIClientPlugin {
    private static Rectangle toReiRect(Rect2i rect) {
        return new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractScreen.class, (AbstractScreen<?, ?> screen) -> CollectionUtils.map(screen.getExclusionZones(), ReiCompat::toReiRect));
    }
}
