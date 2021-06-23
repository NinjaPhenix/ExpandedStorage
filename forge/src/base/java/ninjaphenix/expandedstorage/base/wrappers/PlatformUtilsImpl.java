package ninjaphenix.expandedstorage.base.wrappers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.IContainerFactory;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private final KeyMapping configKeyMapping;
    private final boolean isClient;

    private PlatformUtilsImpl() {
        isClient = FMLLoader.getDist() == Dist.CLIENT;
        configKeyMapping = new KeyMapping("key.expandedstorage.config", KeyConflictContext.GUI, KeyModifier.SHIFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E, "key.categories.inventory");
    }

    public static PlatformUtilsImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformUtilsImpl();
        }
        return INSTANCE;
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

    @Override
    public KeyMapping getConfigScreenKeyMapping() {
        return configKeyMapping;
    }
}
