package com.baharmc.loader.transformed;

import com.baharmc.loader.entrypoint.EntryPointPatch;
import com.baharmc.loader.launched.BaharLaunched;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class EntryPointTransformerBasic implements EntryPointTransformed {

    public final Logger logger = Logger.getLogger("Bahar|EntryPointTransformed");

    @NotNull
    private final List<EntryPointPatch> patches;

    private final Map<String, byte[]> patchedClasses = new HashMap<>();

    private boolean entryPointsLocated = false;

    public EntryPointTransformerBasic(@NotNull Function<EntryPointTransformed, List<EntryPointPatch>> patches) {
        this.patches = patches.apply(this);
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void locateEntryPoints(@NotNull BaharLaunched launched) {
        if (entryPointsLocated) {
            return;
        }

        entryPointsLocated = true;

        patches.forEach(e -> e.process(launched, this::addPatchedClass));
        launched.getLogger().debug("[EntryPointTransformerBasic] Patched " +
            (patchedClasses.size() == 1
                ? "1 class."
                : patchedClasses.size() + " classes.")
        );
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String target) {
        return patchedClasses.getOrDefault(target, new byte[0]);
    }

    private void addPatchedClass(ClassNode node) {
        final String key = node.name.replace('/', '.');

        if (patchedClasses.containsKey(key)) {
            throw new RuntimeException("Duplicate addPatchedClasses call: " + key);
        }

        final ClassWriter writer = new ClassWriter(0);

        node.accept(writer);
        patchedClasses.put(key, writer.toByteArray());
    }

    public ClassNode loadClass(@NotNull BaharLaunched launched, @NotNull String className) throws IOException {
        final byte[] data = patchedClasses.containsKey(className)
            ? patchedClasses.get(className)
            : launched.getClassByteArray(className);

        if (data == null) {
            return null;
        }

        ClassReader reader = new ClassReader(data);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        return node;
    }

}
