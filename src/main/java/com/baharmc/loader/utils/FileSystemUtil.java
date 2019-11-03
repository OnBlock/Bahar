package com.baharmc.loader.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class FileSystemUtil {

    private FileSystemUtil() {
    }

    private static final Map<String, String> JFS_ARGS_CREATE = new HashMap<>();

    private static final Map<String, String> JFS_ARGS_EMPTY = new HashMap<>();

    static {
        JFS_ARGS_CREATE.put("create", "true");
    }

	public static FileSystemDelegate getJarFileSystem(@NotNull File file, boolean create) throws IOException {
    	return getJarFileSystem(file.toURI(), create);
	}

	public static FileSystemDelegate getJarFileSystem(@NotNull Path path, boolean create) throws IOException {
		return getJarFileSystem(path.toUri(), create);
	}

	public static FileSystemDelegate getJarFileSystem(@NotNull URI uri, boolean create) throws IOException {
        final URI jarUri;

        try {
            jarUri = new URI("jar:" + uri.getScheme(), uri.getHost(), uri.getPath(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        try {
            return new FileSystemDelegate(FileSystems.newFileSystem(jarUri, create ? JFS_ARGS_CREATE : JFS_ARGS_EMPTY), true);
        } catch (FileSystemAlreadyExistsException e) {
            return new FileSystemDelegate(FileSystems.getFileSystem(jarUri), false);
        }
    }
}
