package com.baharmc.loader.plugin;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface PluginContained {

    /**
     * Gets the Plugin meta data
     * @return {@link PluginMetaData}
     */
    @NotNull
    PluginMetaData getMetaData();

    /**
     * Gets the Roots of the plugin
     *
     * @return {@link Path}
     */
    @NotNull
    Path getRootPath();

    /**
     * Get an NIO reference to a file inside the JAR.
     * Does not guarantee existence!
     *
     * @param file The location from root, using "/" as a separator.
     * @return The Path to a given file.
     */
    @NotNull
    default Path getPath(String file) {
        Path root = getRootPath();
        return root.resolve(file.replace("/", root.getFileSystem().getSeparator()));
    }
}
