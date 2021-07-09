package ninjaphenix.expandedstorage.base.wrappers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.loading.FMLPaths;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.config.Config;
import ninjaphenix.expandedstorage.base.config.ConfigV0;
import ninjaphenix.expandedstorage.base.config.Converter;
import ninjaphenix.expandedstorage.base.config.LegacyFactory;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

final class ConfigWrapperImpl implements ConfigWrapper {
    private static ConfigWrapperImpl INSTANCE;
    private Path configPath;
    private ConfigV0 config;

    public static ConfigWrapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigWrapperImpl();
        }
        return INSTANCE;
    }

    public void initialise() {
        configPath = FMLPaths.CONFIGDIR.get().resolve("expandedstorage.json");
        config = this.getConfig();
    }

    public boolean isScrollingUnrestricted() {
        return !config.isScrollingRestricted();
    }

    public void setScrollingRestricted(boolean value) {
        if (config.isScrollingRestricted() == value) {
            config.setScrollingRestricted(!value);
            this.saveConfig(config);
        }
    }

    public ResourceLocation getPreferredContainerType() {
        return config.getContainerType();
    }

    public boolean setPreferredContainerType(ResourceLocation containerType) {
        if ((Utils.UNSET_CONTAINER_TYPE.equals(containerType) || Utils.PAGE_CONTAINER_TYPE.equals(containerType)
                || Utils.SCROLL_CONTAINER_TYPE.equals(containerType) || Utils.SINGLE_CONTAINER_TYPE.equals(containerType))
                && containerType != config.getContainerType()) {
            config.setContainerType(containerType);
            this.saveConfig(config);
            return true;
        }
        return false;
    }

    private <T extends Config> void saveConfig(T config) {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            Map<String, Object> configValues = config.getConverter().toSource(config);
            configValues.put("config_version", config.getVersion());
            Utils.GSON.toJson(configValues, Utils.MAP_TYPE, Utils.GSON.newJsonWriter(writer));
        } catch (IOException e) {
            BaseCommon.LOGGER.warn("Failed to save Expanded Storage's config.", e);
        }
    }

    private ConfigV0 getConfig() {
        Path configDirectory = FMLPaths.CONFIGDIR.get();
        Path oldPath = configDirectory.resolve("expandedstorage-client.toml");
        boolean triedLoadingOldConfig = false;
        Path newPath = configDirectory.resolve(configPath);
        boolean triedLoadingNewConfig = false;
        ConfigV0 config = null;
        if (Files.exists(newPath)) {
            triedLoadingNewConfig = true;
            config = this.loadConfig(newPath, ConfigV0.Factory.INSTANCE, false);
        }
        if (Files.exists(oldPath)) {
            triedLoadingOldConfig = true;
            try (BufferedReader reader = Files.newBufferedReader(oldPath)) {
                reader.mark(8192); // default buffered reader character buffer, our file is only ~400 characters so should be fine.
                String lines = reader.lines().collect(Collectors.joining());
                reader.reset();
                CommentedConfig tomlConfig = TomlFormat.instance().createParser().parse(reader);
                ConfigV0 oldConfig = this.convert(tomlConfig, -1, LegacyFactory.INSTANCE);
                if (config == null && oldConfig != null) {
                    config = oldConfig;
                    this.saveConfig(config);
                }
                this.backupFile(oldPath, String.format("Failed to backup legacy Expanded Storage config, '%s'.", oldPath.getFileName().toString()), lines);
            } catch (IOException e) {
                if (config == null) {
                    BaseCommon.LOGGER.warn("Failed to load legacy Expanded Storage Config, new default config will be used.", e);
                }
            }
        }
        if (config == null) {
            if (triedLoadingOldConfig || triedLoadingNewConfig) {
                BaseCommon.LOGGER.warn("Could not load an existing config, Expanded Storage is using it's default config.");
            }
            config = new ConfigV0();
            this.saveConfig(config);
        }
        return config;
    }

    // Tries to load a config file, returns null if loading fails.
    // Will need to be reworked to allow converting between ConfigV0 and ConfigV1
    // essentially converter will need to be decided in this method based on the value of "config_version"
    private <T extends Config> T convertToConfig(String lines, Converter<Map<String, Object>, T> converter, Path configPath) {
        try {
            Map<String, Object> configMap = Utils.GSON.fromJson(lines, Utils.MAP_TYPE);
            // Do not edit, method returns a Double, we need an integer
            int configVersion = Mth.floor((Double) configMap.getOrDefault("config_version", -1.0D));
            return this.convert(configMap, configVersion, converter);
        } catch (JsonParseException e) {
            String configFileName = configPath.getFileName().toString();
            BaseCommon.warnThrowableMessage("Failed to convert config, backing config '{}'.", e, configFileName);
            this.backupFile(configPath, String.format("Failed to backup expanded storage config which failed to read, '%s'.%n", configFileName), lines);
            return null;
        }
    }

    private <A, B extends Config> B convert(A config, int configVersion, Converter<A, B> converter) {
        if (configVersion == converter.getSourceVersion()) {
            B returnValue = converter.fromSource(config);
            if (returnValue == null) {
                return null;
            }
            if (returnValue.getVersion() == converter.getTargetVersion()) {
                return returnValue;
            } else {
                throw new IllegalStateException(String.format("CODE ERROR: Converter converted to an invalid config, expected version %s, got %s.", converter.getTargetVersion(), returnValue.getVersion()));
            }
        } else {
            throw new IllegalStateException(String.format("CODE ERROR: Converter converted to an invalid config, expected version %s, got %s.", converter.getSourceVersion(), configVersion));
        }
    }

    private void backupFile(Path path, String failureMessage, String contents) {
        try {
            Path backupPath = path.resolveSibling(path.getFileName() + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date()) + ".backup");
            Files.move(path, backupPath);
        } catch (IOException e2) {
            BaseCommon.LOGGER.warn(failureMessage, e2);
            if (contents != null) {
                BaseCommon.LOGGER.warn(contents);
            }
        }
    }

    private <T extends Config> T loadConfig(Path configPath, Converter<Map<String, Object>, T> converter, boolean isLegacy) {
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String configLines = reader.lines().collect(Collectors.joining());
            return this.convertToConfig(configLines, converter, configPath);
        } catch (IOException e) {
            String configFileName = configPath.getFileName().toString();
            BaseCommon.warnThrowableMessage("Failed to read {}Expanded Storage config, '{}'.", e, isLegacy ? "legacy " : "", configFileName);
            e.printStackTrace();
            this.backupFile(configPath, String.format("Failed to backup %sExpanded Storage config, '%s'.%n", isLegacy ? "legacy " : "", configFileName), null);
        }
        return null;
    }
}
