package com.baharmc.loader.plugin;

import com.baharmc.loader.plugin.metadata.Contact;
import com.baharmc.loader.plugin.metadata.License;
import com.baharmc.loader.plugin.metadata.Person;
import com.baharmc.loader.plugin.metadata.PluginDependency;
import com.baharmc.loader.utils.semanticversion.Version;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginMetaDataBasic implements PluginMetaData {

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
    private final List<PluginDependency> pluginDependencies;

    public PluginMetaDataBasic(@NotNull String id, @NotNull String name, boolean isStable, boolean isSnapshot,
                               @NotNull String description, @NotNull Version version, @NotNull License license,
                               @NotNull List<Person> authors, @NotNull List<Contact> contacts,
                               @NotNull List<PluginDependency> pluginDependencies) {
        this.id = id;
        this.name = name;
        this.isStable = isStable;
        this.isSnapshot = isSnapshot;
        this.description = description;
        this.version = version;
        this.license = license;
        this.authors = authors;
        this.contacts = contacts;
        this.pluginDependencies = pluginDependencies;
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
}
