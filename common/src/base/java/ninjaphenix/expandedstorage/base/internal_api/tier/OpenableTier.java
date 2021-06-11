package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public class OpenableTier extends TargetedTier {
    private final int slots;

    public OpenableTier(Tier parent, ResourceLocation blockType, int slots) {
        super(parent.key(), parent.blockProperties(), parent.itemProperties(), blockType, parent.miningLevel());
        this.slots = slots;
    }

    public final int slots() {
        return slots;
    }
}
