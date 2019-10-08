package com.baharmc.loader.provided;

import org.jetbrains.annotations.NotNull;

public final class McVersion {

	@NotNull
	public final String raw;

	@NotNull
	public final String normalized;

	public McVersion(@NotNull String raw, @NotNull String normalized) {
		this.raw = raw;
		this.normalized = normalized;
	}

}