package com.baharmc.loader.plugin;

import com.baharmc.loader.plugin.metadata.*;
import com.baharmc.loader.utils.semanticversion.Version;
import org.apache.logging.log4j.Logger;
import org.cactoos.collection.CollectionOf;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class PluginMetaDataBasic implements LoadedPluginMetaData {

    @NotNull
    private final String id;

    @NotNull
    private final String name;

    private final boolean isStable;

    private final boolean isSnapshot;

    @NotNull
    private final String description;

    @NotNull
    private final Version version;

    @NotNull
    private final License license;

    @NotNull
    private final List<Person> authors;

    @NotNull
    private final List<Contact> contacts;

    @NotNull
    private final List<Dependency> dependencies;

    public PluginMetaDataBasic(@NotNull String id, @NotNull String name, boolean isStable, boolean isSnapshot,
                               @NotNull String description, @NotNull Version version, @NotNull License license,
                               @NotNull List<Person> authors, @NotNull List<Contact> contacts,
                               @NotNull List<Dependency> dependencies) {
        this.id = id;
        this.name = name;
        this.isStable = isStable;
        this.isSnapshot = isSnapshot;
        this.description = description;
        this.version = version;
        this.license = license;
        this.authors = authors;
        this.contacts = contacts;
        this.dependencies = dependencies;
    }

    public PluginMetaDataBasic(@NotNull String id, @NotNull String name, boolean isStable, boolean isSnapshot,
                               @NotNull String description, @NotNull Version version) {
        this(id, name, isStable, isSnapshot, description, version, ListOf<String>::new, new ListOf<>(), new ListOf<>(),
            new ListOf<>()
        );
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public Version getVersion() {
        return version;
    }

    @NotNull
    @Override
    public Collection<String> getMixinConfigs() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public Collection<NestedJarEntry> getJars() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public void emitFormatWarnings(@NotNull Logger logger) {

    }

}
