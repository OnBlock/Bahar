package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.transformed.BaharTransformed;
import com.baharmc.loader.utils.FileSystemDelegate;
import com.baharmc.loader.utils.FileSystemUtil;
import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

public final class KnotClassDelegate {

    private final Map<String, Metadata> metadataCache = new HashMap<>();

    @NotNull
    private final BaharLaunched launched;

    @NotNull
    private final KnotClassLoaded knotClassLoaded;

    @NotNull
    private final GameProvided gameProvided;

    private MixinTransformer mixinTransformer;

    private boolean transformInitialized = false;

    public KnotClassDelegate(@NotNull BaharLaunched launched, @NotNull KnotClassLoaded knotClassLoaded, @NotNull GameProvided gameProvided) {
        this.launched = launched;
        this.knotClassLoaded = knotClassLoaded;
        this.gameProvided = gameProvided;
    }

    public void initializeTransformers() {
        if (transformInitialized) {
            throw new RuntimeException("Cannot initialize KnotClassDelegate twice!");
        }

        try {
            Constructor<MixinTransformer> constructor = MixinTransformer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            mixinTransformer = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        transformInitialized = true;
    }

    private MixinTransformer getMixinTransformer() {
        assert mixinTransformer != null;
        return mixinTransformer;
    }

    Metadata getMetadata(@NotNull String name, @NotNull URL resourceURL) {
        URL codeSourceURL = null;
        String filename = name.replace('.', '/') + ".class";

        try {
            codeSourceURL = UrlUtil.getSource(filename, resourceURL);
        } catch (UrlConversionException e) {
            System.err.println("Could not find code source for " + resourceURL + ": " + e.getMessage());
        }

        if (codeSourceURL != null) {
            return metadataCache.computeIfAbsent(codeSourceURL.toString(), (codeSourceStr) -> {
                Manifest manifest = null;
                CodeSource codeSource;
                Certificate[] certificates = null;
                URL fCodeSourceUrl = null;

                try {
                    fCodeSourceUrl = new URL(codeSourceStr);
                    Path path = UrlUtil.asPath(fCodeSourceUrl);

                    if (Files.isRegularFile(path)) {
                        URLConnection connection = new URL("jar:" + codeSourceStr + "!/").openConnection();
                        if (connection instanceof JarURLConnection) {
                            manifest = ((JarURLConnection) connection).getManifest();
                            certificates = ((JarURLConnection) connection).getCertificates();
                        }

                        if (manifest == null) {
                            try (FileSystemDelegate jarFs = FileSystemUtil.getJarFileSystem(path, false)) {
                                Path manifestPath = jarFs.get().getPath("META-INF/MANIFEST.MF");
                                if (Files.exists(manifestPath)) {
                                    try (InputStream stream = Files.newInputStream(manifestPath)) {
                                        manifest = new Manifest(stream);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                codeSource = new CodeSource(fCodeSourceUrl, certificates);

                return new Metadata(manifest, codeSource);
            });
        }

        return Metadata.EMPTY;
    }

    public byte[] loadClassData(@NotNull String name) {
        if (!transformInitialized) {
            try {
                return getClassByteArray(name, true);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load class file for '" + name + "'!", e);
            }
        }

        if (!name.startsWith("org.apache.logging.log4j")) {
            byte[] input = gameProvided.getEntryPointTransformed().transform(name);

            if (input.length == 0) {
                try {
                    input = getClassByteArray(name, true);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load class file for '" + name + "'!", e);
                }
            }

            if (input.length != 0) {
                byte[] b = new BaharTransformed(gameProvided, name).transform(input);
                b = getMixinTransformer().transformClassBytes(name, name, b);
                return b;
            }
        }

        return getMixinTransformer().transformClassBytes(name, name, null);
    }

    String getClassFileName(String name) {
        return name.replace('.', '/') + ".class";
    }

    @NotNull
    public byte[] getClassByteArray(String name, boolean skipOriginalLoader) throws IOException {
        String classFile = getClassFileName(name);
        InputStream inputStream = knotClassLoaded.getResourceAsStream(classFile, skipOriginalLoader);

        if (inputStream == null) {
            return new byte[0];
        }

        int a = inputStream.available();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(a < 32 ? 32768 : a);
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        inputStream.close();
        return outputStream.toByteArray();
    }

}
