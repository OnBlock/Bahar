package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public final class PluginMetaDataParsed implements Scalar<LoadedPluginMetaData[]> {

    @NotNull
    private final InputStream stream;

    public PluginMetaDataParsed(@NotNull InputStream stream) {
        this.stream = stream;
    }

    @Override
    public LoadedPluginMetaData[] value() {
        final Yaml yaml = new Yaml(
            new Constructor(
                PluginMetaDataBasic.class
            )
        );
        return new LoadedPluginMetaData[0];
    }

}
