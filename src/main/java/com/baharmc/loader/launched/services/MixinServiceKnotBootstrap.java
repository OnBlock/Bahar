package com.baharmc.loader.launched.services;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class MixinServiceKnotBootstrap implements IMixinServiceBootstrap {
	@Override
	public String getName() {
		return "Knot";
	}

	@Override
	public String getServiceClassName() {
		return "com.baharmc.loader.launched.services.MixinServiceKnot";
	}

	@Override
	public void bootstrap() {
	}
}
