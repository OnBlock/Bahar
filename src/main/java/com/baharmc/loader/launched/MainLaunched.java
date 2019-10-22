package com.baharmc.loader.launched;

import com.baharmc.loader.utils.UrlUtil;
import com.baharmc.loader.utils.argument.ArgumentParsed;
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

        serverJarPath = new ArgumentParsed(args).value().getKey().getOrDefault(
            "serverJarPath",
            ".bahar/server.jar"
        );

        final File serverJar = new File(serverJarPath);

        if (!serverJar.exists()) {
            System.err.println("Could not find Minecraft server .JAR (" + serverJarPath + ")!");
            System.err.println();
            System.err.println("Bahar's server-side launcher expects the server .JAR to be provided.");
            System.err.println();
            System.err.println("Without the official Minecraft server .JAR, Bahar Server cannot launch.");
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

        try {
            final Object object = newClassLoader.loadClass("com.baharmc.loader.launched.knot.Knot")
                .getConstructor(List.class, File.class)
                .newInstance(args, serverJar);
            object.getClass()
                .getMethod("start")
                .invoke(object);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
