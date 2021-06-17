package ninjaphenix.expandedstorage.base.item.mutator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class ActionGroup implements MenuEntry {
    private final MenuEntry visible;
    private final Action[] actions;
    private final Component name;

    public ActionGroup(MenuEntry visible, Component name, Action... actions) {
        this.visible = visible;
        this.name = name;
        this.actions = actions;
    }

    @Override
    public boolean usable(BlockState state) {
        return visible.usable(state);
    }

    public Action[] actions() {
        return actions;
    }

    public Component name() {
        return name;
    }

    public int size() {
        return actions.length;
    }
}
