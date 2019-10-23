package com.baharmc.loader.mock;

import com.baharmc.loader.provided.BuiltinPlugin;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;

public class MckGameProvided implements GameProvided {

    @NotNull
    @Override
    public String getGameId() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getGameName() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getRawGameVersion() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getNormalizedGameVersion() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection<BuiltinPlugin> getBuiltinMods() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Path getGameContextJars() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void launch(@NotNull ClassLoader loader) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public EntryPointTransformed getEntryPointTransformed() {
        throw new UnsupportedOperationException();
    }
}
