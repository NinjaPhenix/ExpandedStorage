package ninjaphenix.expandedstorage.base;

import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.inventory.PagedContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.ScrollableContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.SingleContainerMenu;
import ninjaphenix.expandedstorage.base.item.StorageMutator;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;

import java.util.function.Supplier;

public final class BaseCommon {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Supplier<MenuType<SingleContainerMenu>> SINGLE_MENU_TYPE = Suppliers.memoize(() -> PlatformUtils.getInstance().createMenuType(Utils.SINGLE_CONTAINER_TYPE, new SingleContainerMenu.Factory()));
    public static final Supplier<MenuType<PagedContainerMenu>> PAGE_MENU_TYPE = Suppliers.memoize(() -> PlatformUtils.getInstance().createMenuType(Utils.PAGE_CONTAINER_TYPE, new PagedContainerMenu.Factory()));
    public static final Supplier<MenuType<ScrollableContainerMenu>> SCROLL_MENU_TYPE = Suppliers.memoize(() -> PlatformUtils.getInstance().createMenuType(Utils.SCROLL_CONTAINER_TYPE, new ScrollableContainerMenu.Factory()));
    private static final int ICON_SUITABILITY = 0;

    private BaseCommon() {

    }

    static void initialize(String platform) {
        BaseImpl.getInstance().setWrapperInstances(platform);
        BaseApi.getInstance().offerTabIcon(Items.CHEST, ICON_SUITABILITY);
        BaseApi.getInstance().defineTierUpgradePath(Utils.translation("itemGroup.expandedstorage"), Utils.WOOD_TIER, Utils.IRON_TIER,
                Utils.GOLD_TIER, Utils.DIAMOND_TIER, Utils.OBSIDIAN_TIER, Utils.NETHERITE_TIER);
        BaseApi.getInstance().register(Utils.resloc("chest_mutator"), new StorageMutator(new Item.Properties().stacksTo(1).tab(Utils.TAB)));
        if (PlatformUtils.getInstance().isClient()) {
            BaseApi.getInstance().registerContainerButtonSettings(Utils.SINGLE_CONTAINER_TYPE,
                    Utils.resloc("textures/gui/single_button.png"),
                    Utils.translation("screen.expandedstorage.single_screen"));
            BaseApi.getInstance().registerContainerButtonSettings(Utils.SCROLL_CONTAINER_TYPE,
                    Utils.resloc("textures/gui/scrollable_button.png"),
                    Utils.translation("screen.expandedstorage.scrollable_screen"));
            BaseApi.getInstance().registerContainerButtonSettings(Utils.PAGE_CONTAINER_TYPE,
                    Utils.resloc("textures/gui/paged_button.png"),
                    Utils.translation("screen.expandedstorage.paged_screen"));
        }
    }

    public static ResourceLocation registerStat(ResourceLocation stat) {
        ResourceLocation rv = Registry.register(Registry.CUSTOM_STAT, stat, stat); // Forge doesn't provide registries for stats
        Stats.CUSTOM.get(rv);
        return rv;
    }

    public static void warnThrowableMessage(String message, Throwable cause, Object... messageParams) {
        LOGGER.warn(new FormattedMessage(message, messageParams, cause));
    }
}
