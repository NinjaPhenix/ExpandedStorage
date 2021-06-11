package ninjaphenix.expandedstorage.base;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.core.Registry;
import ninjaphenix.expandedstorage.base.client.menu.PagedScreen;
import ninjaphenix.expandedstorage.base.client.menu.ScrollableScreen;
import ninjaphenix.expandedstorage.base.client.menu.SingleScreen;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.ModuleInitializer;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Comparator;
import java.util.List;

public final class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        BaseCommon.initialize();
        if (PlatformUtils.getInstance().isClient()) {
            ScreenRegistry.register(BaseCommon.SCROLL_MENU_TYPE, ScrollableScreen::new);
            ScreenRegistry.register(BaseCommon.PAGE_MENU_TYPE, PagedScreen::new);
            ScreenRegistry.register(BaseCommon.SINGLE_MENU_TYPE, SingleScreen::new);
        }
        BaseApi.getInstance().getAndClearItems().forEach((id, item) -> Registry.register(Registry.ITEM, id, item));

        List<EntrypointContainer<ModuleInitializer>> entries = FabricLoader.getInstance().getEntrypointContainers("expandedstorage-module", ModuleInitializer.class);
        // Note, you should not rely on this sorting, this is purely for creative tab ordering.
        entries.sort(Comparator.comparing(entrypoint -> entrypoint.getProvider().getMetadata().getName()));
        entries.forEach(e -> e.getEntrypoint().initialize());

        /* GOALS
         *
         * WIP - Provide base implementation for openable storage blocks such as barrels, chests, and old chests.
         * Provide a centralised api for kubejs and java to register new tiers and therefore blocks.
         * Provide base implementation for networking containers to and from client
         *  note: will only support openable containers like barrels, chests, and old chests.
         *
         * Probably a bunch of other stuff I can't think of.
         */
    }
}
