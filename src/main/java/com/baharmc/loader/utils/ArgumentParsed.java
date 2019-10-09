package com.baharmc.loader.utils;

import org.cactoos.Scalar;
import org.cactoos.map.MapEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentParsed implements Scalar<Map.Entry<Map<String, String>, List<String>>> {

    @NotNull
    private final List<String> arguments;

    public ArgumentParsed(@NotNull List<String> arguments) {
        this.arguments = arguments;
    }

    @NotNull
    public Map.Entry<Map<String, String>, List<String>> value() {
        final Map<String, String> values = new HashMap<>();
        final List<String> extraArgs = new ArrayList<>();

        for (int i = 0; i < arguments.size(); i++) {
            String arg = arguments.get(i);
            if (arg.startsWith("--") && i < arguments.size() - 1) {
                values.put(arg.substring(2), arguments.get(++i));
            } else {
                extraArgs.add(arg);
            }
        }

        return new MapEntry<>(values, extraArgs);
    }

}
