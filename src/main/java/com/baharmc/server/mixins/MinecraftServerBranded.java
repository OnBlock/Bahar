package com.baharmc.server.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerBranded {

    @Shadow public abstract void setOnlineMode(boolean boolean_1);

    @Shadow public abstract boolean shouldTrackOutput();

    @Inject(
        method = "getServerModName",
        at = @At(
            target = "Lnet/minecraft/server/MinecraftServer;getServerModName()Ljava/lang/String;",
            value = "RETURN"
        ),
        cancellable = true
    )
    private void Bahar$Brand(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Bahar");
    }

}