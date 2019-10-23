package com.baharmc.loader.provided;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class EntryPointResult {

	@NotNull
	private final String entryPointName;

	@NotNull
	private final Path entryPointPath;

	public EntryPointResult(@NotNull String entryPointName, @NotNull Path entryPointPath) {
		this.entryPointName = entryPointName;
		this.entryPointPath = entryPointPath;
	}

	@NotNull
	public String getEntryPointName() {
		return entryPointName;
	}

	@NotNull
	public Path getEntryPointPath() {
		return entryPointPath;
	}
}