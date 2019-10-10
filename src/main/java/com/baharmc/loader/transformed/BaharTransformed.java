package com.baharmc.loader.transformed;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.provided.GameProvided;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.transformer.ClassStripper;
import net.fabricmc.loader.transformer.EnvironmentStrippingData;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class BaharTransformed {

    @NotNull
    private final BaharLaunched launched;

    @NotNull
    private final GameProvided gameProvided;

    @NotNull
    private final String name;

    @NotNull
    private final byte[] bytes;

    public BaharTransformed(@NotNull BaharLaunched launched, @NotNull GameProvided gameProvided, @NotNull String name, @NotNull byte[] bytes) {
        this.launched = launched;
        this.gameProvided = gameProvided;
        this.name = name;
        this.bytes = bytes;
    }

    @NotNull
    public byte[] lwTransformerHook() {
        final byte[] input = gameProvided.transform(name);

        if (input.length != 0) {
            return transform(input);
        } else {
            return transform(bytes);
        }

    }

    @NotNull
    public byte[] transform(@NotNull byte[] bytes) {
        if (name.startsWith("net.minecraft.") || name.indexOf('.') < 0) {
            return bytes;
        }

        final ClassReader classReader = new ClassReader(bytes);
        final ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor visitor = classWriter;
        int visitorCount = 0;

        final EnvironmentStrippingData stripData = new EnvironmentStrippingData(Opcodes.ASM7, EnvType.SERVER.toString());

        classReader.accept(stripData, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

        if (stripData.stripEntireClass()) {
            throw new RuntimeException("Cannot load class " + name + " in environment type " + EnvType.SERVER);
        }

        if (!stripData.isEmpty()) {
            visitor = new ClassStripper(
                Opcodes.ASM7,
                visitor,
                stripData.getStripInterfaces(),
                stripData.getStripFields(),
                stripData.getStripMethods()
            );
            visitorCount++;
        }

        if (visitorCount <= 0) {
            return bytes;
        }

        classReader.accept(visitor, 0);
        return classWriter.toByteArray();
    }

}
