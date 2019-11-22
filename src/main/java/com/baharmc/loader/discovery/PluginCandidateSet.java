package com.baharmc.loader.discovery;

import com.baharmc.loader.utils.semanticversion.Version;
import org.cactoos.list.Mapped;
import org.cactoos.list.Sorted;
import org.cactoos.set.SetOf;
import org.cactoos.text.Joined;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PluginCandidateSet {

    @NotNull
    private final String pluginId;

    @NotNull
    private final Set<PluginCandidate> depthZeroCandidates = new HashSet<>();

    @NotNull
    private final Map<String, PluginCandidate> candidates = new HashMap<>();

    public PluginCandidateSet(@NotNull String pluginId) {
        this.pluginId = pluginId;
    }

    @NotNull
    public String getPluginId() {
        return pluginId;
    }

    public boolean add(PluginCandidate candidate) {
        final String version = candidate.getInfo().getVersion().getFriendlyString();
        final PluginCandidate oldCandidate = candidates.get(version);

        if (oldCandidate != null) {
            final int oldDepth = oldCandidate.getDepth();
            final int newDepth = candidate.getDepth();

            if (oldDepth <= newDepth) {
                return false;
            }

            candidates.remove(version);

            if (oldDepth > 0) {
                depthZeroCandidates.remove(oldCandidate);
            }
        }

        candidates.put(version, candidate);

        if (candidate.getDepth() == 0) {
            depthZeroCandidates.add(candidate);
        }

        return true;
    }


    public Collection<PluginCandidate> toSortedSet() throws PluginResolutionException {
        if (depthZeroCandidates.size() > 1) {
            throw new PluginResolutionException(
                "Duplicate versions for plugin ID '" + pluginId + "': " + new Joined(
                    ", ",
                    new SetOf<>(
                        new Mapped<>(
                            c -> "[" + c.getInfo().getVersion() + " at " + c.getUrl().getFile() + "]",
                            depthZeroCandidates
                        )
                    )
                ).toString()
            );
        } else if (depthZeroCandidates.size() == 1) {
            return depthZeroCandidates;
        } else if (candidates.size() > 1) {
            return new Sorted<>(
                (a, b) -> {
                    final Version av = a.getInfo().getVersion();
                    final Version bv = b.getInfo().getVersion();

                    if (av instanceof Comparable && bv instanceof Comparable) {
                        //noinspection unchecked
                        return ((Comparable) bv).compareTo(av);
                    } else {
                        return 0;
                    }
                },
                candidates.values()
            );
        }

        return Collections.singleton(candidates.values().iterator().next());
    }

    public boolean isUserProvided() {
        return !depthZeroCandidates.isEmpty();
    }

}
