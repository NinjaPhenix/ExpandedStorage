package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.UnaryOperator;

@Internal
@Experimental
public class Tier {
    private final ResourceLocation key;
    private final UnaryOperator<Item.Properties> itemProperties;
    private final UnaryOperator<BlockBehaviour.Properties> blockProperties;
    private final int miningLevel;

    protected Tier(ResourceLocation key, int miningLevel, UnaryOperator<BlockBehaviour.Properties> blockProperties,
                   UnaryOperator<Item.Properties> itemProperties) {
        this.key = key;
        this.itemProperties = itemProperties;
        this.blockProperties = blockProperties;
        this.miningLevel = miningLevel;
    }

    public static Tier of(ResourceLocation key, int miningLevel) {
        return new Tier(key, miningLevel, UnaryOperator.identity(), UnaryOperator.identity());
    }

    public static Tier of(ResourceLocation key, int miningLevel, UnaryOperator<BlockBehaviour.Properties> blockProperties) {
        return new Tier(key, miningLevel, blockProperties, UnaryOperator.identity());
    }

    public static Tier of(ResourceLocation key, int miningLevel, UnaryOperator<BlockBehaviour.Properties> blockProperties,
                          UnaryOperator<Item.Properties> itemProperties) {
        return new Tier(key, miningLevel, blockProperties, itemProperties);
    }

    public final ResourceLocation key() {
        return key;
    }

    public final UnaryOperator<Item.Properties> itemProperties() {
        return itemProperties;
    }

    public UnaryOperator<BlockBehaviour.Properties> blockProperties() {
        return blockProperties;
    }

    public final int miningLevel() {
        return miningLevel;
    }

    public final boolean requiresTool() {
        return miningLevel > 0;
    }
}

