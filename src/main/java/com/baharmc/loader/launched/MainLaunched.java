package com.baharmc.loader.launched;

import com.baharmc.loader.launched.knot.Knot;
import com.baharmc.loader.utils.BaharLogger;
import com.baharmc.loader.utils.UrlUtil;
import io.github.portlek.reflection.clazz.ClassOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.List;

public class MainLaunched {

    private static final ClassLoader parentLoader = MainLaunched.class.getClassLoader();

    @NotNull
    private final List<String> args;

    public MainLaunched(@NotNull List<String> args) {
        this.args = args;
    }

    public void start() throws Exception {
        final String serverJarPath;

        if (args.size() == 0) {
            serverJarPath = ".bahar/server.jar";
        } else jar:{
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i).equalsIgnoreCase("serverJarPath") && i + 1 < args.size()) {
                    serverJarPath = args.get(i + 1);
                    break jar;
                }
            }
            serverJarPath = ".bahar/server.jar";
        }

        final File serverJar = new File(serverJarPath);

        if (!serverJar.exists()) {
            BaharLogger.error("Could not find Minecraft server .JAR (" + serverJarPath + ")!");
            BaharLogger.printLine();
            BaharLogger.error("Bahar's server-side launcher expects the server .JAR to be provided.");
            BaharLogger.printLine();
            BaharLogger.error("Without the official Minecraft server .JAR, Bahar Loader cannot launch.");
            throw new RuntimeException("Searched for '" + serverJar.getName() + "' but could not find it.");
        }

        final ClassLoader newClassLoader = new InjectingURLClassLoader(
            new URL[]{
                MainLaunched.class.getProtectionDomain().getCodeSource().getLocation(),
                UrlUtil.asUrl(serverJar)
            },
            parentLoader,
            "com.google.common.jimfs."
        );

        Thread.currentThread().setContextClassLoader(newClassLoader);

        new ClassOf(newClassLoader.loadClass("com.baharmc.loader.launched.knot.Knot"))
            .getMethod("init")
            .of(Knot.class)
            .call(new Knot(serverJar));
    }

}
