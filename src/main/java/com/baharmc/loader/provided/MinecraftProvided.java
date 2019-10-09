package com.baharmc.loader.provided;

import com.baharmc.loader.api.EnvType;
import com.baharmc.loader.utils.Arguments;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class MinecraftProvided implements GameProvided {

    @NotNull
    private final String entryPoint;

    @NotNull
    private final McVersion mcVersion;

    @NotNull
    private final Arguments arguments;

    @NotNull
    private final Path gameJar;

    private final boolean hasModLoader;

    public MinecraftProvided(@NotNull String entryPoint, @NotNull McVersion mcVersion, @NotNull Arguments arguments, @NotNull Path gameJar, boolean hasModLoader) {
        this.entryPoint = entryPoint;
        this.mcVersion = mcVersion;
        this.arguments = arguments;
        this.gameJar = gameJar;
        this.hasModLoader = hasModLoader;
    }

    @Override
    public String getGameId() {
        return "minecrfft";
    }

    @Override
    public String getGameName() {
        return "Minecraft";
    }

    @Override
    public String getRawGameVersion() {
        return mcVersion.getRaw();
    }

    @Override
    public String getNormalizedGameVersion() {
        return mcVersion.getNormalized();
    }

    @Override
    public Collection<BuiltinPlugin> getBuiltinMods() {
        return null;
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        return entryPoint;
    }

    @Override
    public Path getLaunchDirectory() {
        return arguments.launchDirectory().toPath();
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return hasModLoader;
    }

    @Override
    public List<Path> getGameContextJars() {
        return new ListOf<>(gameJar);
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
