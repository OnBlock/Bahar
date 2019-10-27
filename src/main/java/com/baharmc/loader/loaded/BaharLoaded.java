package com.baharmc.loader.loaded;

import com.baharmc.loader.plugin.PluginContained;
import org.apache.logging.log4j.Logger;
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

    void loadPlugins();

    void enablePlugins();

    void disablePlugins();

    @NotNull
    <T> List<T> getEntryPoints(@NotNull String key, @NotNull Class<T> type);

    @NotNull
    MappingResolved getMappingResolver();

    @NotNull
    PluginContained getPluginContained(String id);

    @NotNull
    Collection<PluginContained> getAllPlugins();

    boolean isPluginLoaded(String id);

}
