package com.baharmc.loader.metadata;

import org.jetbrains.annotations.NotNull;

public final class EntryPointMetaDataBasic implements EntryPointMetaData {

    @NotNull
    private final String adapter;
    
    @NotNull
    private final String value;

    public EntryPointMetaDataBasic(@NotNull String adapter, @NotNull String value) {
        this.adapter = adapter;
        this.value = value;
    }

    @NotNull
    @Override
    public String getAdapter() {
        return adapter;
    }

    @NotNull
    @Override
    public String getValue() {
        return value;
    }
}
