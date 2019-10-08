package com.baharmc.loader.utils.semanticversion;

public interface Version {
    String getFriendlyString();

    static Version parse(String string) throws VersionParsingException {
        return VersionDeserializer.deserialize(string);
    }
}
