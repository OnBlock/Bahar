package com.baharmc.loader.transformed;

import com.baharmc.loader.launched.common.BaharLaunched;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.logging.Logger;

public interface EntryPointTransformed {

    @NotNull
    Logger getLogger();

    void locateEntryPoints(@NotNull BaharLaunched launched);

    ClassNode loadClass(@NotNull BaharLaunched launched, @NotNull String className) throws IOException;

    @NotNull
    byte[] transform(@NotNull String target);

}
