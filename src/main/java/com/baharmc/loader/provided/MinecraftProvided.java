package com.baharmc.loader.provided;

import com.baharmc.loader.api.EnvType;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class MinecraftProvided implements GameProvided {



    @Override
    public String getGameId() {
        return null;
    }

    @Override
    public String getGameName() {
        return null;
    }

    @Override
    public String getRawGameVersion() {
        return null;
    }

    @Override
    public String getNormalizedGameVersion() {
        return null;
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        return null;
    }

    @Override
    public String getEntryPoint() {
        return null;
    }

    @Override
    public Path getLaunchDirectory() {
        return null;
    }

    @Override
    public boolean isObfuscated() {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return false;
    }

    @Override
    public List<Path> getGameContextJars() {
        return null;
    }

    @Override
    public boolean locateGame(EnvType envType, ClassLoader loader) {
        return false;
    }

    @Override
    public void acceptArguments(String... arguments) {

    }

    @Override
    public void launch(ClassLoader loader) {

    }
}
