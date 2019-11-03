package com.baharmc.loader.entrypoint.hooks;

import com.baharmc.loader.launched.common.BaharLaunched;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

final class EntryPointUtils {

	private EntryPointUtils() {
	}

	static <T> void logErrors(@NotNull String name, @NotNull Collection<T> entryPoints, @NotNull Consumer<T> entryPointConsumer) {
		List<Throwable> errors = new ArrayList<>();

		BaharLaunched.getInstance().getLogger().debug("Iterating over entry point '" + name + "'");

		entryPoints.forEach(e -> {
			try {
				entryPointConsumer.accept(e);
			} catch (Throwable t) {
				errors.add(t);
			}
		});

		if (!errors.isEmpty()) {
			final RuntimeException exception = new RuntimeException("Could not execute entry point stage '" + name + "' due to errors!");

			for (Throwable t : errors) {
				exception.addSuppressed(t);
			}
			
			throw exception;
		}
	}
}
