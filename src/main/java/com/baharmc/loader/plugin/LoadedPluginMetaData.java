package com.baharmc.loader.plugin;

import com.baharmc.loader.metadata.EntryPointMetaData;
import com.baharmc.loader.plugin.metadata.Dependency;
import com.baharmc.loader.plugin.metadata.NestedJarEntry;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface LoadedPluginMetaData extends PluginMetaData {

    @NotNull
    Collection<String> getMixinConfigs();

    @NotNull
    Collection<NestedJarEntry> getJars();

    @NotNull
    List<Dependency> getDependencies();

    @NotNull
    List<EntryPointMetaData> getEntryPoints(String type);

    void emitFormatWarnings(@NotNull Logger logger);

}
