package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public interface BaharLaunched {

    @NotNull
    MappingConfiguration getMappingConfiguration();

    @NotNull
    Logger getLogger();

    void init();

}
