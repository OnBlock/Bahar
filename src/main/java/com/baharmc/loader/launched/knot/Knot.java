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
import java.util.List;
import java.util.Optional;

public final class Knot extends LaunchedBase {

    @NotNull
    private GameProvided provided = new MckGameProvided();

    @NotNull
    private KnotClassLoaded loaded = new KnotClassLoaderBasic(this, provided);

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
        final Optional<EntryPointResult> optionalEntryPointResult = new GameProviderHelped(
            getClass().getClassLoader()
        ).findFirstClass(classes);

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

        loaded = new KnotClassLoaderBasic(this, provided);

        getLogger().info("Loading for game " + provided.getGameName() + " " + provided.getRawGameVersion());
        deobfuscate(provided);
        provided.getEntryPointTransformed().locateEntryPoints(this);
        Thread.currentThread().setContextClassLoader(getTargetClassLoader());

        // LOADING
        final BaharLoaded baharLoaded = new BaharLoaderBasic(this, provided);

        baharLoaded.load();
        baharLoaded.lock();
        MixinBootstrap.init();
        new BaharMixinBootstrap(baharLoaded).init();
        doneMixinBootstrapping();
        getKnotClassLoaded().getDelegate().initializeTransformers();
        // LOADING

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

    @NotNull
    @Override
    public KnotClassLoaded getKnotClassLoaded() {
        return loaded;
    }
}
