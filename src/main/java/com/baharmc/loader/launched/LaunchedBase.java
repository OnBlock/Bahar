package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import net.fabricmc.mappings.Mappings;
import org.apache.logging.log4j.Logger;
import org.cactoos.collection.CollectionOf;
import org.cactoos.collection.Filtered;
import org.cactoos.collection.Mapped;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class LaunchedBase implements BaharLaunched {

    @NotNull
    private final Map<String, Object> properties = new HashMap<>();

    @NotNull
    private final Logger logger;

    @NotNull
    private final File serverJarFile;

    @NotNull
    private final MappingConfiguration mappingConfiguration;

    private boolean mixinReady = false;

    private Path minecraftJar = null;

    private boolean emittedInfo = false;

    static BaharLaunched INSTANCE;

    public LaunchedBase(@NotNull Logger logger, @NotNull File serverJarFile) {
        INSTANCE = this;
        this.logger = logger;
        this.serverJarFile = serverJarFile;
        this.mappingConfiguration = new MappingConfiguration(this);
    }

    @Override
    public void deobfuscate(@NotNull String gameId, @NotNull String gameVersion, @NotNull Path jarFile) {
        Path resultJarFile = jarFile;

        logger.debug("Requesting deobfuscation of " + jarFile.getFileName());

        final Mappings mappings = mappingConfiguration.getMappings();
        final String targetNameSpace = MappingConfiguration.TARGET_NAMESPACE;

        if (mappings.getNamespaces().contains(targetNameSpace)) {
            return;
        }

        try {
            propose(UrlUtil.asUrl(jarFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (minecraftJar == null) {
            minecraftJar = resultJarFile;
        }
    }

    @Override
    public void doneMixinBootstrapping() {
        if (mixinReady) {
            throw new RuntimeException("Must not call LaunchedBase#finishMixinBootstrapping() twice!");
        }

        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mixinReady = true;
    }

    @NotNull
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @NotNull
    @Override
    public MappingConfiguration getMappingConfiguration() {
        return mappingConfiguration;
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isMixinReady() {
        return mixinReady;
    }

    @NotNull
    @Override
    public String getTargetNamespace() {
        return "intermediary";
    }


    @NotNull
    @Override
    public Collection<URL> getLoadTimeDependencies() {
        String cmdLineClasspath = System.getProperty("java.class.path");

        return new CollectionOf<>(
            new Filtered<>(
                Objects::nonNull,
                new Mapped<>(
                    ss -> {
                        File file = new File(ss);
                        if (!file.equals(serverJarFile)) {
                            try {
                                return (UrlUtil.asUrl(file));
                            } catch (UrlConversionException e) {
                                logger.debug(e);
                                return null;
                            }
                        } else {
                            return null;
                        }
                    },
                    new Filtered<>(
                        s -> {
                            if (s.equals("*") || s.endsWith(File.separator + "*")) {
                                System.err.println("WARNING: Knot does not support wildcard classpath entries: " +
                                    s +
                                    " - the game may not load properly!");
                                return false;
                            } else {
                                return true;
                            }
                        },
                        cmdLineClasspath.split(File.pathSeparator)
                    )
                )
            )
        );
    }

}
