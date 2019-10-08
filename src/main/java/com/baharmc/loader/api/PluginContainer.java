package com.baharmc.loader.api;

import com.baharmc.loader.metadata.PluginMetaData;

import java.nio.file.Path;

public interface PluginContainer {
    /**
     * Gets the Plugin meta data
     * @return PluginMetaData
     */
    PluginMetaData getMetaData();

    Path getRootPath();

    /**
     * Get an NIO reference to a file inside the JAR.
     * Does not guarantee existence!
     *
     * @param file The location from root, using "/" as a separator.
     * @return The Path to a given file.
     */
    default Path getPath(String file) {
        Path root = getRootPath();
        return root.resolve(file.replace("/", root.getFileSystem().getSeparator()));
    }
}
