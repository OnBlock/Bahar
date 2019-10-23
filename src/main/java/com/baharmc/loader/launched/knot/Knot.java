package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.LaunchedBase;
import com.baharmc.loader.launched.common.BaharMixinBootstrap;
import com.baharmc.loader.loaded.BaharLoaded;
import com.baharmc.loader.loaded.BaharLoaderBasic;
import com.baharmc.loader.mock.MckGameProvided;
import com.baharmc.loader.provided.EntryPointResult;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.provided.GameProviderHelped;
import com.baharmc.loader.provided.MinecraftProvided;
import com.baharmc.loader.utils.version.GetVersion;
import org.apache.logging.log4j.LogManager;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class Knot extends LaunchedBase {

    @NotNull
    private GameProvided provided = new MckGameProvided();

    @NotNull
    private KnotClassLoaded loaded = new KnotClassLoader(this, provided);

    public Knot(@NotNull File serverJarFile) {
        super(LogManager.getFormatterLogger("Bahar"), serverJarFile);
    }

    @Override
    public void start() {
        if (!(provided instanceof MckGameProvided)) {
            return;
        }

        final List<String> classes = new ListOf<>(
            "net.minecraft.server.MinecraftServer",
            "com.mojang.minecraft.server.MinecraftServer"
        );
        final GameProviderHelped helped = new GameProviderHelped(getClass().getClassLoader());
        final Optional<EntryPointResult> optionalEntryPointResult = helped.findFirstClass(classes);

        if (!optionalEntryPointResult.isPresent()) {
            return;
        }

        final EntryPointResult entrypointResult = optionalEntryPointResult.get();

        provided = new MinecraftProvided(
            entrypointResult.getEntryPointName(),
            new GetVersion(
                entrypointResult.getEntryPointPath()
            ).value(),
            entrypointResult.getEntryPointPath()
        );

        getLogger().info("Loading for game " + provided.getGameName() + " " + provided.getRawGameVersion());

        loaded = new KnotClassLoader(this, provided);

        for (Path path : provided.getGameContextJars()) {
            deobfuscate(
                provided.getGameId(),
                provided.getNormalizedGameVersion(),
                path
            );
        }

        provided.getEntryPointTransformed().locateEntryPoints(this);
        Thread.currentThread().setContextClassLoader(getTargetClassLoader());

        final BaharLoaded baharLoaded = new BaharLoaderBasic(this, provided);

        baharLoaded.load();
        baharLoaded.freeze();
        MixinBootstrap.init();
        new BaharMixinBootstrap(baharLoaded).init();
        doneMixinBootstrapping();
        loaded.getDelegate().initializeTransformers();
        provided.launch(getTargetClassLoader());
    }

    @Override
    public void propose(@NotNull URL url) {
        getLogger().debug("[Knot] Proposed " + url + " to classpath.");
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

    @NotNull
    @Override
    public ClassLoader getTargetClassLoader() {
        return (ClassLoader) loaded;
    }
}
