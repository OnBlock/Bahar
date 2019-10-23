package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public interface BaharLaunched {

    static BaharLaunched getInstance() {
        if (LaunchedBase.INSTANCE == null) {
            throw new RuntimeException("Accessed BaharLaunched too early!");
        }

        return LaunchedBase.INSTANCE;
    }

    void deobfuscate(@NotNull String gameId, @NotNull String gameVersion, @NotNull Path jarFile);

    void doneMixinBootstrapping();

    void propose(@NotNull URL url);

    void start();

    @NotNull
    MappingConfiguration getMappingConfiguration();

    @NotNull
    Logger getLogger();

    @NotNull
    Map<String, Object> getProperties();

    boolean isMixinReady();

    @NotNull
    String getTargetNamespace();

    @NotNull
    ClassLoader getTargetClassLoader();

    @NotNull
    Collection<URL> getLoadTimeDependencies();

    @NotNull
    String getEntryPoint();

    @NotNull
    byte[] getClassByteArray(String name) throws IOException;

}
