package ninjaphenix.expandedstorage.base.wrappers;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private final boolean isClient;
    private boolean configKeyRequiresShift = true;
    private final Supplier<Object> configKeyMapping = Suppliers.memoize(this::createConfigKey);

    private PlatformUtilsImpl() {
        isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static PlatformUtilsImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformUtilsImpl();
        }
        return INSTANCE;
    }

    private KeyMapping createConfigKey() {
        if (FabricLoader.getInstance().isModLoaded("amecs")) {
            var classLoader = PlatformUtilsImpl.class.getClassLoader();
            try {
                var modifiersClass = classLoader.loadClass("de.siphalor.amecs.api.KeyModifiers");
                var modifiers = modifiersClass.getConstructor().newInstance();
                modifiersClass.getDeclaredMethod("setShift", boolean.class).invoke(modifiers, true);
                var keybindClass = classLoader.loadClass("de.siphalor.amecs.api.AmecsKeyBinding");
                var keybind = KeyBindingHelper.registerKeyBinding((KeyMapping) keybindClass.getConstructor(ResourceLocation.class, InputConstants.Type.class, int.class, String.class, modifiersClass)
                                                                                           .newInstance(Utils.resloc("config"), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_W, "key.categories.inventory", modifiers));
                configKeyRequiresShift = false;
                return keybind;
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                System.err.println("Amecs loaded, but failed to use api, please report this.");
            }
        }
        return KeyBindingHelper.registerKeyBinding(new KeyMapping("key.expandedstorage.config", GLFW.GLFW_KEY_W, "key.categories.inventory"));
    }

    @Override
    public CreativeModeTab createTab(Supplier<ItemStack> icon) {
        FabricItemGroupBuilder.build(new ResourceLocation("dummy"), null); // Fabric API is dumb.
        return new CreativeModeTab(CreativeModeTab.TABS.length - 1, Utils.MOD_ID) {
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
        return ScreenHandlerRegistry.registerExtended(menuType, factory::create);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public KeyMapping getConfigScreenKeyMapping() {
        return (KeyMapping) configKeyMapping.get();
    }

    @Override
    public boolean configKeyRequiresShift() {
        return configKeyRequiresShift;
    }
}
