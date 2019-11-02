package com.baharmc.runtime.mixins;

import com.baharmc.runtime.SomeGlobals;
import com.baharmc.runtime.util.RollingAverage;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    private int currentTick = 0;

    private long tickSection;

    @Inject(at = @At(value = "HEAD"), method = "run")
    private void bahar$run(CallbackInfo ci) {
        tickSection = System.nanoTime();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void bahar$tick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        final long curTime = System.nanoTime();

        if (++currentTick % RollingAverage.SAMPLE_INTERVAL != 0) {
            return;
        }

        final long diff = curTime - tickSection;
        final BigDecimal currentTps = RollingAverage.TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);

        SomeGlobals.tps1.add(currentTps, diff);
        SomeGlobals.tps5.add(currentTps, diff);
        SomeGlobals.tps15.add(currentTps, diff);
        tickSection = curTime;
    }

}