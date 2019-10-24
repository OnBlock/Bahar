package com.baharmc.server.mixins;

import com.baharmc.api.Bahar;
import com.baharmc.server.BaharServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.ServerCommandOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandOutput.class)
public abstract class ServerCommandOutputMixin {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void bahar$init(MinecraftServer minecraftServer, CallbackInfo ci) {
        Bahar.setServer(
            new BaharServer(
                minecraftServer
            )
        );
    }

}