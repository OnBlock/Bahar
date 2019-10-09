package com.baharmc.loader.provided;

import com.baharmc.loader.PluginMetaData;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public final class BuiltinPlugin {

	@NotNull
	private final URL url;

	@NotNull
	private final PluginMetaData pluginMetaData;

	public BuiltinPlugin(@NotNull URL url, @NotNull PluginMetaData pluginMetaData) {
		this.url = url;
		this.pluginMetaData = pluginMetaData;
	}

	public URL getUrl() {
		return url;
	}

	public PluginMetaData getPluginMetaData() {
		return pluginMetaData;
	}

}