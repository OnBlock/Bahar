package com.baharmc.loader.loaded;

import com.baharmc.loader.contained.PluginContainerBasic;
import com.baharmc.loader.discovery.ClasspathPluginCandidateFound;
import com.baharmc.loader.discovery.PluginCandidate;
import com.baharmc.loader.discovery.PluginResolutionException;
import com.baharmc.loader.discovery.PluginResolve;
import com.baharmc.loader.entrypoint.EntryPointStorage;
import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.mock.MckMappingResolved;
import com.baharmc.loader.mock.MckPluginContained;
import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.utils.semanticversion.SemanticVersion;
import org.cactoos.collection.CollectionOf;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
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

    private boolean locked = false;

    static BaharLoaded INSTANCE;

    public BaharLoaderBasic(@NotNull BaharLaunched launched, @NotNull GameProvided provided) {
        this.launched = launched;
        this.provided = provided;
        this.entryPointStorage = new EntryPointStorage(launched);
        INSTANCE = this;
    }

    @Override
    public void load() {
        if (locked) {
            throw new RuntimeException("Bahar is already loaded!");
        }

        final PluginResolve pluginResolve = new PluginResolve(
            launched.getLogger(),
            provided,
            new ClasspathPluginCandidateFound()
        );

        final Map<String, PluginCandidate> pluginCandidates;

        try {
            pluginCandidates = pluginResolve.resolve();
        } catch (PluginResolutionException e) {
            throw new RuntimeException(e);
        }

        if (!pluginCandidates.containsKey("bahar")) {
            throw new RuntimeException("Bahar cannot be loaded!");
        }

        launched.getLogger().info(
            "Loading for Bahar " +
                pluginCandidates.get("bahar").getInfo().getVersion().getFriendlyString()
        );
        pluginCandidates.values().forEach(pluginCandidate -> {
            try {
                addPlugin(pluginCandidate);
            } catch (PluginResolutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void lock() {
        if (locked) {
            throw new RuntimeException("Bahar is already locked!");
        }

        locked = true;
    }

    @Override
    public void loadPlugins() {

        finishLoading();
    }

    @Override
    public void enablePlugins() {

    }

    @Override
    public void disablePlugins() {

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

    @Override
    public void addPlugin(@NotNull PluginCandidate pluginCandidate) throws PluginResolutionException {
        final LoadedPluginMetaData info = pluginCandidate.getInfo();
        final URL url = pluginCandidate.getUrl();

        if (plugins.containsKey(info.getId())) {
            throw new PluginResolutionException(
                "Duplicate plugin ID: " + info.getId() +
                    "! (" + plugins.get(info.getId()).getOriginURL().getFile() + ", " + url.getFile() + ")"
            );
        }

        plugins.put(info.getId(), new PluginContainerBasic(info, url));
    }

    private void finishLoading() {
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
