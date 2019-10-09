package com.baharmc.loader.provided;

import com.baharmc.loader.GameProvided;
import com.baharmc.loader.api.EnvType;
import com.baharmc.loader.metadata.PluginMetaDataBasic;
import com.baharmc.loader.utils.Arguments;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
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

    @NotNull
    @Override
    public String getGameId() {
        return "minecrfft";
    }

    @NotNull
    @Override
    public String getGameName() {
        return "Minecraft";
    }

    @NotNull
    @Override
    public String getRawGameVersion() {
        return mcVersion.getRaw();
    }

    @NotNull
    @Override
    public String getNormalizedGameVersion() {
        return mcVersion.getNormalized();
    }

    @NotNull
    @Override
    public Collection<BuiltinPlugin> getBuiltinMods() {
        URL url;

        try {
            url = gameJar.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new ListOf<>(
            new BuiltinPlugin(
                url,
                new BuiltinModMetadata.Builder(
                    getGameId(),
                    getNormalizedGameVersion()
                ).setName(getGameName())
                    .build()
            )
        );
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        return entryPoint;
    }

    @NotNull
    @Override
    public Path getLaunchDirectory() {
        return arguments.launchDirectory().toPath();
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return hasModLoader;
    }

    @NotNull
    @Override
    public List<Path> getGameContextJars() {
        return new ListOf<>(gameJar);
    }

    @Override
    public boolean locateGame(@NotNull EnvType envType, @NotNull ClassLoader loader) {
        return false;
    }

    @Override
    public void acceptArguments(@NotNull String... arguments) {

    }

    @Override
    public void launch(@NotNull ClassLoader loader) {

    }
}
