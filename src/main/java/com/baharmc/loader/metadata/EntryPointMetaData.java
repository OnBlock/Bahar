package com.baharmc.loader.metadata;

import org.jetbrains.annotations.NotNull;

public interface EntryPointMetaData {

	@NotNull
	String getAdapter();

	@NotNull
	String getValue();

}
