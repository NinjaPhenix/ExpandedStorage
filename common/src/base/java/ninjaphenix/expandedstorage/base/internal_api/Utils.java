package ninjaphenix.expandedstorage.base.internal_api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.expandedstorage.base.config.ResourceLocationTypeAdapter;
import ninjaphenix.expandedstorage.base.internal_api.tier.Tier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Internal
@Experimental
public final class Utils {
    @Internal
    public static final String MOD_ID = "expandedstorage";
    public static final CreativeModeTab TAB = PlatformUtils.getInstance().createTab(BaseApi.getInstance()::tabIcon);
    @Internal
    public static final Component ALT_USE = new TranslatableComponent("tooltip.expandedstorage.alt_use",
            new KeybindComponent("key.sneak").withStyle(ChatFormatting.GOLD),
            new KeybindComponent("key.use").withStyle(ChatFormatting.GOLD));

    // Default tiers which all modules can, but don't need to, specify blocks for.
    // Note: each module should register their own implementation of these tiers specific to their blocks.
    public static final Tier WOOD_TIER = Tier.of(resloc("wood"), Tiers.WOOD.getLevel());
    public static final Tier IRON_TIER = Tier.of(resloc("iron"), Tiers.STONE.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier GOLD_TIER = Tier.of(resloc("gold"), Tiers.IRON.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier DIAMOND_TIER = Tier.of(resloc("diamond"), Tiers.IRON.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier OBSIDIAN_TIER = Tier.of(resloc("obsidian"), Tiers.DIAMOND.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier NETHERITE_TIER = Tier.of(resloc("netherite"), Tiers.NETHERITE.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops, Item.Properties::fireResistant);

    // Slots for Storage Tiers
    public static final int WOOD_STACK_COUNT = 27;
    public static final int IRON_STACK_COUNT = 54;
    public static final int GOLD_STACK_COUNT = 81;
    public static final int DIAMOND_STACK_COUNT = 108;
    public static final int OBSIDIAN_STACK_COUNT = 108;
    public static final int NETHERITE_STACK_COUNT = 135;

    // Item Cooldown
    public static final int QUARTER_SECOND = 5;

    // Config related

    @Internal
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Internal
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter())
                                                     .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                     .setPrettyPrinting()
                                                     .setLenient()
                                                     .create();

    // Container Types
    @Internal
    public static final ResourceLocation UNSET_CONTAINER_TYPE = Utils.resloc("auto");

    @Internal
    public static final ResourceLocation SINGLE_CONTAINER_TYPE = Utils.resloc("single");

    @Internal
    public static final ResourceLocation PAGE_CONTAINER_TYPE = Utils.resloc("page");

    @Internal
    public static final ResourceLocation SCROLL_CONTAINER_TYPE = Utils.resloc("scroll");

    // Config paths
    @Internal
    public static final String FABRIC_LEGACY_CONFIG_PATH = "ninjaphenix-container-library.json";

    @Internal
    public static final String CONFIG_PATH = "expandedstorage.json";

    @Internal
    public static final int CONTAINER_HEADER_HEIGHT = 17;

    @Internal
    public static final int INVENTORY_HEADER_HEIGHT = 14;

    @Internal
    public static final int SLOT_SIZE = 18;

    @Internal
    public static final int CONTAINER_PADDING_WIDTH = 7;

    private Utils() {

    }

    @Internal
    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(Utils.MOD_ID, path);
    }

    @Internal
    public static MutableComponent translation(String key, Object... params) {
        return new TranslatableComponent(key, params);
    }

    @Internal
    public static <K, V> Map<K, V> unmodifiableMap(Consumer<Map<K, V>> initialiser) {
        Map<K, V> map = new HashMap<>();
        initialiser.accept(map);
        return Collections.unmodifiableMap(map);
    }
}
