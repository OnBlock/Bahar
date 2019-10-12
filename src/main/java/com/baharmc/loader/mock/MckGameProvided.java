package com.baharmc.loader.mock;

import com.baharmc.loader.provided.BuiltinPlugin;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.cactoos.collection.CollectionOf;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class MckGameProvided implements GameProvided {

    @NotNull
    @Override
    public String getGameId() {
        return "";
    }

    @NotNull
    @Override
    public String getGameName() {
        return "";
    }

    @NotNull
    @Override
    public String getRawGameVersion() {
        return "";
    }

    @NotNull
    @Override
    public String getNormalizedGameVersion() {
        return "";
    }

    @NotNull
    @Override
    public Collection<BuiltinPlugin> getBuiltinMods() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        return "";
    }

    @NotNull
    @Override
    public Path getLaunchDirectory() {
        return null;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return false;
    }

    @NotNull
    @Override
    public List<Path> getGameContextJars() {
        return new ListOf<>();
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String name) {
        return new byte[0];
    }

    @Override
    public void launch(@NotNull ClassLoader loader) {
    }

    @NotNull
    @Override
    public EntryPointTransformed getEntryPointTransformed() {
        return new MckEntryPointTransformed();
    }
}
