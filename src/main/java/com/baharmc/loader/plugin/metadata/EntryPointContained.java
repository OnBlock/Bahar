package com.baharmc.loader.plugin.metadata;

import com.baharmc.loader.metadata.EntryPointMetaData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntryPointContained {

    @NotNull
    List<EntryPointMetaData> getMetaDataMap();

}
