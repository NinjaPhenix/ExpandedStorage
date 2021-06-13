package ninjaphenix.expandedstorage.base.platform.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.IContainerFactory;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class PlatformUtilsImpl implements PlatformUtils {
    private static Boolean isClient;

    private PlatformUtilsImpl() {

    }

    @SuppressWarnings("unused")
    public static PlatformUtilsImpl getInstance() {
        return new PlatformUtilsImpl();
    }

    @Override
    public CreativeModeTab createTab(Supplier<ItemStack> icon) {
        return new CreativeModeTab(Utils.MOD_ID) {
            @Override
            public ItemStack makeIcon() {
                return icon.get();
            }
        };
    }

    @Override
    public boolean isClient() {
        if (isClient == null) {
            isClient = FMLLoader.getDist() == Dist.CLIENT;
        }
        return isClient;
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(ResourceLocation menuType, ClientContainerMenuFactory<T> factory) {
        MenuType<T> menu = new MenuType<>((IContainerFactory<T>) factory::create);
        menu.setRegistryName(menuType);
        return menu;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
