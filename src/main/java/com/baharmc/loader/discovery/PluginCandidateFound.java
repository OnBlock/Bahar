package com.baharmc.loader.discovery;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.function.Consumer;

public interface PluginCandidateFound {

    void findCandidates(@NotNull Consumer<URL> consumer);

}
