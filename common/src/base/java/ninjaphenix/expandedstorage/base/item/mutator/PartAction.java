package ninjaphenix.expandedstorage.base.item.mutator;

import net.minecraft.nbt.CompoundTag;

public abstract class PartAction extends Action {
    public abstract void storeData(CompoundTag tag);

    public final void finish() {

    }
}
