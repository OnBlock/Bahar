package com.baharmc.loader.discovery;

import com.baharmc.loader.discovery.resolve.URLProcessAction;
import com.baharmc.loader.provided.GameProvided;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;
import org.apache.logging.log4j.Logger;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static com.google.common.jimfs.Feature.FILE_CHANNEL;
import static com.google.common.jimfs.Feature.SECURE_DIRECTORY_STREAM;

public final class PluginResolve {

    public static final FileSystem IN_MEMORY_FS = Jimfs.newFileSystem(
        "nestedJarStore",
        Configuration.builder(PathType.unix())
            .setRoots("/")
            .setWorkingDirectory("/")
            .setAttributeViews("basic")
            .setSupportedFeatures(SECURE_DIRECTORY_STREAM, FILE_CHANNEL)
            .build()
    );

    public static final Pattern PLUGIN_ID_PATTERN = Pattern.compile("[a-z][a-z0-9-_]{1,63}");

    public static final Map<URI, List<Path>> IN_MEMORY_CACHE = new ConcurrentHashMap<>();

    @NotNull
    private final Logger logger;

    @NotNull
    private final GameProvided gameProvided;

    @NotNull
    private final List<PluginCandidateFound> candidateFounds;

    public PluginResolve(@NotNull Logger logger, @NotNull GameProvided gameProvided, @NotNull List<PluginCandidateFound> candidateFounds) {
        this.logger = logger;
        this.gameProvided = gameProvided;
        this.candidateFounds = candidateFounds;
    }

    public PluginResolve(@NotNull Logger logger, @NotNull GameProvided gameProvided, @NotNull PluginCandidateFound candidateFinded) {
        this(
            logger,
            gameProvided,
            new ListOf<>(
                candidateFinded
            )
        );
    }

    @NotNull
    public Map<String, PluginCandidate> resolve() throws PluginResolutionException {
        final ConcurrentMap<String, PluginCandidateSet> candidatesById = new ConcurrentHashMap<>();
        final long first = System.currentTimeMillis();
        final Queue<URLProcessAction> allActions = new ConcurrentLinkedQueue<>();
        final ForkJoinPool pool = new ForkJoinPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

        candidateFounds.forEach(f -> f.findCandidates(u -> {
            final URLProcessAction action = new URLProcessAction(logger, candidatesById, u, 0);

            allActions.add(action);
            pool.execute(action);
        }));

        gameProvided.getBuiltinPlugins().forEach(plugin -> candidatesById.computeIfAbsent(
            plugin.getPluginMetaData().getId(),
            PluginCandidateSet::new
        ).add(
            new PluginCandidate(
                new BuiltinMetaDataWrapped(
                    plugin.getPluginMetaData()
                ),
                plugin.getUrl(),
                0
            )
        ));

        Throwable exception = null;

        try {
            pool.shutdown();
            pool.awaitTermination(30, TimeUnit.SECONDS);

            for (URLProcessAction action : allActions) {
                if (!action.isDone()) {
                    throw new PluginResolutionException("Plugin resolution took too long!");
                }

                final Throwable t = action.getException();

                if (t != null) {
                    if (exception == null) {
                        exception = t;
                        continue;
                    }

                    exception.addSuppressed(t);
                }
            }
        } catch (Exception e) {
            throw new PluginResolutionException("Plugin resolution took too long!", e);
        }

        if (exception != null) {
            throw new PluginResolutionException("Plugin resolution failed!", exception);
        }

        final long second = System.currentTimeMillis();
        final Map<String, PluginCandidate> result = findCompatibleSet(candidatesById);
        final long third = System.currentTimeMillis();

        logger.debug("Plugin resolution detection time: " + (second - first) + "ms");
        logger.debug("Plugin resolution time: " + (third - second) + "ms");

        for (PluginCandidate candidate : result.values()) {
            candidate.getInfo().emitFormatWarnings(logger);
        }

        return result;
    }

    public Map<String, PluginCandidate> findCompatibleSet(@NotNull Map<String, PluginCandidateSet> pluginCandidateSetMap)
        throws PluginResolutionException {

        return new MapOf<>();
    }

}
