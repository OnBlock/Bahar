package com.baharmc.loader.plugin.metadata;

import com.baharmc.loader.utils.semanticversion.Version;
import org.jetbrains.annotations.NotNull;

public interface Dependency {

    @NotNull
    String getPluginId();

    boolean matches(@NotNull Version version);

}
