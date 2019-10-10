package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public interface BaharLaunched {

    @NotNull
    MappingConfiguration getMappingConfiguration();

    @NotNull
    Logger getLogger();

    boolean isDevelopment();

    void init();

}
