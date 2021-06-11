package ninjaphenix.expandedstorage.base.internal_api.item;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Internal
@Experimental
public enum MutationMode {
    MERGE("merge", 0),
    SPLIT("split", 1),
    ROTATE("rotate", 2);

    private static final MutationMode[] VALUES = MutationMode.values();
    private final String name;
    private final int index;

    MutationMode(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static MutationMode from(byte index) {
        if (index >= 0 && index < MutationMode.VALUES.length) {
            return MutationMode.VALUES[index];
        }
        return null;
    }

    public byte toByte() {
        return (byte) index;
    }

    @Override
    public String toString() {
        return name;
    }

    public MutationMode next() {
        return MutationMode.VALUES[(index + 1) % MutationMode.VALUES.length];
    }
}
