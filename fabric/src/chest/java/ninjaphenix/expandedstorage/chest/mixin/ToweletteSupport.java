package ninjaphenix.expandedstorage.chest.mixin;

import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.towelette.api.Fluidloggable;

@Mixin(ChestBlock.class)
public class ToweletteSupport implements Fluidloggable {
}
