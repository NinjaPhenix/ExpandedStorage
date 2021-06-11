package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.UnaryOperator;

@Internal
@Experimental
public class TargetedTier extends Tier {
    private final ResourceLocation blockType;

    protected TargetedTier(ResourceLocation key, UnaryOperator<BlockBehaviour.Properties> blockProperties,
                           UnaryOperator<Item.Properties> itemProperties, ResourceLocation blockType, int miningLevel) {
        super(key, miningLevel, blockProperties, itemProperties);
        this.blockType = blockType;
    }

    public final boolean appliesTo(Block block) {
        return block instanceof AbstractStorageBlock storageBlock && storageBlock.blockType() == blockType;
    }

    public final ResourceLocation blockType() {
        return blockType;
    }
}

