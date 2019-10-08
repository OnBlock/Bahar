package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.LaunchedBase;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Knot extends LaunchedBase {

    @NotNull
    private final File serverJarFile;

    public Knot(@NotNull File serverJarFile) {
        this.serverJarFile = serverJarFile;
    }

    @Override
    public void init() {

    }

}
