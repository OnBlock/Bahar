package com.baharmc.loader.launched;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public final class Knot extends LaunchedBase {

    @NotNull
    private final File serverJarFile;

    public Knot(@NotNull File serverJarFile, @NotNull Logger logger) {
        super(logger);
        this.serverJarFile = serverJarFile;
    }

    @Override
    public void init() {

    }

}
