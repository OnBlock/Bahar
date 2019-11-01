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
        final Map<String, Object> parsed = new Yaml().load(stream);

        try {
            return new PluginMetaDataBasic(
                get(String.class, parsed.getOrDefault("id", ""), "Plugin id must be specified!"),
                get(String.class, parsed.getOrDefault("name", ""), "Plugin name must be specified!"),
                get(boolean.class, parsed.getOrDefault("isStable", true)),
                get(boolean.class, parsed.getOrDefault("isSnapshot", false)),
                get(String.class, parsed.getOrDefault("description", "")),
                Version.parse(
                    get(
                        String.class,
                        parsed.getOrDefault("version", ""),
                        "Plugin version must be specified!"
                    )
                )
            );
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
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
