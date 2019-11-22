package com.baharmc.loader.plugin;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;

public interface PluginContained {

    void instantiate();

    @NotNull
    LoadedPluginMetaData getMetadata();

    @NotNull
    Path getRootPath();

    @NotNull
    URL getOriginURL();

}
