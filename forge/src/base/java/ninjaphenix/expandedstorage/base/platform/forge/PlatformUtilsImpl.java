package ninjaphenix.expandedstorage.base.platform.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.IContainerFactory;
import ninjaphenix.expandedstorage.base.config.button.ButtonOffset;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
    public ButtonOffset[] getButtonOffsetConfig() {
        return new ButtonOffset[] {
                new ButtonOffset(Collections.singleton("quark"), -36)
        };
    }

    @Override
    public Set<String> getLoadedModIds() {
        var mods = ModList.get().getMods();
        var modIds = new HashSet<String>();
        for (ModInfo mod : mods) {
            modIds.add(mod.getModId());
        }
        return modIds;
    }
}
