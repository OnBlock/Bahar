package com.baharmc.loader.entrypoint.hooks;

import net.fabricmc.loader.FabricLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

final class EntryPointUtils {

	private EntryPointUtils() {
	}

	static <T> void logErrors(String name, Collection<T> entrypoints, Consumer<T> entrypointConsumer) {
		List<Throwable> errors = new ArrayList<>();

		FabricLoader.INSTANCE.getLogger().debug("Iterating over entrypoint '" + name + "'");

		entrypoints.forEach(e -> {
			try {
				entrypointConsumer.accept(e);
			} catch (Throwable t) {
				errors.add(t);
			}
		});

		if (!errors.isEmpty()) {
			final RuntimeException exception = new RuntimeException("Could not execute entrypoint stage '" + name + "' due to errors!");

			for (Throwable t : errors) {
				exception.addSuppressed(t);
			}
			
			throw exception;
		}
	}
}
