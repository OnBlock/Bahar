package com.baharmc.loader.plugin.metadata;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Person {

    @NotNull
    String getName();

    @NotNull
    List<String> getRoles();

    @NotNull
    Contact getContact();

}
