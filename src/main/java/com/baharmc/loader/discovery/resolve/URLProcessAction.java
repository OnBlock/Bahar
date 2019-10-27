package com.baharmc.loader.discovery.resolve;

import com.baharmc.loader.discovery.PluginCandidateSet;
import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.discovery.ModCandidate;
import net.fabricmc.loader.discovery.ModCandidateSet;
import net.fabricmc.loader.discovery.ModResolver;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.loader.metadata.LoaderModMetadata;
import net.fabricmc.loader.metadata.ModMetadataParser;
import net.fabricmc.loader.metadata.NestedJarEntry;
import net.fabricmc.loader.util.FileSystemUtil;
import net.fabricmc.loader.util.UrlConversionException;
import net.fabricmc.loader.util.UrlUtil;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public final class URLProcessAction extends RecursiveAction {

    @NotNull
    private final Logger logger;

    @NotNull
    private final Map<String, PluginCandidateSet> candidateById;

    @NotNull
    private final URL url;

    private final int depth;

    public URLProcessAction(@NotNull Logger logger, @NotNull Map<String, PluginCandidateSet> candidateById, @NotNull URL url, int depth) {
        this.logger = logger;
        this.candidateById = candidateById;
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

        if (Files.isDirectory(path)) {
            modJson = path.resolve("bahar.plugin.yml");
            rootDir = path;
        } else {
            try {
                jarFs = FileSystemUtil.getJarFileSystem(path, false);
                modJson = jarFs.get().getPath("bahar.plugin.yml");
                rootDir = jarFs.get().getRootDirectories().iterator().next();
            } catch (IOException e) {
                throw new RuntimeException("Failed to open mod JAR at " + path + "!");
            }
        }

        LoadedPluginMetaData[] info;

        try (InputStream stream = Files.newInputStream(modJson)) {
            info = ModMetadataParser.getMods(loader, stream);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Mod at '" + path + "' has an invalid fabric.mod.json file!", e);
        } catch (NoSuchFileException e) {
            info = new LoaderModMetadata[0];
        } catch (IOException e) {
            throw new RuntimeException("Failed to open fabric.mod.json for mod at '" + path + "'!", e);
        }

        for (LoaderModMetadata i : info) {
            ModCandidate candidate = new ModCandidate(i, normalizedUrl, depth);
            boolean added;

            if (candidate.getInfo().getId() == null || candidate.getInfo().getId().isEmpty()) {
                throw new RuntimeException(String.format("Mod file `%s` has no id", candidate.getOriginUrl().getFile()));
            }

            if (!MOD_ID_PATTERN.matcher(candidate.getInfo().getId()).matches()) {
                List<String> errorList = new ArrayList<>();
                isModIdValid(candidate.getInfo().getId(), errorList);
                StringBuilder fullError = new StringBuilder("Mod id `");
                fullError.append(candidate.getInfo().getId()).append("` does not match the requirements because");

                if (errorList.size() == 1) {
                    fullError.append(" it ").append(errorList.get(0));
                } else {
                    fullError.append(":");
                    for (String error : errorList) {
                        fullError.append("\n  - It ").append(error);
                    }
                }

                throw new RuntimeException(fullError.toString());
            }

            added = candidatesById.computeIfAbsent(candidate.getInfo().getId(), ModCandidateSet::new).add(candidate);

            if (!added) {
                loader.getLogger().debug(candidate.getOriginUrl() + " already present as " + candidate);
            } else {
                loader.getLogger().debug("Adding " + candidate.getOriginUrl() + " as " + candidate);

                List<Path> jarInJars = inMemoryCache.computeIfAbsent(candidate.getOriginUrl(), (u) -> {
                    loader.getLogger().debug("Searching for nested JARs in " + candidate);
                    Collection<NestedJarEntry> jars = candidate.getInfo().getJars();
                    List<Path> list = new ArrayList<>(jars.size());

                    jars.stream()
                        .map((j) -> rootDir.resolve(j.getFile().replace("/", rootDir.getFileSystem().getSeparator())))
                        .forEach((modPath) -> {
                            if (!Files.isDirectory(modPath) && modPath.toString().endsWith(".jar")) {
                                // TODO: pre-check the JAR before loading it, if possible
                                loader.getLogger().debug("Found nested JAR: " + modPath);
                                Path dest = inMemoryFs.getPath(UUID.randomUUID() + ".jar");

                                try {
                                    Files.copy(modPath, dest);
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to load nested JAR " + modPath + " into memory (" + dest + ")!", e);
                                }

                                list.add(dest);
                            }
                        });

                    return list;
                });

                if (!jarInJars.isEmpty()) {
                    invokeAll(
                        jarInJars.stream()
                            .map((p) -> {
                                try {
                                    return new ModResolver.UrlProcessAction(loader, candidatesById, UrlUtil.asUrl(p.normalize()), depth + 1);
                                } catch (UrlConversionException e) {
                                    throw new RuntimeException("Failed to turn path '" + p.normalize() + "' into URL!", e);
                                }
                            }).collect(Collectors.toList())
                    );
                }
            }
        }
    }

}
