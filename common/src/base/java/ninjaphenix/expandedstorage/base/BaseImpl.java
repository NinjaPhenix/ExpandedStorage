package ninjaphenix.expandedstorage.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.item.BlockUpgradeBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.tier.Tier;
import ninjaphenix.expandedstorage.base.item.StorageConversionKit;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class BaseImpl implements BaseApi {
    private static BaseImpl instance;
    private final Map<Predicate<Block>, BlockUpgradeBehaviour> BLOCK_UPGRADE_BEHAVIOURS = new HashMap<>();
    private final Map<Pair<ResourceLocation, ResourceLocation>, AbstractStorageBlock> BLOCKS = new HashMap<>();
    private Map<ResourceLocation, Item> items = new LinkedHashMap<>();
    private Item tabIcon = Items.ENDER_CHEST;
    private int suitability = -1;

    private BaseImpl() {

    }

    public static BaseImpl getInstance() {
        if (BaseImpl.instance == null) {
            BaseImpl.instance = new BaseImpl();
        }
        return BaseImpl.instance;
    }

    @Override
    public Optional<BlockUpgradeBehaviour> getBlockUpgradeBehaviour(Block block) {
        for (Map.Entry<Predicate<Block>, BlockUpgradeBehaviour> entry : BLOCK_UPGRADE_BEHAVIOURS.entrySet()) {
            if (entry.getKey().test(block)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public void defineBlockUpgradeBehaviour(Predicate<Block> target, BlockUpgradeBehaviour behaviour) {
        BLOCK_UPGRADE_BEHAVIOURS.put(target, behaviour);
    }

    @Override
    public void defineTierUpgradePath(Component addingMod, Tier... tiers) {
        int numTiers = tiers.length;
        for (int fromIndex = 0; fromIndex < numTiers - 1; fromIndex++) {
            Tier fromTier = tiers[fromIndex];
            for (int toIndex = fromIndex + 1; toIndex < numTiers; toIndex++) {
                Tier toTier = tiers[toIndex];
                ResourceLocation itemId = Utils.resloc(fromTier.key().getPath() + "_to_" + toTier.key().getPath() + "_conversion_kit");
                if (!items.containsKey(itemId)) {
                    Item.Properties properties = fromTier.itemProperties()
                                                         .andThen(toTier.itemProperties())
                                                         .apply(new Item.Properties().tab(Utils.TAB).stacksTo(16));
                    Item kit = new StorageConversionKit(properties, fromTier.key(), toTier.key(), addingMod);
                    this.register(itemId, kit);
                }
            }
        }
    }

    @Override
    @ApiStatus.Internal
    public void register(ResourceLocation itemId, Item item) {
        items.put(itemId, item);
    }

    @Override
    public void registerContainerButtonSettings(ResourceLocation containerType, ResourceLocation texture, Component text) {
        if (PlatformUtils.getInstance().isClient()) {
            PickScreen.declareButtonSettings(containerType, texture, text);
        } else {
            throw new IllegalStateException("registerContainerButtonSettings is client only");
        }
    }

    @Override
    public void registerTieredBlock(AbstractStorageBlock block) {
        BLOCKS.putIfAbsent(new Pair<>(block.blockType(), block.blockTier()), block);
    }

    @Override
    public AbstractStorageBlock getTieredBlock(ResourceLocation blockType, ResourceLocation tier) {
        return BLOCKS.get(new Pair<>(blockType, tier));
    }

    @Override
    @ApiStatus.Internal
    public Map<ResourceLocation, Item> getAndClearItems() {
        Map<ResourceLocation, Item> items = this.items;
        this.items = null;
        return items;
    }

    @Override
    public void offerTabIcon(Item tabIcon, int suitability) {
        if (this.suitability < suitability) {
            this.suitability = suitability;
            this.tabIcon = tabIcon;
        }
    }

    @Override
    @ApiStatus.Internal
    public ItemStack tabIcon() {
        return new ItemStack(tabIcon);
    }
}
