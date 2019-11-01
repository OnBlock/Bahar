package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

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
        final Optional<String> id = get(String.class, parsed.getOrDefault("id", ""));

        return new LoadedPluginMetaData[0];
    }

    @NotNull
    private <T> Optional<T> get(@NotNull Class<T> tClass, @NotNull Object object) {
        if (!tClass.isAssignableFrom(object.getClass())) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((T) object);
    }

}
