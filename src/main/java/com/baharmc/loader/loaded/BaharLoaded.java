package com.baharmc.loader.loaded;

import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface BaharLoaded {

    static BaharLoaded getInstance() {
        if (BaharLoaderBasic.INSTANCE == null) {
            throw new RuntimeException("Accessed FabricLoader too early!");
        }

        return BaharLoaderBasic.INSTANCE;
    }

    void load();

    void freeze();

    @NotNull
    <T> List<T> getEntryPoints(@NotNull String key, @NotNull Class<T> type);

    @NotNull
    MappingResolved getMappingResolver();

    @NotNull
    PluginContained getPluginContained(String id);

    @NotNull
    Collection<PluginContained> getAllPlugins();

    boolean isModLoaded(String id);

    @NotNull
    Object getGameInstance();

    @NotNull
    File getGameDirectory();

    @NotNull
    File getConfigDirectory();

}
