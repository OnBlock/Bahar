package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

public interface BaharLaunched {

    void deobfuscate(@NotNull String gameId, @NotNull String gameVersion, @NotNull Path gameDirectory,
                     @NotNull Path jarFile);

    void doneMixinBootstrapping();

    void propose(@NotNull URL url);

    void init();

    @NotNull
    MappingConfiguration getMappingConfiguration();

    @NotNull
    Logger getLogger();

    @NotNull
    Map<String, Object> getProperties();

    boolean isMixinReady();

    @NotNull
    String getTargetNamespace();

}
