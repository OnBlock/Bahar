package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.LaunchedBase;
import com.baharmc.loader.loaded.BaharLoaded;
import com.baharmc.loader.loaded.BaharLoaderBasic;
import com.baharmc.loader.mock.MckEntryPointTransformed;
import com.baharmc.loader.mock.MckGameProvided;
import com.baharmc.loader.provided.EntrypointResult;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.provided.GameProviderHelped;
import com.baharmc.loader.provided.MinecraftProvided;
import com.baharmc.loader.utils.argument.Arguments;
import com.baharmc.loader.utils.version.GetVersion;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public final class Knot extends LaunchedBase {

    @NotNull
    private final List<String> args;

    @NotNull
    private GameProvided provided = new MckGameProvided();

    @NotNull
    private KnotClassLoaded loaded = new KnotCompatibilityClassLoader(this, provided);

    public Knot(@NotNull Logger logger, @NotNull List<String> args, @NotNull File serverJarFile) {
        super(logger, serverJarFile);
        this.args = args;
    }

    @Override
    public void start() {
        if (!(provided instanceof MckEntryPointTransformed)) {
            return;
        }

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

        provided = new MinecraftProvided(
            entrypointResult.getEntrypointName(),
            new GetVersion(
                entrypointResult.getEntrypointPath()
            ).value(),
            arguments,
            entrypointResult.getEntrypointPath(),
            helped.getSource("ModLoader.class").isPresent()
        );

        getLogger().info("Loading for game " + provided.getGameName() + " " + provided.getRawGameVersion());

        loaded = new KnotCompatibilityClassLoader(this, provided);

        for (Path path : provided.getGameContextJars()) {
            deobfuscate(
                provided.getGameId(),
                provided.getNormalizedGameVersion(),
                provided.getLaunchDirectory(),
                path
            );
        }

        provided.getEntrypointTransformer().locateEntryPoints(this);

        Thread.currentThread().setContextClassLoader((ClassLoader) loaded);

        final BaharLoaded baharLoaded = new BaharLoaderBasic(this, provided);

        baharLoaded.load();
        baharLoaded.freeze();
        MixinBootstrap.init();
    }

    @Override
    public void propose(@NotNull URL url) {
        getLogger().fine("[Knot] Proposed " + url + " to classpath.");
        loaded.addURL(url);
    }

    @NotNull
    @Override
    public byte[] getClassByteArray(String name) throws IOException {
        return loaded.getDelegate().getClassByteArray(name, false);
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        return provided.getEntryPoint();
    }
}
