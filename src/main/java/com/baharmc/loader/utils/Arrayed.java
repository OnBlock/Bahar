package com.baharmc.loader.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Arrayed {

    @NotNull
    private final Map<String, String> values;

    @NotNull
    private final List<String> extraArgs;

    public Arrayed(@NotNull Map<String, String> values, @NotNull List<String> extraArgs) {
        this.values = values;
        this.extraArgs = extraArgs;
    }

    @NotNull
    public String[] value() {
        final String[] newArgs = new String[values.size() * 2 + extraArgs.size()];
        final AtomicInteger integer = new AtomicInteger();

        values.keySet().forEach(s -> {
            newArgs[integer.getAndIncrement()] = "--" + s;
            newArgs[integer.getAndIncrement()] = values.get(s);
        });

        extraArgs.forEach(s -> newArgs[integer.getAndIncrement()] = s);

        return newArgs;
    }

}
