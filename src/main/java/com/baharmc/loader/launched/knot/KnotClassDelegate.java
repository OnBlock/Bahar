package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.provided.GameProvided;
import com.baharmc.loader.transformed.BaharTransformed;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

class KnotClassDelegate {

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

    public byte[] loadClassData(String name) {
        if (!transformInitialized) {
            try {
                return getClassByteArray(name, true);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load class file for '" + name + "'!", e);
            }
        }

        if (!name.startsWith("org.apache.logging.log4j")) {
            byte[] input = gameProvided.transform(name);

            if (input.length == 0) {
                try {
                    input = getClassByteArray(name, true);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load class file for '" + name + "'!", e);
                }
            }

            if (input.length != 0) {
                byte[] b = new BaharTransformed(launched, gameProvided, name, input).transform(input);
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
