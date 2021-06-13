package ninjaphenix.expandedstorage.base.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.base.BaseImpl;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;

import java.util.function.Supplier;

public interface PlatformUtils {
    static PlatformUtils getInstance() {
        return BaseImpl.getInstance().getPlatformWrapper();
    }

    CreativeModeTab createTab(Supplier<ItemStack> icon);

    boolean isClient();

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(ResourceLocation menuType, ClientContainerMenuFactory<T> factory);

    boolean isModLoaded(String modId);
}
