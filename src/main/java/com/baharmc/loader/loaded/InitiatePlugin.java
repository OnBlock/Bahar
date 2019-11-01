package com.baharmc.loader.loaded;

import com.baharmc.loader.entrypoint.EntryPointStorage;
import com.baharmc.loader.language.DefaultLanguageAdapted;
import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.launched.knot.Knot;
import com.baharmc.loader.metadata.EntryPointMetaData;
import com.baharmc.loader.plugin.PluginContained;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class InitiatePlugin {

    private final BaharLaunched launched = BaharLaunched.getInstance();
    private final BaharLoaded loaded = BaharLoaded.getInstance();

    @NotNull
    private final Object gameInstance;

    @NotNull
    private final File runDirectory;

    public InitiatePlugin(@NotNull Object gameInstance, @NotNull File runDirectory) {
        this.gameInstance = gameInstance;
        this.runDirectory = runDirectory;
    }

    public void init() {
        if (launched instanceof Knot) {
            ClassLoader gameClassLoader = gameInstance.getClass().getClassLoader();
            final ClassLoader targetClassLoader = launched.getTargetClassLoader();
            final boolean matchesKnot = (gameClassLoader == targetClassLoader);
            boolean containsKnot = false;

            if (matchesKnot) {
                containsKnot = true;
            } else {
                gameClassLoader = gameClassLoader.getParent();
                while (gameClassLoader != null && gameClassLoader.getParent() != gameClassLoader) {
                    if (gameClassLoader == targetClassLoader) {
                        containsKnot = true;
                    }
                    gameClassLoader = gameClassLoader.getParent();
                }
            }

            if (!matchesKnot) {
                if (containsKnot) {
                    launched.getLogger().info("Environment: Target class loader is parent of game class loader.");
                } else {
                    launched.getLogger().warn("\n\n* CLASS LOADER MISMATCH! THIS IS VERY BAD AND WILL PROBABLY CAUSE WEIRD ISSUES! *\n"
                        + " - Expected game class loader: " + launched.getTargetClassLoader() + "\n"
                        + " - Actual game class loader: " + gameClassLoader + "\n"
                        + "Could not find the expected class loader in game class loader parents!\n");
                }
            }
        }

        final EntryPointStorage entryPointStorage = loaded.getEntryPointStorage();

        for (PluginContained plugin : loaded.getAllPlugins()) {
            try {
                plugin.instantiate();

                for (EntryPointMetaData in : plugin.getMetadata().getEntryPoints("main")) {
                    entryPointStorage.add(plugin, "main", in, new MapOf<>(
                        new MapEntry<>("main", new DefaultLanguageAdapted())
                    ));
                }
            } catch (Exception e) {
                throw new RuntimeException(
                    String.format("Failed to load mod %s (%s)",
                        plugin.getMetadata().getName(),
                        plugin.getOriginURL().getFile()
                    ), e
                );
            }
        }
    }

}
