package com.baharmc.loader.mock;

import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.plugin.PluginMetaData;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.Path;

public class MckPluginContained implements PluginContained {
    @NotNull
    @Override
    public PluginMetaData getMetaData() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Path getRootPath() {
        throw new UnsupportedOperationException();
    }
}
