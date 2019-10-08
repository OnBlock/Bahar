package com.baharmc.loader.contained;

import com.baharmc.loader.PluginContained;
import com.baharmc.loader.metadata.PluginMetaData;

import java.nio.file.Path;

public class PluginContainerBasic implements PluginContained {

    @Override
    public PluginMetaData getMetaData() {
        return null;
    }

    @Override
    public Path getRootPath() {
        return null;
    }

}
