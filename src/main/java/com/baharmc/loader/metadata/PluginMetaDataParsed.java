package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
import com.baharmc.loader.utils.semanticversion.Version;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public final class PluginMetaDataParsed implements Scalar<LoadedPluginMetaData> {

    @NotNull
    private final InputStream stream;

    public PluginMetaDataParsed(@NotNull InputStream stream) {
        this.stream = stream;
    }

    @Override
    public LoadedPluginMetaData value() {
        final Yaml yaml = new Yaml();
        final Map<String, Object> parsed = yaml.load(stream);
        final String id = get(String.class, parsed.getOrDefault("id", ""), "Plugin id must be specified!");
        final String name = get(String.class, parsed.getOrDefault("name", ""), "Plugin name must be specified!");
        final String versionString = get(String.class, parsed.getOrDefault("version", ""), "Plugin version must be specified!");
        final boolean isStable = get(boolean.class, parsed.getOrDefault("isStable", true));
        final boolean isSnapshot = get(boolean.class, parsed.getOrDefault("isSnapshot", false));
        final String description = get(String.class, parsed.getOrDefault("description", ""));
        final Version version;

        try {
            version = Version.parse(versionString);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return new PluginMetaDataBasic(id, name, isStable, isSnapshot, description, version);
    }

    @NotNull
    private <T> T get(@NotNull Class<T> tClass, @NotNull Object object, @NotNull String error) {
        if (!tClass.isAssignableFrom(object.getClass()) && !error.isEmpty()) {
            throw new RuntimeException(error);
        }

        //noinspection unchecked
        return (T) object;
    }

    @NotNull
    private <T> T get(@NotNull Class<T> tClass, @NotNull Object object) {
        return get(tClass, object, "");
    }

}
