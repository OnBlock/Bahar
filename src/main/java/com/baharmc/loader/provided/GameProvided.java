package com.baharmc.loader.provided;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface GameProvided {

	@NotNull
	String getGameId();

	@NotNull
	String getGameName();

	@NotNull
	String getRawGameVersion();

	@NotNull
	String getNormalizedGameVersion();

	@NotNull
	Collection<BuiltinPlugin> getBuiltinMods();

	@NotNull
	String getEntryPoint();

	@NotNull
	Path getLaunchDirectory();

	boolean requiresUrlClassLoader();

	@NotNull
	List<Path> getGameContextJars();

	@NotNull
	byte[] transform(@NotNull String name);

	void launch(@NotNull ClassLoader loader);

}
