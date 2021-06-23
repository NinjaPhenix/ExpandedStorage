package ninjaphenix.expandedstorage.base.wrappers;

import com.mojang.datafixers.types.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import org.lwjgl.glfw.GLFW;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private final KeyMapping configKeyMapping;
    private final boolean isClient;

    private PlatformUtilsImpl() {
        isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        configKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.expandedstorage.config", GLFW.GLFW_KEY_E, "key.categories.inventory"));
    }

    public static PlatformUtilsImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformUtilsImpl();
        }
        return INSTANCE;
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
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> blockEntitySupplier, Set<Block> blocks, Type<?> type) {
        return new BlockEntityType<>(blockEntitySupplier::apply, blocks, type);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public KeyMapping getConfigScreenKeyMapping() {
        return configKeyMapping;
    }
}
