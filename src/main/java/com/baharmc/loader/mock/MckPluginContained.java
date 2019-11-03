package com.baharmc.loader.mock;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;

public class MckPluginContained implements PluginContained {
    @NotNull
    @Override
    public LoadedPluginMetaData getMetadata() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Path getRootPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void instantiate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull URL getOriginURL() {
        throw new UnsupportedOperationException();
    }
}
