package ninjaphenix.expandedstorage.base.platform;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

public interface ConfigWrapper {
    /**
     * Should be private, do not use.
     */
    @Deprecated
    LazyLoadedValue<ConfigWrapper> instance = new LazyLoadedValue<>(() -> Utils.getClassInstance(ConfigWrapper.class, "ninjaphenix.expandedstorage.base.platform", "ConfigWrapperImpl"));

    static ConfigWrapper getInstance() {
        return instance.get();
    }

    void initialise();

    boolean isScrollingUnrestricted();

    void setScrollingRestricted(boolean value);

    ResourceLocation getPreferredContainerType();

    boolean setPreferredContainerType(ResourceLocation containerType);
}
