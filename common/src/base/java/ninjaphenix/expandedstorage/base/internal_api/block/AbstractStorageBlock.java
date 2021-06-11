package ninjaphenix.expandedstorage.base.internal_api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractStorageBlockEntity;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
@Experimental
public abstract class AbstractStorageBlock extends Block {
    private final ResourceLocation blockId;
    private final ResourceLocation blockTier;

    public AbstractStorageBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier) {
        super(properties);
        this.blockId = blockId;
        this.blockTier = blockTier;
    }

    public abstract ResourceLocation blockType();

    public final ResourceLocation blockId() {
        return blockId;
    }

    public final ResourceLocation blockTier() {
        return blockTier;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof AbstractStorageBlockEntity entity && stack.hasCustomHoverName()) {
            entity.setCustomName(stack.getHoverName());
        }
    }
}
