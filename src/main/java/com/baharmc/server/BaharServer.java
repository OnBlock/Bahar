package com.baharmc.server;

import com.baharmc.api.Server;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class BaharServer implements Server {

    @NotNull
    private final MinecraftServer minecraftServer;

    public BaharServer(@NotNull MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    @NotNull
    @Override
    public String getBrandName() {
        return minecraftServer.getServerModName();
    }

}
