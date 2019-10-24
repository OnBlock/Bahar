package com.baharmc.loader.metadata;

import org.jetbrains.annotations.NotNull;

public interface EntryPointMetadata {

	@NotNull
	String getAdapter();

	@NotNull
	String getValue();

}
