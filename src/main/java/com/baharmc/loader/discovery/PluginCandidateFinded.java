package com.baharmc.loader.discovery;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.function.Consumer;

public interface PluginCandidateFinded {

    void findCandidates(@NotNull Consumer<URL> consumer);

}
