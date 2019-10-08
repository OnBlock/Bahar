package com.baharmc.loader.launched.knot;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class BrandedBaharServer {

    @Inject(
            method = "getServerModName", at = @At(
            target = "Lnet/minecraft/server/MinecraftServer;getServerModName()Ljava/lang/String;",
            value = "RETURN"
    ))

    private void Bahar$Brand(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Bahar");
    }

}
