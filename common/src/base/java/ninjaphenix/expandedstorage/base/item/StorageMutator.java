package ninjaphenix.expandedstorage.base.item;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import ninjaphenix.expandedstorage.base.client.menu.StorageMutatorScreen;
import ninjaphenix.expandedstorage.base.item.mutator.MenuEntry;

import java.util.HashMap;

public class StorageMutator extends Item {
    private static final HashMap<ResourceLocation, MenuEntry> ALL_ACTIONS = new HashMap<>();

    public StorageMutator(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        if (player != null && level.isClientSide()) {
            Minecraft.getInstance().setScreen(new StorageMutatorScreen(new TextComponent("TRANSLATE"))); // todo: translate
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "item.expandedstorage.storage_mutator";
    }
}
