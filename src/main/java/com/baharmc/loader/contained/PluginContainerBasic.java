package com.baharmc.loader.contained;

import com.baharmc.loader.plugin.PluginContained;
import com.baharmc.loader.plugin.PluginMetaData;
import com.baharmc.loader.utils.FileSystemUtil;
import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginContainerBasic implements PluginContained {

    @NotNull
    private final PluginMetaData pluginMetaData;

    @NotNull
    private final URL originUrl;

    private Path root;

    public PluginContainerBasic(@NotNull PluginMetaData pluginMetaData, @NotNull URL originUrl) {
        this.pluginMetaData = pluginMetaData;
        this.originUrl = originUrl;
    }

    @NotNull
    @Override
    public URL getOriginURL() {
        return originUrl;
    }

    @Override
    public void instantiate() {
        if (root != null) {
            throw new RuntimeException("Not allowed to instantiate twice!");
        }

        try {
            Path holder = UrlUtil.asPath(originUrl).toAbsolutePath();
            if (Files.isDirectory(holder)) {
                root = holder.toAbsolutePath();
            } else {
                final FileSystemUtil.FileSystemDelegate delegate = FileSystemUtil.getJarFileSystem(holder, false);

                if (delegate.get() == null) {
                    throw new RuntimeException("Could not open JAR file " + holder.getFileName() + " for NIO reading!");
                }

                root = delegate.get().getRootDirectories().iterator().next();
            }
        } catch (IOException | UrlConversionException e) {
            throw new RuntimeException("Failed to find root directory for plugin '" + pluginMetaData.getId() + "'!", e);
        }
    }

    @NotNull
    @Override
    public PluginMetaData getMetadata() {
        return pluginMetaData;
    }

    @NotNull
    @Override
    public Path getRootPath() {
        if (root == null) {
            throw new RuntimeException("Accessed plugin root before primary loader!");
        }
        return root;
    }

}
