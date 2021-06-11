package ninjaphenix.expandedstorage.base.internal_api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import ninjaphenix.expandedstorage.base.BaseImpl;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.item.BlockUpgradeBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.tier.Tier;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.jetbrains.annotations.ApiStatus.Experimental;
import static org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public interface BaseApi {
    static BaseApi getInstance() {
        return BaseImpl.getInstance();
    }

    /**
     * Sets the ExpandedStorage creative tab icon only if it is more suitable than previously supplied icons.
     *
     * @param suitability Any integer between 1 and 949 ( inclusive ), you should not override ExpandedStorage's icon.
     */
    void offerTabIcon(Item tabIcon, int suitability);

    /**
     * Define a new upgrade path, will register all necessary upgrade items excluding duplicates.
     *
     * @param addingMod Friendly mod name for upgrade item tooltip
     * @param tiers     Storage block tiers in order of upgrade path
     */
    void defineTierUpgradePath(Component addingMod, Tier... tiers);

    @Internal
    ItemStack tabIcon();

    Optional<BlockUpgradeBehaviour> getBlockUpgradeBehaviour(Block block);

    void defineBlockUpgradeBehaviour(Predicate<Block> target, BlockUpgradeBehaviour behaviour);

    @Internal
    void register(ResourceLocation id, Item item);

    /**
     * @deprecated Temporarily internal, proper API will be introduced at a later date.
     */
    @Internal
    @Deprecated
    @ScheduledForRemoval
    void registerContainerButtonSettings(ResourceLocation containerType, ResourceLocation texture, Component text);

    /**
     * @deprecated Will be removed with no replacement.
     */
    @Deprecated
    @ScheduledForRemoval
    void registerTieredBlock(AbstractStorageBlock block);

    /**
     * @deprecated Will be removed with no replacement.
     */
    @Deprecated
    @ScheduledForRemoval
    AbstractStorageBlock getTieredBlock(ResourceLocation blockType, ResourceLocation tier);

    @Internal
    Map<ResourceLocation, Item> getAndClearItems();
}
