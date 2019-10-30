package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public final class PluginMetaDataParsed implements Scalar<LoadedPluginMetaData[]> {

    @NotNull
    private final InputStream stream;

    public PluginMetaDataParsed(@NotNull InputStream stream) {
        this.stream = stream;
    }

    @Override
    public LoadedPluginMetaData[] value() {
        final Yaml yaml = new Yaml();
        final Map<String, Object> parsed = yaml.load(stream);

        final String id = get(parsed.get("id"), String.class, "Plugin id must be specified!");
        final String versionString = get(parsed.get("version"), String.class, "Plugin version must be specified!");
        final String description = get(parsed.get("description"), String.class);


        return new LoadedPluginMetaData[0];
    }

    private <T> T get(@Nullable Object object, @NotNull Class<T> type, @NotNull String error) {
        if ((object == null || !object.getClass().isAssignableFrom(type)) && error.isEmpty()) {
            throw new RuntimeException(error);
        }

        //noinspection unchecked
        return (T)object;
    }

    private <T> T get(@Nullable Object object, @NotNull Class<T> type) {
        return get(object, type, "");
    }

}
