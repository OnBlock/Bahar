package com.baharmc.loader.loaded;

import net.fabricmc.mappings.Mappings;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MappingResolverBasic implements MappingResolved {

    @NotNull
    private final Mappings mappings;

    @NotNull
    private final String targetName;

    public MappingResolverBasic(@NotNull Mappings mappings, @NotNull String targetName) {
        this.mappings = mappings;
        this.targetName = targetName;
    }

    @NotNull
    @Override
    public Collection<String> getNamespaces() {
        return null;
    }

    @NotNull
    @Override
    public String getCurrentRuntimeNamespace() {
        return null;
    }

    @NotNull
    @Override
    public String mapClassName(@NotNull String namespace, @NotNull String className) {
        return null;
    }

    @NotNull
    @Override
    public String unmapClassName(@NotNull String targetNamespace, @NotNull String className) {
        return null;
    }

    @NotNull
    @Override
    public String mapFieldName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor) {
        return null;
    }

    @NotNull
    @Override
    public String mapMethodName(@NotNull String namespace, @NotNull String owner, @NotNull String name, @NotNull String descriptor) {
        return null;
    }
}
