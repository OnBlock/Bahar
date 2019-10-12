package com.baharmc.loader.loaded;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.mock.MckMappingResolved;
import com.baharmc.loader.mock.MckPluginContained;
import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.provided.GameProvided;
import org.cactoos.collection.CollectionOf;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class BaharLoadedBasic implements BaharLoaded {

    @NotNull
    private final BaharLaunched launched;

    @NotNull
    private final GameProvided provided;

    @NotNull
    private MappingResolved mappingResolved = new MckMappingResolved();

    public static BaharLoaded INSTANCE;

    public BaharLoadedBasic(@NotNull BaharLaunched launched, @NotNull GameProvided provided) {
        this.launched = launched;
        this.provided = provided;
    }

    @Override
    public void load() {

    }

    @Override
    public void freeze() {

    }

    @NotNull
    @Override
    public <T> List<T> getEntryPoints(@NotNull String key, @NotNull Class<T> type) {
        return new ListOf<>();
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
    public PluginContained getPluginContained(String id) {
        return new MckPluginContained();
    }

    @NotNull
    @Override
    public Collection<PluginContained> getAllPlugins() {
        return new CollectionOf<>();
    }

    @Override
    public boolean isModLoaded(String id) {
        return false;
    }

    @NotNull
    @Override
    public Object getGameInstance() {
        return null;
    }

    @NotNull
    @Override
    public File getGameDirectory() {
        return null;
    }

    @NotNull
    @Override
    public File getConfigDirectory() {
        return null;
    }
}
