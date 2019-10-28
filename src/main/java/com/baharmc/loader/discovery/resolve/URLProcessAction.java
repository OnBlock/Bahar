package com.baharmc.loader.discovery.resolve;

import com.baharmc.loader.discovery.PluginCandidate;
import com.baharmc.loader.discovery.PluginCandidateSet;
import com.baharmc.loader.discovery.PluginResolve;
import com.baharmc.loader.metadata.PluginMetaDataParsed;
import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.utils.FileSystemUtil;
import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import org.apache.logging.log4j.Logger;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.ListOf;
import org.cactoos.list.Mapped;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RecursiveAction;

public final class URLProcessAction extends RecursiveAction {

    @NotNull
    private final transient Logger logger;

    @NotNull
    private final Map<String, PluginCandidateSet> candidatesById;

    @NotNull
    private final URL url;

    private final int depth;

    public URLProcessAction(@NotNull Logger logger, @NotNull Map<String, PluginCandidateSet> candidateById, @NotNull URL url, int depth) {
        this.logger = logger;
        this.candidatesById = candidateById;
        this.url = url;
        this.depth = depth;
    }

    @Override
    protected void compute() {
        final FileSystemUtil.FileSystemDelegate jarFs;
        final Path path, modJson, rootDir;
        final URL normalizedUrl;

        logger.debug("Testing " + url);

        try {
            path = UrlUtil.asPath(url).normalize();
            normalizedUrl = UrlUtil.asUrl(path);
        } catch (UrlConversionException e) {
            throw new RuntimeException("Failed to convert URL " + url + "!", e);
        }

        if (path.toFile().isDirectory()) {
            modJson = path.resolve("bahar.plugin.yml");
            rootDir = path;
        } else {
            try {
                jarFs = FileSystemUtil.getJarFileSystem(path, false);
                modJson = jarFs.get().getPath("bahar.plugin.yml");
                rootDir = jarFs.get().getRootDirectories().iterator().next();
            } catch (IOException e) {
                throw new RuntimeException("Failed to open plugin JAR at " + path + "!");
            }
        }

        LoadedPluginMetaData[] info;

        try (InputStream stream = Files.newInputStream(modJson)) {
            info = new PluginMetaDataParsed(stream).value();
        } catch (NoSuchFileException e) {
            info = new LoadedPluginMetaData[0];
        } catch (IOException e) {
            throw new RuntimeException("Failed to open bahar.plugin.yml for plugin at '" + path + "'!", e);
        }

        for (LoadedPluginMetaData i : info) {
            final PluginCandidate candidate = new PluginCandidate(i, normalizedUrl, depth);

            if (candidate.getInfo().getId().isEmpty()) {
                throw new RuntimeException(String.format("Plugin file `%s` has no id", candidate.getUrl().getFile()));
            }

            if (!PluginResolve.PLUGIN_ID_PATTERN.matcher(candidate.getInfo().getId()).matches()) {
                final List<String> errors = new IsPluginIdValid(candidate.getInfo().getId()).value();
                final StringBuilder fullError = new StringBuilder("Plugin id `");

                fullError.append(candidate.getInfo().getId()).append("` does not match the requirements because");

                if (errors.size() == 1) {
                    fullError.append(" it ").append(errors.get(0));
                } else {
                    fullError.append(":");
                    for (String error : errors) {
                        fullError.append("\n  - It ").append(error);
                    }
                }

                throw new RuntimeException(fullError.toString());
            }

            final boolean added = candidatesById.computeIfAbsent(
                candidate.getInfo().getId(),
                PluginCandidateSet::new
            ).add(candidate);

            if (!added) {
                logger.debug(candidate.getUrl() + " already present as " + candidate);
                continue;
            }

            logger.debug("Adding " + candidate.getUrl() + " as " + candidate);

            final URI uri;
            try {
                uri = candidate.getUrl().toURI();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            logger.debug("Searching for nested JARs in " + candidate);

            final List<Path> jarInJars = PluginResolve.IN_MEMORY_CACHE.computeIfAbsent(uri, u -> new ListOf<>(
                new Mapped<>(
                    filtered -> {
                        logger.debug("Found nested JAR: " + filtered);

                        final Path dest = PluginResolve.IN_MEMORY_FS.getPath(UUID.randomUUID() + ".jar");

                        try {
                            Files.copy(filtered, dest);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to load nested JAR " + filtered + " into memory (" + dest + ")!", e);
                        }

                        return dest;
                    },
                    new Filtered<>(
                        pluginPath -> !Files.isDirectory(pluginPath) && pluginPath.toString().endsWith(".jar"),
                        new Mapped<>(
                            jar -> rootDir.resolve(
                                jar.getFile().replace("/", rootDir.getFileSystem().getSeparator())
                            ),
                            candidate.getInfo().getJars()
                        )
                    )
                )
            ));

            if (!jarInJars.isEmpty()) {
                invokeAll(
                    new Mapped<>(
                        jar -> {
                            try {
                                return new URLProcessAction(logger, candidatesById, UrlUtil.asUrl(jar.normalize()), depth + 1);
                            } catch (UrlConversionException e) {
                                throw new RuntimeException("Failed to turn path '" + jar.normalize() + "' into URL!", e);
                            }
                        },
                        jarInJars
                    )
                );
            }
        }
    }

}
