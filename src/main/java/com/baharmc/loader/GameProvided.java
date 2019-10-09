package com.baharmc.loader;

import com.baharmc.loader.api.EnvType;
import com.baharmc.loader.provided.BuiltinPlugin;
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

	boolean locateGame(@NotNull EnvType envType, @NotNull ClassLoader loader);

	void acceptArguments(@NotNull String... arguments);

	void launch(@NotNull ClassLoader loader);

}
