package com.baharmc.loader.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystem;

public class FileSystemDelegate implements AutoCloseable {

    @NotNull
    private final FileSystem fileSystem;

    private final boolean owner;

    public FileSystemDelegate(@NotNull FileSystem fileSystem, boolean owner) {
        this.fileSystem = fileSystem;
        this.owner = owner;
    }

    public FileSystem get() {
        return fileSystem;
    }

    @Override
    public void close() throws IOException {
        if (owner) {
            fileSystem.close();
        }
    }

}