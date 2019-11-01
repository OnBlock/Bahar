package com.baharmc.loader.plugin;

import com.baharmc.loader.plugin.metadata.NestedJarEntry;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface LoadedPluginMetaData extends PluginMetaData {

    @NotNull
    Collection<String> getMixinConfigs();

    @NotNull
    Collection<NestedJarEntry> getJars();

    void emitFormatWarnings(@NotNull Logger logger);

}
