package ninjaphenix.expandedstorage.old_chest.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OldChestBlockEntity extends AbstractOpenableStorageBlockEntity {
    public OldChestBlockEntity(BlockEntityType<OldChestBlockEntity> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType, blockId);
    }

    @NotNull
    @Override
    protected IItemHandler createItemHandler(Level level, BlockState state, BlockPos pos, @Nullable Direction side) {
        return AbstractChestBlock.createItemHandler(level, state, pos).orElse(AbstractOpenableStorageBlockEntity.createGenericItemHandler(this));
    }
}
