package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class LaunchedBase implements BaharLaunched {

    @NotNull
    protected Map<String, Object> properties = new HashMap<>();

    @NotNull
    private final Logger logger;

    @NotNull
    private final MappingConfiguration mappingConfiguration;

    public LaunchedBase(@NotNull Logger logger) {
        this.logger = logger;
        this.mappingConfiguration = new MappingConfiguration(this);
    }

    @NotNull
    @Override
    public MappingConfiguration getMappingConfiguration() {
        return mappingConfiguration;
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }
}
