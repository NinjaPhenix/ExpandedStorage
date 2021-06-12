package ninjaphenix.expandedstorage.base.platform;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.BaseImpl;

public interface ConfigWrapper {
    static ConfigWrapper getInstance() {
        return BaseImpl.getInstance().getConfigWrapper();
    }

    void initialise();

    boolean isScrollingUnrestricted();

    void setScrollingRestricted(boolean value);

    ResourceLocation getPreferredContainerType();

    boolean setPreferredContainerType(ResourceLocation containerType);
}
