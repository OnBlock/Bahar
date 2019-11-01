package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
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

        return new PluginMetaDataBasic(id, name);
    }

    @NotNull
    private <T> T get(@NotNull Class<T> tClass, @NotNull Object object, @NotNull String error) {
        if (!tClass.isAssignableFrom(object.getClass())) {
            throw new RuntimeException(error);
        }

        //noinspection unchecked
        return (T) object;
    }

}
