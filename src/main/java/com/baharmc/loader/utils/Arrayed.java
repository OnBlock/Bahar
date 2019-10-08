package com.baharmc.loader.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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

        int i = 0;
        for (String s : values.keySet()) {
            newArgs[i++] = "--" + s;
            newArgs[i++] = values.get(s);
        }

        for (String s : extraArgs) {
            newArgs[i++] = s;
        }

        return newArgs;
    }

}
