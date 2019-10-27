package com.baharmc.loader.discovery;

import com.baharmc.loader.discovery.resolve.URLProcessAction;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public final class PluginResolve {

    @NotNull
    private final List<PluginCandidateFinded> candidateFindeds;

    public PluginResolve(@NotNull List<PluginCandidateFinded> candidateFindeds) {
        this.candidateFindeds = candidateFindeds;
    }

    public PluginResolve(@NotNull PluginCandidateFinded candidateFinded) {
        this(
            new ListOf<>(
                candidateFinded
            )
        );
    }

    @NotNull
    public Map<String, PluginCandidate> resolve() {
        final ConcurrentMap<String, PluginCandidate> candidatesById = new ConcurrentHashMap<>();
        final long first = System.currentTimeMillis();
        final Queue<URLProcessAction> allActions = new ConcurrentLinkedQueue<>();
        final ForkJoinPool pool = new ForkJoinPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

        return new MapOf<>();
    }

}
