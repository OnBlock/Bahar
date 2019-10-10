package com.baharmc.loader.launched;

import com.baharmc.loader.utils.UrlUtil;
import com.baharmc.loader.utils.argument.ArgumentParsed;
import io.github.portlek.reflection.RefClass;
import io.github.portlek.reflection.clazz.ClassOf;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class MainLaunched {

    private static final Logger LOGGER = Logger.getLogger("Bahar");

    private static final ClassLoader parentLoader = MainLaunched.class.getClassLoader();

    @NotNull
    private final List<String> args;

    public MainLaunched(@NotNull List<String> args) {
        this.args = args;
    }

    public void start() throws Exception {
        final String serverJarPath;

        serverJarPath = new ArgumentParsed(args).value().getKey().getOrDefault(
            "serverJarPath",
            ".bahar/server.jar"
        );

        final File serverJar = new File(serverJarPath);

        if (!serverJar.exists()) {
            LOGGER.severe("Could not find Minecraft server .JAR (" + serverJarPath + ")!");
            System.out.println();
            LOGGER.severe("Bahar's server-side launcher expects the server .JAR to be provided.");
            System.out.println();
            LOGGER.severe("Without the official Minecraft server .JAR, Bahar Server cannot launch.");
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

        RefClass refClass = new ClassOf(newClassLoader.loadClass("com.baharmc.loader.launched.Knot"));

        Object knot = refClass.getConstructor(File.class, Logger.class).create(new Knot(LOGGER, args, serverJar), LOGGER, args, serverJar);
        refClass.getMethod("init").of(knot).call(new Knot(LOGGER, args, serverJar));
    }

}
