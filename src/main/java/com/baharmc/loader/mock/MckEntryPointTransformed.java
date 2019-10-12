package com.baharmc.loader.mock;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.jetbrains.annotations.NotNull;

public class MckEntryPointTransformed implements EntryPointTransformed {
    @Override
    public void locateEntryPoints(BaharLaunched launched) {
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String target) {
        return new byte[0];
    }
}
