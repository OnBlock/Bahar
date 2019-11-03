package com.baharmc.loader.plugin.metadata;

import org.jetbrains.annotations.NotNull;

public final class ContactBasic implements Contact {

    @NotNull
    private final String name;

    @NotNull
    private final String description;

    public ContactBasic(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getDescription() {
        return description;
    }

}
