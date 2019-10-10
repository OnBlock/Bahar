package com.baharmc.loader.launched;

import com.baharmc.loader.provided.EntrypointResult;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.provided.GameProviderHelped;
import com.baharmc.loader.provided.MinecraftProvided;
import com.baharmc.loader.utils.argument.Arguments;
import com.baharmc.loader.utils.version.GetVersion;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public final class Knot extends LaunchedBase {

    @NotNull
    private final List<String> args;

    @NotNull
    private final File serverJarFile;

    public Knot(@NotNull Logger logger, @NotNull List<String> args, @NotNull File serverJarFile) {
        super(logger);
        this.args = args;
        this.serverJarFile = serverJarFile;
    }

    @Override
    public void init() {
        final List<String> classes = new ListOf<>(
            "net.minecraft.server.MinecraftServer",
            "com.mojang.minecraft.server.MinecraftServer"
        );
        final GameProviderHelped helped = new GameProviderHelped(getClass().getClassLoader());
        final Optional<EntrypointResult> optionalEntrypointResult = helped.findFirstClass(classes);

        if (!optionalEntrypointResult.isPresent()) {
            return;
        }

        final EntrypointResult entrypointResult = optionalEntrypointResult.get();
        final Arguments arguments = new Arguments(args);

        arguments.remove("version");
        arguments.remove("gameDir");
        arguments.remove("assetsDir");

        final GameProvided provided = new MinecraftProvided(
            entrypointResult.getEntrypointName(),
            new GetVersion(
                entrypointResult.getEntrypointPath()
            ).value(),
            arguments,
            entrypointResult.getEntrypointPath(),
            helped.getSource("ModLoader.class").isPresent()
        );

        getLogger().info("Loading for game " + provided.getGameName() + " " + provided.getRawGameVersion());

    }

    @Override
    public void propose(@NotNull URL url) {

    }

}
