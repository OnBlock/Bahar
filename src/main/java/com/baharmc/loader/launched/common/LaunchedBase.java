package com.baharmc.loader.launched.common;

import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import com.baharmc.loader.utils.mappings.TinyRemapperMappingsHelper;
import net.fabricmc.mappings.Mappings;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.apache.logging.log4j.Logger;
import org.cactoos.collection.CollectionOf;
import org.cactoos.collection.Filtered;
import org.cactoos.collection.Mapped;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;

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
    public void deobfuscate(@NotNull GameProvided provided) {
        final Path resultJarFile = provided.getGameContextJars();

        logger.debug("Requesting deobfuscation of " + resultJarFile.getFileName());

        final String targetNameSpace = MappingConfiguration.TARGET_NAMESPACE;
        final Mappings mappings = mappingConfiguration.getMappings();

        if (!mappings.getNamespaces().contains(targetNameSpace)) {
            try {
                propose(UrlUtil.asUrl(resultJarFile));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (minecraftJar == null) {
                minecraftJar = resultJarFile;
            }

            return;
        }

        logger.debug("Bahar mapping file detected, applying...");

        try {
            final Path jarFile = provided.getGameContextJars();

            if (!Files.exists(jarFile)) {
                throw new RuntimeException("Could not locate Minecraft: " + jarFile + " not found");
            }

            final String gameId = provided.getGameId();
            final String gameVersion = provided.getNormalizedGameVersion();
            final Path deobfJarDir;

            if (gameId.isEmpty()) {
                deobfJarDir = serverJarFile.getParentFile().toPath()
                    .resolve("remappedJars");
            } else {
                deobfJarDir = serverJarFile.getParentFile().toPath()
                    .resolve("remappedJars")
                    .resolve(
                        gameVersion.isEmpty()
                            ? gameId
                            : String.format("%s-%s", gameId, gameVersion
                        )
                    );
            }

            if (!deobfJarDir.toFile().exists()) {
                deobfJarDir.toFile().mkdirs();
            }

            final String deobfJarFilename = targetNameSpace + "-" + jarFile.getFileName();
            final Path deobfJarFile = deobfJarDir.resolve(deobfJarFilename);
            final Path deobfJarFileTmp = deobfJarDir.resolve(deobfJarFilename + ".tmp");

            if (deobfJarFileTmp.toFile().exists()) {
                logger.warn(
                    "Incomplete remapped file found! " +
                        "This means that the remapping process failed on the previous launch. " +
                        "If this persists, make sure to let us at Bahar know!"
                );
                Files.deleteIfExists(deobfJarFile);
                Files.deleteIfExists(deobfJarFileTmp);
            }

            if (!deobfJarFile.toFile().exists()) {
                boolean found = false;

                while (!found) {
                    if (!emittedInfo) {
                        logger.info("Bahar is preparing JARs on first launch, this may take a few seconds...");
                        emittedInfo = true;
                    }

                    final Set<Path> depPaths = new HashSet<>();
                    final TinyRemapper remapper = TinyRemapper.newRemapper()
                        .withMappings(TinyRemapperMappingsHelper.create(mappings, "official", targetNameSpace))
                        .rebuildSourceFilenames(true)
                        .build();

                    for (URL url : getLoadTimeDependencies()) {
                        try {
                            final Path path = UrlUtil.asPath(url);

                            if (!Files.exists(path)) {
                                throw new RuntimeException("Path does not exist: " + path);
                            }

                            if (!path.equals(jarFile)) {
                                depPaths.add(path);
                            }
                        } catch (UrlConversionException e) {
                            throw new RuntimeException("Failed to convert '" + url + "' to path!", e);
                        }
                    }

                    try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(deobfJarFileTmp)
                        .assumeArchive(true)
                        .filter(clsName -> !clsName.startsWith("com/google/common/")
                            && !clsName.startsWith("com/google/gson/")
                            && !clsName.startsWith("com/google/thirdparty/")
                            && !clsName.startsWith("org/apache/logging/log4j/"))
                        .build()) {
                        for (Path path : depPaths) {
                            logger.debug("Appending '" + path + "' to remapper classpath");
                            remapper.readClassPath(path);
                        }

                        remapper.readInputs(jarFile);
                        remapper.apply(outputConsumer);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to remap '" + jarFile + "'!", e);
                    } finally {
                        remapper.finish();
                    }

                    depPaths.add(deobfJarFileTmp);

                    for (Path p : depPaths) {
                        try {
                            p.getFileSystem().close();
                            FileSystems.getFileSystem(new URI("jar:" + p.toUri())).close();
                        } catch (Exception ignored) {
                            // ignored
                        }
                    }

                    try (JarFile jar = new JarFile(deobfJarFileTmp.toFile())) {
                        found = jar.stream().anyMatch(e -> e.getName().endsWith(".class"));
                    }

                    if (!found) {
                        logger.error("Generated deobfuscated JAR contains no classes! Trying again...");
                        Files.delete(deobfJarFileTmp);
                    } else {
                        Files.move(deobfJarFileTmp, deobfJarFile);
                    }
                }
            }

            if (!deobfJarFile.toFile().exists()) {
                throw new RuntimeException("Remapped .JAR file does not exist after remapping! Cannot continue!");
            }

            propose(UrlUtil.asUrl(deobfJarFile));

            if (minecraftJar == null) {
                minecraftJar = deobfJarFile;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void doneMixinBootstrapping() {
        if (mixinReady) {
            throw new RuntimeException("Must not call LaunchedBase#finishMixinBootstrapping() twice!");
        }

        try {
            final Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);

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
        final String cmdLineClasspath = System.getProperty("java.class.path");

        return new CollectionOf<>(
            new Filtered<>(
                Objects::nonNull,
                new Mapped<>(
                    ss -> {
                        final File file = new File(ss);

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
