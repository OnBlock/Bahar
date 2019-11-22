package com.baharmc.loader.launched.knot;

import org.jetbrains.annotations.NotNull;

import java.security.CodeSource;
import java.util.jar.Manifest;

class Metadata {

	static final Metadata EMPTY = new Metadata(null, null);

	@NotNull
	private final Manifest manifest;

	@NotNull
	private final CodeSource codeSource;

	public Metadata(@NotNull Manifest manifest, @NotNull CodeSource codeSource) {
		this.manifest = manifest;
		this.codeSource = codeSource;
	}

	@NotNull
	public Manifest getManifest() {
		return manifest;
	}

	@NotNull
	public CodeSource getCodeSource() {
		return codeSource;
	}

}