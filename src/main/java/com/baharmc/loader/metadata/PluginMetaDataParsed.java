package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
import com.baharmc.loader.plugin.metadata.Contact;
import com.baharmc.loader.plugin.metadata.License;
import com.baharmc.loader.plugin.metadata.Person;
import com.baharmc.loader.plugin.metadata.PluginDependency;
import com.baharmc.loader.utils.semanticversion.Version;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
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
                ),
                new License() {
                    @NotNull
                    @Override
                    public List<String> getLicenses() {
                        final boolean one = parsed.containsKey("license");
                        final boolean more = parsed.containsKey("licenses");

                        if (one) {
                            return new ListOf<>(
                                get(String.class, parsed.getOrDefault("license", ""))
                            );
                        } else if (more) {
                            //noinspection unchecked
                            return (List<String>) get(List.class, parsed.getOrDefault("licenses", new ListOf<>()));
                        }
                        
                        return new ListOf<>();
                    }
                },
                new ListOf<>(),
                new ListOf<>(),
                new ListOf<>()
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
