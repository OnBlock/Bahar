package com.baharmc.loader.discovery;

import com.baharmc.loader.plugin.LoadedPluginMetaData;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public final class PluginCandidate {

    @NotNull
    private final LoadedPluginMetaData info;

    @NotNull
    private final URL url;

    private final int depth;

    public PluginCandidate(@NotNull LoadedPluginMetaData info, @NotNull URL url, int depth) {
        this.info = info;
        this.url = url;
        this.depth = depth;
    }

    @NotNull
    public LoadedPluginMetaData getInfo() {
        return info;
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PluginCandidate)) {
            return false;
        }
        PluginCandidate other = (PluginCandidate) obj;
        return other.info.getVersion().getFriendlyString().equals(info.getVersion().getFriendlyString()) &&
            other.info.getId().equals(info.getId());
    }

    @Override
    public int hashCode() {
        return info.getId().hashCode() * 17 + info.getVersion().hashCode();
    }

    @Override
    public String toString() {
        return "PluginCandidate{" + info.getId() + "@" + info.getVersion().getFriendlyString() + "}";
    }

}
