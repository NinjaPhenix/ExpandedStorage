package ninjaphenix.expandedstorage.base.internal_api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@Experimental
public interface ItemUpgradeBehaviour {
    boolean tryUpgradeItem(Level level, Player player, ItemStack upgrade, ItemStack target, ResourceLocation from, ResourceLocation to);
}
