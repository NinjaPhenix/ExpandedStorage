package ninjaphenix.expandedstorage.base;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import ninjaphenix.expandedstorage.base.client.menu.PagedScreen;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.client.menu.ScrollableScreen;
import ninjaphenix.expandedstorage.base.client.menu.SingleScreen;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Mod("expandedstorage")
public final class Main {
    public Main() {
        BaseCommon.initialize("forge");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            BaseApi.getInstance().getAndClearItems().forEach((key, value) -> registry.register(value.setRegistryName(key)));
        });
        modEventBus.addGenericListener(MenuType.class, (RegistryEvent.Register<MenuType<?>> event) -> {
            IForgeRegistry<MenuType<?>> registry = event.getRegistry();
            registry.registerAll(BaseCommon.SINGLE_MENU_TYPE.get(), BaseCommon.PAGE_MENU_TYPE.get(), BaseCommon.SCROLL_MENU_TYPE.get());
        });
        modEventBus.addListener((FMLClientSetupEvent event) -> {
            MenuScreens.register(BaseCommon.SINGLE_MENU_TYPE.get(), SingleScreen::new);
            MenuScreens.register(BaseCommon.PAGE_MENU_TYPE.get(), PagedScreen::new);
            MenuScreens.register(BaseCommon.SCROLL_MENU_TYPE.get(), ScrollableScreen::new);
        });
        if (PlatformUtils.getInstance().isClient()) {
            this.registerConfigGuiHandler();
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, (GuiScreenEvent.InitGuiEvent.Post event) -> {
                if (event.getGui() instanceof PagedScreen screen) {
                    int width = 54;
                    int x = screen.getLeftPos() + screen.getImageWidth() - 61;
                    int originalX = x;
                    int y = screen.getTopPos() + screen.getImageHeight() - 96;
                    var renderableChildren = new ArrayList<>(event.getWidgetList());
                    renderableChildren.sort(Comparator.comparingInt(a -> -a.x));
                    for (var widget : renderableChildren) {
                        if (this.regionIntersects(widget, x, y, width, 12)) {
                            x = widget.x - width - 2;
                        }
                    }
                    screen.createPageButtons(x == originalX, x, y);
                }
            });
        }
        new ninjaphenix.expandedstorage.barrel.Main();
        new ninjaphenix.expandedstorage.chest.Main();
        new ninjaphenix.expandedstorage.old_chest.Main();
    }

    private boolean regionIntersects(AbstractWidget widget, int x, int y, int width, int height) {
        return widget.x <= x + width && y <= widget.y + widget.getHeight() ||
                x <= widget.x + widget.getWidth() && widget.y <= y + height;
    }

    @OnlyIn(Dist.CLIENT)
    private void registerConfigGuiHandler() {
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> {
            Set<ResourceLocation> values = new HashSet<>();
            values.add(Utils.SINGLE_CONTAINER_TYPE);
            values.add(Utils.PAGE_CONTAINER_TYPE);
            values.add(Utils.SCROLL_CONTAINER_TYPE);
            return new PickScreen(values, screen);
        });
    }
}
