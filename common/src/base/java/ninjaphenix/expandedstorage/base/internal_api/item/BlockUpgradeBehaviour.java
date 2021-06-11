package ninjaphenix.expandedstorage.base.internal_api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public interface BlockUpgradeBehaviour {
    boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to);
}
