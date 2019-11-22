package com.baharmc.loader.launched.common;

import com.baharmc.loader.loaded.BaharLoaded;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.Joined;
import org.cactoos.list.ListOf;
import org.cactoos.list.Mapped;
import org.cactoos.scalar.And;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public final class BaharMixinBootstrap {

	private boolean initialized = false;

	@NotNull
	private final BaharLoaded loaded;

	public BaharMixinBootstrap(@NotNull BaharLoaded loaded) {
		this.loaded = loaded;
	}

	public void init() {
		if (initialized) {
			throw new RuntimeException("BaharMixinBootstrap has already been initialized!");
		}

		MixinBootstrap.init();
		try {
			new And(
				Mixins::addConfiguration,
				new Filtered<>(
					s -> !s.isEmpty(),
					new Joined<>(
						new Mapped<>(
							pluginContained -> new ListOf<>(
								pluginContained.getMetadata().getMixinConfigs()
							),
							loaded.getAllPlugins()
						)
					)
				)
			).value();
		} catch (Exception e) {
			e.printStackTrace();
		}
		initialized = true;
	}

}