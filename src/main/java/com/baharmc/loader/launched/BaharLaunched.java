package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import com.baharmc.loader.launched.knot.KnotClassLoaded;
import com.baharmc.loader.provided.GameProvided;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public interface BaharLaunched {

    static BaharLaunched getInstance() {
        if (LaunchedBase.INSTANCE == null) {
            throw new RuntimeException("Accessed BaharLaunched too early!");
        }

        return LaunchedBase.INSTANCE;
    }

    void deobfuscate(@NotNull GameProvided provided);

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
    KnotClassLoaded getKnotClassLoaded();

    @NotNull
    Collection<URL> getLoadTimeDependencies();

    @NotNull
    String getEntryPoint();

    @NotNull
    byte[] getClassByteArray(String name) throws IOException;

}
