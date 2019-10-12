package com.baharmc.loader.mock;

import com.baharmc.loader.loaded.MappingResolved;
import org.cactoos.collection.CollectionOf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MckMappingResolved implements MappingResolved {

    @NotNull
    @Override
    public Collection<String> getNamespaces() {
        return new CollectionOf<>();
    }

    @NotNull
    @Override
    public String getCurrentRuntimeNamespace() {
        return "";
    }

    @NotNull
    @Override
    public String mapClassName(@NotNull String namespace, @NotNull String className) {
        return "";
    }

    @NotNull
    @Override
    public String unmapClassName(@NotNull String targetNamespace, @NotNull String className) {
        return "";
    }

    @NotNull
    @Override
    public String mapFieldName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor) {
        return "";
    }

    @NotNull
    @Override
    public String mapMethodName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor) {
        return "";
    }
}
