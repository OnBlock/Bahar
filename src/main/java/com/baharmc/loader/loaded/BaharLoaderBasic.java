package com.baharmc.loader.loaded;

import com.baharmc.loader.entrypoint.EntryPointStorage;
import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.launched.common.BaharMixinBootstrap;
import com.baharmc.loader.mock.MckMappingResolved;
import com.baharmc.loader.mock.MckPluginContained;
import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.provided.GameProvided;
import net.fabricmc.loader.api.SemanticVersion;
import org.cactoos.collection.CollectionOf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaharLoaderBasic implements BaharLoaded {

    @NotNull
    private final BaharLaunched launched;

    @NotNull
    private final GameProvided provided;

    @NotNull
    private MappingResolved mappingResolved = new MckMappingResolved();

    private final EntryPointStorage entryPointStorage;

    private final Map<String, PluginContained> plugins = new HashMap<>();

    private boolean frozen = false;

    static BaharLoaded INSTANCE;

    public BaharLoaderBasic(@NotNull BaharLaunched launched, @NotNull GameProvided provided) {
        this.launched = launched;
        this.provided = provided;
        this.entryPointStorage = new EntryPointStorage(launched);
        INSTANCE = this;
    }

    @Override
    public void loadPlugins() {
        freeze();
        MixinBootstrap.init();
        new BaharMixinBootstrap(this).init();
        launched.doneMixinBootstrapping();
        launched.getKnotClassLoaded().getDelegate().initializeTransformers();
    }

    @Override
    public void enablePlugins() {

    }

    @Override
    public void disablePlugins() {

    }

    @Override
    public void freeze() {
        if (frozen) {
            throw new RuntimeException("Already frozen!");
        }

        frozen = true;
        finishPluginLoading();
    }

    @NotNull
    @Override
    public <T> List<T> getEntryPoints(@NotNull String key, @NotNull Class<T> type) {
        return entryPointStorage.getEntryPoints(key, type);
    }

    @NotNull
    @Override
    public MappingResolved getMappingResolver() {
        if (mappingResolved instanceof MckMappingResolved) {
            mappingResolved = new MappingResolverBasic(
                launched.getMappingConfiguration().getMappings(),
                launched.getTargetNamespace()
            );
        }

        return mappingResolved;
    }

    @NotNull
    @Override
    public PluginContained getPluginContained(@NotNull String id) {
        return plugins.getOrDefault(id, new MckPluginContained());
    }

    @NotNull
    @Override
    public Collection<PluginContained> getAllPlugins() {
        return new CollectionOf<>(
            plugins.values()
        );
    }

    @Override
    public boolean isPluginLoaded(@NotNull String id) {
        return plugins.containsKey(id);
    }

    private void finishPluginLoading() {
        for (PluginContained pluginContained : plugins.values()) {
            if (!pluginContained.getMetadata().getId().equals("bahar")) {
                launched.propose(pluginContained.getOriginURL());
            }
        }

        postprocessPluginMetadata();
    }

    private void postprocessPluginMetadata() {
        for (PluginContained pluginContained : plugins.values()) {
            if (!(pluginContained.getMetadata().getVersion() instanceof SemanticVersion)) {
                launched.getLogger().warn("Plugin `" +
                    pluginContained.getMetadata().getId() +
                    "` (" +
                    pluginContained.getMetadata().getVersion().getFriendlyString() +
                    ") does not respect SemVer - comparison support is limited."
                );
            } else if (((SemanticVersion) pluginContained.getMetadata().getVersion()).getVersionComponentCount() >= 4) {
                launched.getLogger().warn("Plugin `" +
                    pluginContained.getMetadata().getId() +
                    "` (" +
                    pluginContained.getMetadata().getVersion().getFriendlyString() +
                    ") uses more dot-separated version components than SemVer allows; support for this is currently not guaranteed."
                );
            }
        }
    }

}
