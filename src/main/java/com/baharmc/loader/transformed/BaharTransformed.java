package com.baharmc.loader.transformed;

import com.baharmc.loader.provided.GameProvided;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public final class BaharTransformed {

    @NotNull
    private final GameProvided gameProvided;

    @NotNull
    private final String name;

    public BaharTransformed(@NotNull GameProvided gameProvided, @NotNull String name) {
        this.gameProvided = gameProvided;
        this.name = name;
    }

    @NotNull
    public byte[] lwTransformerHook(@NotNull byte[] bytes) {
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
        final EnvironmentStrippingData stripData = new EnvironmentStrippingData(Opcodes.ASM7, "SERVER");

        classReader.accept(stripData, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

        if (stripData.stripEntireClass()) {
            throw new RuntimeException("Cannot load class " + name + " in environment type SERVER");
        }

        if (!stripData.isEmpty()) {
            classReader.accept(new ClassStripper(
                Opcodes.ASM7,
                classWriter,
                stripData.getStripInterfaces(),
                stripData.getStripFields(),
                stripData.getStripMethods()
            ), 0);
            return classWriter.toByteArray();
        }

        return bytes;
    }

}
