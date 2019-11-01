package com.baharmc.loader.plugin.metadata;

import com.baharmc.loader.utils.semanticversion.Version;
import org.jetbrains.annotations.NotNull;

public final class DependencyBasic implements Dependency {

    @NotNull
    private final String pluginId;

    @NotNull
    private final String versionString;

    public DependencyBasic(@NotNull String pluginId, @NotNull String versionString) {
        this.pluginId = pluginId;
        this.versionString = versionString;
    }

    @NotNull
    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public boolean matches(@NotNull Version version) {
        return false;
    }
}
