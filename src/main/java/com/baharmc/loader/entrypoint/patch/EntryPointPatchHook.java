package com.baharmc.loader.entrypoint.patch;

import com.baharmc.loader.entrypoint.EntryPointPatch;
import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ListIterator;
import java.util.function.Consumer;

public final class EntryPointPatchHook extends EntryPointPatch {

    public EntryPointPatchHook(@NotNull EntryPointTransformed transformed) {
        super(transformed);
    }

    @Override
    public void process(@NotNull BaharLaunched launched, @NotNull Consumer<ClassNode> classEmitter) {
        final String entryPoint = launched.getEntryPoint();

        if (!entryPoint.startsWith("net.minecraft.") && !entryPoint.startsWith("com.mojang.")) {
            return;
        }

        try {
            String gameEntrypoint = null;
            boolean serverHasFile = true;
            ClassNode mainClass = loadClass(launched, entryPoint);

            if (mainClass == null) {
                throw new RuntimeException("Could not load main class " + entryPoint + "!");
            }

            final MethodNode mainMethod = findMethod(
                mainClass,
                method -> method.name.equals("main") &&
                    method.desc.equals("([Ljava/lang/String;)V") &&
                    isPublicStatic(method.access)
            );

            if (mainMethod == null) {
                throw new RuntimeException("Could not find main method in " + entryPoint + "!");
            }

            final MethodInsnNode newGameInsn = (MethodInsnNode) findInsn(
                mainMethod,
                insn -> insn.getOpcode() == Opcodes.INVOKESPECIAL &&
                    ((MethodInsnNode) insn).name.equals("<init>") &&
                    ((MethodInsnNode) insn).owner.equals(mainClass.name),
                false
            );

            if (newGameInsn != null) {
                gameEntrypoint = newGameInsn.owner.replace('/', '.');
                serverHasFile = newGameInsn.desc.startsWith("(Ljava/io/File;");
            }

            if (gameEntrypoint == null) {
                final MethodInsnNode newGameInsnNode = (MethodInsnNode) findInsn(mainMethod,
                   insn -> insn.getOpcode() == Opcodes.INVOKESPECIAL &&
                       ((MethodInsnNode) insn).name.equals("<init>") &&
                       ((MethodInsnNode) insn).desc.startsWith("(Ljava/io/File;"),
                    true
                );

                if (newGameInsnNode != null) {
                    gameEntrypoint = newGameInsnNode.owner.replace('/', '.');
                }
            }

            if (gameEntrypoint == null) {
                throw new RuntimeException("Could not find game constructor in " + entryPoint + "!");
            }

            debug("Found game constructor: " + entryPoint + " -> " + gameEntrypoint);

            final ClassNode gameClass = gameEntrypoint.equals(entryPoint) ? mainClass : loadClass(launched, gameEntrypoint);

            if (gameClass == null) {
                throw new RuntimeException("Could not load game class " + gameEntrypoint + "!");
            }

            MethodNode gameMethod = null;
            int gameMethodQuality = 0;

            for (MethodNode gmCandidate : gameClass.methods) {
                if (gmCandidate.name.equals("<init>") && gameMethodQuality < 1) {
                    gameMethod = gmCandidate;
                    gameMethodQuality = 1;
                }
            }

            if (gameMethod == null) {
                throw new RuntimeException("Could not find game constructor method in " + gameClass.name + "!");
            }

            debug("Patching game constructor " + gameMethod.name + gameMethod.desc);

            final ListIterator<AbstractInsnNode> it = gameMethod.instructions.iterator();

            moveBefore(it, Opcodes.RETURN);

            if (serverHasFile) {
                it.add(new VarInsnNode(Opcodes.ALOAD, 1));
            } else {
                it.add(new InsnNode(Opcodes.ACONST_NULL));
            }

            finishEntryPoint(it);

            if (gameClass != mainClass) {
                classEmitter.accept(gameClass);
            } else {
                classEmitter.accept(mainClass);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void finishEntryPoint(@NotNull ListIterator<AbstractInsnNode> it) {
        it.add(new VarInsnNode(Opcodes.ALOAD, 0));
        it.add(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "com/baharmc/loader/entrypoint/hooks/EntryPointServer.java",
            "start",
            "(Ljava/io/File;Ljava/lang/Object;)V",
            false)
        );
    }

}
