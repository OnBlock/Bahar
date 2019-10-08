package com.baharmc.loader.contained;

import com.baharmc.loader.PluginContained;
import com.baharmc.loader.metadata.PluginMetaDataBasic;

import java.nio.file.Path;

public class PluginContainerBasic implements PluginContained {

    @Override
    public PluginMetaDataBasic getMetaData() {
        return null;
    }

    @Override
    public Path getRootPath() {
        return null;
    }

}
