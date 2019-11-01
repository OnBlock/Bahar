package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
import com.baharmc.loader.plugin.metadata.ContactBasic;
import com.baharmc.loader.plugin.metadata.DependencyBasic;
import com.baharmc.loader.plugin.metadata.PersonBasic;
import com.baharmc.loader.utils.semanticversion.Version;
import org.cactoos.Scalar;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PluginMetaDataParsed implements Scalar<LoadedPluginMetaData> {

    @NotNull
    private final InputStream stream;

    public PluginMetaDataParsed(@NotNull InputStream stream) {
        this.stream = stream;
    }

    @SuppressWarnings("unchecked")
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
                () -> {
                    final boolean one = parsed.containsKey("license");
                    final boolean more = parsed.containsKey("licenses");

                    if (!one && !more) {
                        return new ListOf<>();
                    }

                    if (one) {
                        return new ListOf<>(
                            get(String.class, parsed.getOrDefault("license", ""))
                        );
                    }

                    return (List<String>) get(List.class, parsed.getOrDefault("licenses", new ListOf<>()));
                },
                new ListOf<>(
                    new Mapped<>(
                        input -> {
                            final Map<String, Object> person = (Map<String, Object>) input.getValue();

                            return new PersonBasic(
                                input.getKey(),
                                new ListOf<>(
                                    (Collection<String>) person.getOrDefault("roles", new ListOf<>())
                                ),
                                new ListOf<>(
                                    new Mapped<>(
                                        entry -> new ContactBasic(entry.getKey(), entry.getValue()),
                                        ((Map<String, String>) person.getOrDefault("contacts", new MapOf<>())).entrySet()
                                    )
                                )
                            );
                        },
                        ((Map<String, Object>) parsed.getOrDefault("authors", new MapOf<>())).entrySet()
                    )
                ),
                new ListOf<>(
                    new Mapped<>(
                        input -> new ContactBasic(input.getKey(), input.getValue()),
                        ((Map<String, String>) parsed.getOrDefault("contacts", new MapOf<>())).entrySet()
                    )
                ),
                new ListOf<>(
                    new Mapped<>(
                        input -> new DependencyBasic(input.getKey(), input.getValue()),
                        ((Map<String, String>) parsed.getOrDefault("depends", new MapOf<>())).entrySet()
                    )
                ),
                () -> new ListOf<>(
                    new Mapped<>(
                        input -> new EntryPointMetaDataBasic("main", input),
                        (List<String>) parsed.getOrDefault("main", "")
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
