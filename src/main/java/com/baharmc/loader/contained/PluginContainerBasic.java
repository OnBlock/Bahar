package com.baharmc.loader.contained;

import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.plugin.PluginMetaData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public class PluginContainerBasic implements PluginContained {

    @NotNull
    private final PluginMetaData pluginMetaData;

    @NotNull
    private final File file;

    public PluginContainerBasic(@NotNull PluginMetaData pluginMetaData, @NotNull File file) {
        this.pluginMetaData = pluginMetaData;
        this.file = file;
    }

    @Override
    public PluginMetaData getMetaData() {
        return pluginMetaData;
    }

    @Override
    public Path getRootPath() {
        return file.toPath();
    }

}
