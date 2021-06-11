package ninjaphenix.expandedstorage.base.config;

import java.util.Map;

public interface Config {
    int getVersion();

    <T extends Config> Converter<Map<String, Object>, T> getConverter();
}
