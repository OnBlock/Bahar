package com.baharmc.loader.plugin;

import com.baharmc.loader.utils.semanticversion.Version;
import org.jetbrains.annotations.NotNull;

public interface PluginMetaData {

    @NotNull
    String getId();

    @NotNull
    Version getVersion();

}
