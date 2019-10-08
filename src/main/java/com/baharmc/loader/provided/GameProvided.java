package com.baharmc.loader.provided;

import com.baharmc.loader.api.EnvType;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface GameProvided {

	String getGameId();
	String getGameName();
	String getRawGameVersion();
	String getNormalizedGameVersion();
	Collection<BuiltinPlugin> getBuiltinMods();

	String getEntryPoint();
	Path getLaunchDirectory();
	boolean isObfuscated();
	boolean requiresUrlClassLoader();
	List<Path> getGameContextJars();

	boolean locateGame(EnvType envType, ClassLoader loader);
	void acceptArguments(String... arguments);
	// TODO EntryPointTransformer getEntryPointTransformer();
	void launch(ClassLoader loader);

}
