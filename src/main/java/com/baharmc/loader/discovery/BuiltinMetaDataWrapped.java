package com.baharmc.loader.discovery;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginMetaData;
import com.baharmc.loader.plugin.metadata.Dependency;
import com.baharmc.loader.plugin.metadata.NestedJarEntry;
import com.baharmc.loader.utils.semanticversion.Version;
import org.apache.logging.log4j.Logger;
import org.cactoos.collection.CollectionOf;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public final class BuiltinMetaDataWrapped implements LoadedPluginMetaData {

    @NotNull
    private final PluginMetaData pluginMetaData;

    public BuiltinMetaDataWrapped(@NotNull PluginMetaData pluginMetaData) {
        this.pluginMetaData = pluginMetaData;
    }

    @NotNull
    @Override
    public Collection<String> getMixinConfigs() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public Collection<NestedJarEntry> getJars() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public String getId() {
        return pluginMetaData.getId();
    }

    @NotNull
    @Override
    public Version getVersion() {
        return pluginMetaData.getVersion();
    }

    @NotNull
    @Override
    public List<Dependency> getDependencies() {
        return new ListOf<>();
    }

    @Override
    public void emitFormatWarnings(@NotNull Logger logger) {
    }
}
