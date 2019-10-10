package com.baharmc.loader.utils.argument;

import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class Arguments {

    @NotNull
    private final Scalar<Map.Entry<Map<String, String>, List<String>>> scalar;

    public Arguments(@NotNull Scalar<Map.Entry<Map<String, String>, List<String>>> scalar) {
        this.scalar = scalar;
    }

    public Arguments(@NotNull Map.Entry<Map<String, String>, List<String>> arguments) {
        this(() -> arguments);
    }

    public Arguments(@NotNull List<String> arguments) {
        this(new ArgumentParsed(arguments));
    }

    @NotNull
    public String[] toArray() {
        try {
            return new ArgumentArrayed(scalar.value().getKey(), scalar.value().getValue()).value();
        } catch (Exception e) {
            return new String[0];
        }
    }

    public void remove(@NotNull String key) {
        try {
            scalar.value().getKey().remove(key);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    public File getAsFile(@NotNull String path) {
        try {
            return new File(scalar.value().getKey().getOrDefault(path, "."));
        } catch (Exception e) {
            return new File(".");
        }
    }

}
