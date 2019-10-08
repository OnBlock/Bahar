package com.baharmc.loader.contained;

import com.baharmc.loader.PluginContained;
import com.baharmc.loader.PluginMetaData;
import com.baharmc.loader.metadata.PluginMetaDataBasic;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PluginContainerBasic implements PluginContained {

    @NotNull
    private final PluginMetaData pluginMetaData;

    public PluginContainerBasic(@NotNull PluginMetaData pluginMetaData) {
        this.pluginMetaData = pluginMetaData;
    }

    @Override
    public PluginMetaDataBasic getMetaData() {
        return null;
    }

    @Override
    public Path getRootPath() {
        return null;
    }

}
