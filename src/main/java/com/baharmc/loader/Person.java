package com.baharmc.loader;

import org.jetbrains.annotations.NotNull;

public interface Person {

    @NotNull
    String getName();

    @NotNull
    Contact getContact();

}
