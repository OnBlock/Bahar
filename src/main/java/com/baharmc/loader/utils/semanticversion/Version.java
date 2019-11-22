package com.baharmc.loader.utils.semanticversion;

import org.jetbrains.annotations.NotNull;

public interface Version {

    @NotNull
    String getFriendlyString();

    @NotNull
    static Version parse(@NotNull String string) throws VersionParsingException {
        return VersionDeserializer.deserialize(string);
    }

}
