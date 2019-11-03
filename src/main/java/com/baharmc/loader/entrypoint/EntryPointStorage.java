package com.baharmc.loader.entrypoint;

import com.baharmc.loader.language.LanguageAdapted;
import com.baharmc.loader.language.LanguageAdapterException;
import com.baharmc.loader.launched.common.BaharLaunched;
import com.baharmc.loader.metadata.EntryPointMetaData;
import com.baharmc.loader.plugin.PluginContained;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class EntryPointStorage {

    @NotNull
    private final BaharLaunched launched;

    public EntryPointStorage(@NotNull BaharLaunched launched) {
        this.launched = launched;
    }

    private final Map<String, List<Entry>> entryMap = new HashMap<>();

    private List<Entry> getOrCreateEntries(@NotNull String key) {
        return entryMap.computeIfAbsent(key, z -> new ArrayList<>());
    }

    public void add(@NotNull PluginContained pluginContained,
                    @NotNull String key,
                    @NotNull EntryPointMetaData metadata,
                    @NotNull Map<String, LanguageAdapted> adapterMap) {
        if (!adapterMap.containsKey(metadata.getAdapter())) {
            throw new RuntimeException(
                "Could not find adapter '" +
                    metadata.getAdapter() +
                "' (plugin " + pluginContained.getMetadata().getId() + "!)"
            );
        }

        launched.getLogger().debug(
            "Registering new-style initializer " +
                metadata.getValue() +
                " for plugin " +
                pluginContained.getMetadata().getId() +
                " (key " + key + ")");
        getOrCreateEntries(key).add(new Entry(
            pluginContained, adapterMap.get(metadata.getAdapter()), metadata.getValue()
        ));
    }

    public <T> List<T> getEntryPoints(String key, Class<T> type) {
        final List<Entry> entries = entryMap.getOrDefault(key, new ListOf<>());
        final List<T> results = new ArrayList<>(entries.size());
        boolean hadException = false;

        for (Entry entry : entries) {
            try {
                T result = entry.getOrCreate(type);
                results.add(result);
            } catch (Throwable  t) {
                hadException = true;
                launched.getLogger().fatal(
                    "Exception occurred while getting '" + key + "' entry points @ " + entry,
                    t
                );
            }
        }

        if (hadException) {
            throw new EntryPointException("Could not look up entries for entry point " + key + "!");
        } else {
            return results;
        }
    }

    private static final class Entry {

        private final Map<Class<?>, Object> instanceMap = new IdentityHashMap<>();

        @NotNull
        private final PluginContained pluginContained;

        @NotNull
        private final LanguageAdapted languageAdapted;

        @NotNull
        private final String value;

        public Entry(@NotNull PluginContained pluginContained, @NotNull LanguageAdapted languageAdapted,
                     @NotNull String value) {
            this.pluginContained = pluginContained;
            this.languageAdapted = languageAdapted;
            this.value = value;
        }

        @NotNull
        public <T> T getOrCreate(Class<T> type) throws LanguageAdapterException {
            Object o = instanceMap.get(type);

            if (o == null) {
                o = create(type);
                instanceMap.put(type, o);
            }

            //noinspection unchecked
            return (T) o;
        }

        private <T> T create(Class<T> type) throws LanguageAdapterException {
            return languageAdapted.create(pluginContained, value, type);
        }

    }

}
