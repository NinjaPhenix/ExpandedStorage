package ninjaphenix.expandedstorage.base.config.button;

import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public final class ButtonOffset {
    private final Set<String> mods;
    private final int offset;

    // todo: rework into Set<Conditions>, int offset
    public ButtonOffset(Set<String> mods, int offset) {
        this.mods = mods;
        this.offset = offset;
    }

    public boolean areModsPresent(Set<String> loadedMods) {
        return loadedMods.containsAll(mods);
    }

    public int offset() {
        return offset;
    }
}
