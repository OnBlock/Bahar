package com.baharmc.loader.launched;

import com.baharmc.loader.utils.UrlUtil;
import com.baharmc.loader.utils.argument.ArgumentParsed;
import org.graalvm.compiler.lir.alloc.SaveCalleeSaveRegisters;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

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
            System.err.println("Do you want to download the server .jar to .bahar directory? (Type Y/N)");

            final Scanner scanner = new Scanner(System.in);
            final String yesNo = scanner.next();

            if ((yesNo.equalsIgnoreCase("n") || yesNo.equalsIgnoreCase("no")) ||
                !yesNo.equalsIgnoreCase("y") && !yesNo.equalsIgnoreCase("ye") && !yesNo.equalsIgnoreCase("yes")) {
                return;
            }

            final File directory = new File(".bahar");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            return;
        }

        final ClassLoader newClassLoader = new InjectingURLClassLoader(
            new URL[]{
                getClass().getProtectionDomain().getCodeSource().getLocation(),
                UrlUtil.asUrl(serverJar)
            },
            parentLoader,
            "com.google.common.jimfs."
        );

        Thread.currentThread().setContextClassLoader(newClassLoader);

        try {
            final Object object = newClassLoader.loadClass("com.baharmc.loader.launched.knot.Knot")
                .getConstructor(File.class)
                .newInstance(serverJar);
            object.getClass()
                .getMethod("start")
                .invoke(object);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
