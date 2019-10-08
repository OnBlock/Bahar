package com.baharmc.loader.mock;

import com.baharmc.loader.PluginContained;
import com.baharmc.loader.metadata.PluginMetaData;

import java.nio.file.Path;

public class MckPluginContained implements PluginContained {
    @Override
    public PluginMetaData getMetaData() {
        return new MckPluginMetaData();
    }

    @Override
    public Path getRootPath() {
        return Path.of("");
    }
}
