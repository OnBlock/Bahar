package com.baharmc.loader.loaded;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class InitiatePlugin {

    @NotNull
    private final Object gameInstance;

    @NotNull
    private final File runDirectory;

    public InitiatePlugin(@NotNull Object gameInstance, @NotNull File runDirectory) {
        this.gameInstance = gameInstance;
        this.runDirectory = runDirectory;
    }

    public void init() {

    }

}
