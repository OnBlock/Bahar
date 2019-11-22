package com.baharmc.loader.loaded;

import com.baharmc.loader.discovery.PluginCandidate;
import com.baharmc.loader.discovery.PluginResolutionException;
import com.baharmc.loader.entrypoint.EntryPointStorage;
import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface BaharLoaded {

    static BaharLoaded getInstance() {
        if (BaharLoaderBasic.INSTANCE == null) {
            throw new RuntimeException("Accessed BaharLoaded too early!");
        }

        return BaharLoaderBasic.INSTANCE;
    }

    void load();

    void lock();

    @NotNull
    <T> List<T> getEntryPoints(@NotNull String key, @NotNull Class<T> type);

    @NotNull
    EntryPointStorage getEntryPointStorage();

    @NotNull
    MappingResolved getMappingResolver();

    @NotNull
    PluginContained getPluginContained(String id);

    @NotNull
    Collection<PluginContained> getAllPlugins();

    @NotNull
    PluginContained getRuntime();

    boolean isPluginLoaded(String id);

    void addPlugin(@NotNull PluginCandidate pluginCandidate) throws PluginResolutionException;

}