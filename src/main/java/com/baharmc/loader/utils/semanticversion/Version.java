package com.baharmc.loader.utils.semanticversion;

import org.jetbrains.annotations.NotNull;

public interface Version {

    @NotNull
    String getFriendlyString();

    @NotNull
    static Version parse(String string) throws VersionParsingException {
        return VersionDeserializer.deserialize(string);
    }

}
