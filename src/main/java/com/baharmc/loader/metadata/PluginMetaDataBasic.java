package com.baharmc.loader.metadata;

import com.baharmc.loader.PluginMetaData;
import com.baharmc.loader.utils.semanticversion.Version;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginMetaDataBasic implements PluginMetaData {

    @NotNull
    private final String id;

    @NotNull
    private final String name;

    @NotNull
    private final Version version;

    private final boolean isStable;

    private final boolean isSnapshot;

    @NotNull
    private final String description;

    @NotNull
    private final String license;

    @NotNull
    private final List<String> authors;

    @NotNull
    private final List<String> contacts;

    public PluginMetaDataBasic(@NotNull String id, @NotNull String name, @NotNull Version version,
                               boolean isStable, boolean isSnapshot, @NotNull String description,
                               @NotNull String license, @NotNull List<String> authors, @NotNull List<String> contacts) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.isStable = isStable;
        this.isSnapshot = isSnapshot;
        this.description = description;
        this.license = license;
        this.authors = authors;
        this.contacts = contacts;
    }
}
