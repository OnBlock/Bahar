package com.baharmc.loader.launched.knot;

import java.security.CodeSource;
import java.util.jar.Manifest;

class Metadata {

	static final Metadata EMPTY = new Metadata(null, null);

	private final Manifest manifest;
	private final CodeSource codeSource;

	Metadata(Manifest manifest, CodeSource codeSource) {
		this.manifest = manifest;
		this.codeSource = codeSource;
	}

}