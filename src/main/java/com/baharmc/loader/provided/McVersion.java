package com.baharmc.loader.provided;

import org.jetbrains.annotations.NotNull;

public final class McVersion {

	@NotNull
	private final String raw;

	@NotNull
	private final String normalized;

	public McVersion(@NotNull String raw, @NotNull String normalized) {
		this.raw = raw;
		this.normalized = normalized;
	}

	@NotNull
	public String getRaw() {
		return raw;
	}

	@NotNull
	public String getNormalized() {
		return normalized;
	}
}