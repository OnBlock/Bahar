package com.baharmc.loader.provided;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class EntrypointResult {

	@NotNull
	private final String entrypointName;

	@NotNull
	private final Path entrypointPath;

	public EntrypointResult(@NotNull String entrypointName, @NotNull Path entrypointPath) {
		this.entrypointName = entrypointName;
		this.entrypointPath = entrypointPath;
	}

	public String getEntrypointName() {
		return entrypointName;
	}

	public Path getEntrypointPath() {
		return entrypointPath;
	}
}