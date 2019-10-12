package com.baharmc.loader.loaded;

import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

public interface BaharLoaded {

    void load();

    void freeze();

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
