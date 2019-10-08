package com.baharmc.loader.launched;

import com.baharmc.loader.BaharLaunched;
import org.jetbrains.annotations.NotNull;

public class LauncherBasic implements BaharLaunched {

    @NotNull
    private final String[] args;

    public LauncherBasic(@NotNull String[] args) {
        this.args = args;
    }

    public void start() {

    }

}
