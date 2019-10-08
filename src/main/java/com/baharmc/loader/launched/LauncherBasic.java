package com.baharmc.loader.launched;

import com.baharmc.loader.BaharLaunched;
import com.baharmc.loader.launched.server.KnotServer;
import com.baharmc.loader.utils.UrlUtil;
import io.github.portlek.reflection.clazz.ClassOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;

public class LauncherBasic implements BaharLaunched {

    private static final ClassLoader parentLoader = LauncherBasic.class.getClassLoader();

    @NotNull
    private final String[] args;

    public LauncherBasic(@NotNull String[] args) {
        this.args = args;
    }

    @Override
    public void start() throws Exception {
        final String serverJarPath = ".bahar/server.jar";
        final File serverJar = new File(serverJarPath);

        if (!serverJar.exists()) {
            System.err.println("Could not find Minecraft server .JAR (" + serverJarPath + ")!");
            System.err.println();
            System.err.println("Bahar's server-side launcher expects the server .JAR to be provided.");
            System.err.println();
            System.err.println("Without the official Minecraft server .JAR, Bahar Loader cannot launch.");
            throw new RuntimeException("Searched for '" + serverJar.getName() + "' but could not find it.");
        }

        final ClassLoader newClassLoader = new InjectingURLClassLoader(
            new URL[]{
                LauncherBasic.class.getProtectionDomain().getCodeSource().getLocation(),
                UrlUtil.asUrl(serverJar)
            },
            parentLoader,
            "com.google.common.jimfs."
        );

        Thread.currentThread().setContextClassLoader(newClassLoader);

        new ClassOf(newClassLoader.loadClass("com.baharmc.loader.launched.server.KnotServer"))
            .getMethod("start")
            .of(KnotServer.class)
            .call(new KnotServer(args));
    }

}
