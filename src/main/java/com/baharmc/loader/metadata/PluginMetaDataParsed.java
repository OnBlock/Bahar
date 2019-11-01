package com.baharmc.loader.metadata;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public final class PluginMetaDataParsed implements Scalar<LoadedPluginMetaData[]> {

    @NotNull
    private final InputStream stream;

    public PluginMetaDataParsed(@NotNull InputStream stream) {
        this.stream = stream;
    }

    @Override
    public LoadedPluginMetaData[] value() {
        final Yaml yaml = new Yaml();
        final Map<String, Object> parsed = yaml.load(stream);

        final String id = parsed.getOrDefault("id", "");
        
        if (id.isEmpty()) {
            throw new RuntimeException("Plugin id must be specified!");
        }


        return new LoadedPluginMetaData[0];
    }

}
