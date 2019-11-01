package com.baharmc.loader.discovery;

import com.baharmc.loader.discovery.resolve.IsPluginIdValid;
import com.baharmc.loader.discovery.resolve.URLProcessAction;
import com.baharmc.loader.plugin.metadata.Dependency;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.utils.semanticversion.Version;
import com.google.common.base.Joiner;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.PathType;
import net.fabricmc.loader.util.sat4j.core.VecInt;
import net.fabricmc.loader.util.sat4j.minisat.SolverFactory;
import net.fabricmc.loader.util.sat4j.specs.TimeoutException;
import net.fabricmc.loader.util.sat4j.specs.*;
import org.apache.logging.log4j.Logger;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @NotNull
    public Map<String, PluginCandidate> findCompatibleSet(@NotNull Map<String, PluginCandidateSet> pluginCandidateSetMap)
        throws PluginResolutionException {

        boolean isAdvanced = false;
        final Map<String, Collection<PluginCandidate>> pluginCandidateMap = new HashMap<>();
        final Set<String> mandatoryPlugins = new HashSet<>();

        for (PluginCandidateSet pcs : pluginCandidateSetMap.values()) {
            final Collection<PluginCandidate> s = pcs.toSortedSet();

            pluginCandidateMap.put(pcs.getPluginId(), s);

            isAdvanced |= (s.size() > 1) || (s.iterator().next().getDepth() > 0);

            if (pcs.isUserProvided()) {
                mandatoryPlugins.add(pcs.getPluginId());
            }
        }

        final Map<String, PluginCandidate> result;

        if (!isAdvanced) {
            result = new HashMap<>();
            for (String s : pluginCandidateMap.keySet()) {
                result.put(s, pluginCandidateMap.get(s).iterator().next());
            }
        } else {
            int varCount = 1;
            final Map<PluginCandidate, Integer> candidateIntMap = new HashMap<>();
            final List<PluginCandidate> intCandidateMap = new ArrayList<>(pluginCandidateMap.size() * 2);

            intCandidateMap.add(null);

            for (Collection<PluginCandidate> m : pluginCandidateMap.values()) {
                for (PluginCandidate candidate : m) {
                    candidateIntMap.put(candidate, varCount++);
                    intCandidateMap.add(candidate);
                }
            }

            ISolver solver = SolverFactory.newLight();
            solver.newVar(varCount);

            try {
                for (Map.Entry<String, Collection<PluginCandidate>> entry : pluginCandidateMap.entrySet()) {
                    final IVecInt versionVec = new VecInt(
                        entry.getValue().stream().mapToInt(candidateIntMap::get).toArray()
                    );

                    try {
                        if (mandatoryPlugins.contains(entry.getKey())) {
                            solver.addExactly(versionVec, 1);
                        } else {
                            solver.addAtMost(versionVec, 1);
                        }
                    } catch (ContradictionException e) {
                        throw new PluginResolutionException(
                            "Could not resolve valid plugin collection (at: adding plugin " + entry.getKey() + ")",
                            e
                        );
                    }
                }

                for (Map.Entry<PluginCandidate, Integer> entry : candidateIntMap.entrySet()) {
                    final int pluginClauseId = entry.getValue();

                    for (Dependency dep : entry.getKey().getInfo().getDependencies()) {
                        final int[] matchingCandidates = pluginCandidateMap.getOrDefault(
                            dep.getPluginId(),
                            Collections.emptyList()
                        ).stream()
                            .filter(c -> dep.matches(c.getInfo().getVersion()))
                            .mapToInt(candidateIntMap::get)
                            .toArray();
                        final int[] clause = new int[matchingCandidates.length + 1];

                        System.arraycopy(matchingCandidates, 0, clause, 0, matchingCandidates.length);
                        clause[matchingCandidates.length] = -pluginClauseId;

                        try {
                            solver.addClause(new VecInt(clause));
                        } catch (ContradictionException e) {
                            throw new PluginResolutionException(
                                "Could not find required plugin: " +
                                    entry.getKey().getInfo().getId() + " requires " + dep,
                                e
                            );
                        }
                    }

                }

                //noinspection UnnecessaryLocalVariable
                final IProblem problem = solver;
                IVecInt assumptions = new VecInt(pluginCandidateMap.size());

                for (Map.Entry<String, Collection<PluginCandidate>> entry : pluginCandidateMap.entrySet()) {
                    int pos = assumptions.size();
                    assumptions = assumptions.push(0);
                    boolean satisfied = false;

                    for (PluginCandidate candidate : entry.getValue()) {
                        assumptions.set(pos, candidateIntMap.get(candidate));
                        if (problem.isSatisfiable(assumptions)) {
                            satisfied = true;
                            break;
                        }
                    }

                    if (!satisfied) {
                        if (mandatoryPlugins.contains(entry.getKey())) {
                            throw new PluginResolutionException(
                                "Could not resolve plugin collection including mandatory plugin '" +
                                    entry.getKey() + "'"
                            );
                        } else {
                            assumptions = assumptions.pop();
                        }
                    }
                }

                // assume satisfied
                final int[] model = problem.model();
                result = new HashMap<>();

                for (int i : model) {
                    if (i <= 0) {
                        continue;
                    }

                    final PluginCandidate candidate = intCandidateMap.get(i);

                    if (result.containsKey(candidate.getInfo().getId())) {
                        throw new PluginResolutionException(
                            "Duplicate ID '" + candidate.getInfo().getId() +
                            "' after solving - wrong constraints?"
                        );
                    } else {
                        result.put(candidate.getInfo().getId(), candidate);
                    }
                }
            } catch (TimeoutException e) {
                throw new PluginResolutionException("Plugin collection took too long to be resolved", e);
            }
        }

        final Set<String> missingPlugins = new HashSet<>();

        for (String m : mandatoryPlugins) {
            if (!result.containsKey(m)) {
                missingPlugins.add(m);
            }
        }

        final StringBuilder errorsHard = new StringBuilder();
        final StringBuilder errorsSoft = new StringBuilder();

        if (!missingPlugins.isEmpty()) {
            errorsHard.append("\n - Missing plugins: ").append(Joiner.on(", ").join(missingPlugins));
        } else {
            for (PluginCandidate candidate : result.values()) {
                for (Dependency dependency : candidate.getInfo().getDependencies()) {
                    addErrorToList(candidate, dependency, result, errorsHard, "depends on", true);
                }

                final Version version = candidate.getInfo().getVersion();
                final List<Version> suspiciousVersions = new ArrayList<>();

                for (PluginCandidate other : pluginCandidateMap.get(candidate.getInfo().getId())) {
                    Version otherVersion = other.getInfo().getVersion();
                    if (version instanceof Comparable && otherVersion instanceof Comparable && !version.equals(otherVersion)) {
                        //noinspection unchecked
                        if (((Comparable) version).compareTo(otherVersion) == 0) {
                            suspiciousVersions.add(otherVersion);
                        }
                    }
                }

                if (!suspiciousVersions.isEmpty()) {
                    errorsSoft.append("\n - Conflicting versions found for ")
                        .append(candidate.getInfo().getId())
                        .append(": used ")
                        .append(version.getFriendlyString())
                        .append(", also found ")
                        .append(suspiciousVersions
                            .stream()
                            .map(Version::getFriendlyString)
                            .collect(Collectors.joining(", "))
                        );
                }
            }
        }

        final String errHardStr = errorsHard.toString();
        final String errSoftStr = errorsSoft.toString();

        if (!errSoftStr.isEmpty()) {
            logger.warn("Warnings were found! " + errSoftStr);
        }

        if (!errHardStr.isEmpty()) {
            throw new PluginResolutionException("Errors were found!" + errHardStr + errSoftStr);
        }

        return result;
    }

    private void addErrorToList(@NotNull PluginCandidate candidate, @NotNull Dependency dependency,
                                @NotNull Map<String, PluginCandidate> result, @NotNull StringBuilder errors,
                                @NotNull String errorType, boolean cond) {
        final String dependencyPluginId = dependency.getPluginId();
        final StringBuilder prefix = new StringBuilder("\n - Plugin ").append(candidate.getInfo().getId());

        prefix.append(" ").append(errorType).append(" plugin ").append(dependencyPluginId);

        final List<String> errorList = new IsPluginIdValid(dependencyPluginId).value();

        if (!errorList.isEmpty()) {
            if (errorList.size() == 1) {
                errors.append(prefix).append(" which has an invalid plugin id because it ").append(errorList.get(0));
            } else {
                errors.append(prefix).append(" which has an invalid plugin because:");

                for (String error : errorList) {
                    errors.append("\n   - It ").append(error);
                }
            }
        }

        final PluginCandidate depCandidate = result.get(dependencyPluginId);
        final boolean isPresent = depCandidate != null && dependency.matches(depCandidate.getInfo().getVersion());

        if (isPresent != cond) {
            errors
                .append("\n - Plugin ")
                .append(candidate.getInfo().getId())
                .append(" ")
                .append(errorType)
                .append(" plugin ")
                .append(dependency)
                .append(", ");

            if (depCandidate == null) {
                errors.append("which is missing");
            } else if (cond) {
                errors.append("but a different version is present: ").append(depCandidate.getInfo().getVersion());
            } else if (errorType.contains("conf")) {
                errors.append("but the conflicting version is present: ").append(depCandidate.getInfo().getVersion());
            } else {
                errors.append("but the breaking version is present: ").append(depCandidate.getInfo().getVersion());
            }

            errors.append("!");
        }
    }

}
