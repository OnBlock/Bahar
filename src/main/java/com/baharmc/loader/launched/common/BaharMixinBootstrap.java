package com.baharmc.loader.launched.common;

import com.baharmc.loader.loaded.BaharLoaded;
import com.baharmc.loader.plugin.LoadedPluginMetaData;
import com.baharmc.loader.plugin.PluginContained;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cactoos.collection.CollectionOf;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.Joined;
import org.cactoos.list.ListOf;
import org.cactoos.list.Mapped;
import org.cactoos.set.SetOf;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Set;

public final class BaharMixinBootstrap {

	private static final Logger LOGGER = LogManager.getFormatterLogger("Bahar|MixinBootstrap");

	private boolean initialized = false;

	@NotNull
	private final BaharLoaded loaded;

	public BaharMixinBootstrap(@NotNull BaharLoaded loaded) {
		this.loaded = loaded;
	}

	public void addConfiguration(@NotNull String configuration) {
		Mixins.addConfiguration(configuration);
	}

	public Set<String> getMixinConfigs() {
		return new SetOf<>(
			new Filtered<>(
				s -> !s.isEmpty(),
				new Joined<>(
					new Mapped<>(
						metaData -> new ListOf<>(
							((LoadedPluginMetaData)metaData).getMixinConfigs()
						),
						new CollectionOf<>(
							new Filtered<>(
								m -> m instanceof LoadedPluginMetaData,
								new Mapped<>(
									PluginContained::getMetadata,
									loaded.getAllPlugins()
								)
							)
						)
					)
				)
			)
		);
	}

	public void init() {
		if (initialized) {
			throw new RuntimeException("BaharMixinBootstrap has already been initialized!");
		}

		MixinBootstrap.init();
		getMixinConfigs().forEach(this::addConfiguration);
		initialized = true;
	}
}
