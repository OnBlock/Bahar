package com.baharmc.runtime.mixins.events;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Final
    @Shadow
    private static Logger LOGGER;

    @Inject(at = @At("HEAD"), method = "shutdown")
    private void bahar$shutdown(CallbackInfo ci) {
        LOGGER.info("Bahar: Stopping all plugins");
        // TODO trigger server stop event
    }

}