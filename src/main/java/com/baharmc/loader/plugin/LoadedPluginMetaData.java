package com.baharmc.loader.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface LoadedPluginMetaData extends PluginMetaData {

    @NotNull
    Collection<String> getMixinConfigs();

}
