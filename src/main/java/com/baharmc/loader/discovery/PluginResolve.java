package com.baharmc.loader.discovery;

import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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


        return new MapOf<>();
    }

}
