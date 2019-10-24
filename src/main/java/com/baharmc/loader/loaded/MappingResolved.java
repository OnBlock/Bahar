package com.baharmc.loader.loaded;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface MappingResolved {

    @NotNull
    Collection<String> getNamespaces();

    @NotNull
    String getCurrentRuntimeNamespace();

    @NotNull
    String mapClassName(@NotNull String namespace, @NotNull String className);

    @NotNull
    String unmapClassName(@NotNull String targetNamespace, @NotNull String className);

    @NotNull
    String mapFieldName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor);

    @NotNull
    String mapMethodName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor);

}
