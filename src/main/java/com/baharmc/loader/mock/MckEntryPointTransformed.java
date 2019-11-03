package com.baharmc.loader.mock;

import com.baharmc.loader.launched.common.BaharLaunched;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.logging.Logger;

public class MckEntryPointTransformed implements EntryPointTransformed {
    @NotNull
    @Override
    public Logger getLogger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void locateEntryPoints(@NotNull BaharLaunched launched) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassNode loadClass(@NotNull BaharLaunched launched, @NotNull String className) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String target) {
        throw new UnsupportedOperationException();
    }
}
