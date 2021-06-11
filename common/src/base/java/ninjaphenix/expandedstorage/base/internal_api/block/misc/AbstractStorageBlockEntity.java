package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

/**
 * todo: look into how block state updating occurs.
 */
@Internal
@Experimental
public abstract class AbstractStorageBlockEntity extends BlockEntity implements Nameable {
    private LockCode lockKey;
    private Component customName;

    public AbstractStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
        lockKey = LockCode.NO_LOCK;
    }

    public static void alertBlockLocked(Player player, Component displayName) {
        player.displayClientMessage(new TranslatableComponent("container.isLocked", displayName), true);
        player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        lockKey = LockCode.fromTag(tag);
        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        lockKey.addToTag(tag);
        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName));
        }
        return tag;
    }

    public boolean canPlayerInteractWith(Player player) {
        return !player.isSpectator() && lockKey.unlocksWith(player.getMainHandItem());
    }

    @Override
    public final Component getName() {
        return this.hasCustomName() ? customName : this.getDefaultName();
    }

    public abstract Component getDefaultName();

    @Override
    public final boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public final Component getCustomName() {
        return customName;
    }

    public final void setCustomName(Component name) {
        customName = name;
    }
}
