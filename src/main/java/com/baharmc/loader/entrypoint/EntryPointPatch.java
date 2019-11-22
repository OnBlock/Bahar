package com.baharmc.loader.entrypoint;

import com.baharmc.loader.launched.common.BaharLaunched;
import com.baharmc.loader.transformed.EntryPointTransformed;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class EntryPointPatch {

    @NotNull
    private final EntryPointTransformed transformed;

    public EntryPointPatch(@NotNull EntryPointTransformed transformed) {
        this.transformed = transformed;
    }

    protected void debug(String s) {
        transformed.getLogger().fine(s);
    }

    protected void warn(String s) {
        transformed.getLogger().warning(s);
    }

    protected ClassNode loadClass(@NotNull BaharLaunched launched, @NotNull String className) throws IOException {
        return transformed.loadClass(launched, className);
    }

    protected FieldNode findField(@NotNull ClassNode node, @NotNull Predicate<FieldNode> predicate) {
        return node.fields.stream().filter(predicate).findAny().orElse(null);
    }

    protected List<FieldNode> findFields(@NotNull ClassNode node, @NotNull Predicate<FieldNode> predicate) {
        return node.fields.stream().filter(predicate).collect(Collectors.toList());
    }

    protected MethodNode findMethod(@NotNull ClassNode node, @NotNull Predicate<MethodNode> predicate) {
        return node.methods.stream().filter(predicate).findAny().orElse(null);
    }

    protected AbstractInsnNode findInsn(@NotNull MethodNode node, @NotNull Predicate<AbstractInsnNode> predicate, boolean last) {
        if (last) {
            for (int i = node.instructions.size() - 1; i >= 0; i--) {
                AbstractInsnNode insn = node.instructions.get(i);
                if (predicate.test(insn)) {
                    return insn;
                }
            }
        } else {
            for (int i = 0; i < node.instructions.size(); i++) {
                AbstractInsnNode insn = node.instructions.get(i);
                if (predicate.test(insn)) {
                    return insn;
                }
            }
        }

        return null;
    }

    protected void moveAfter(@NotNull ListIterator<AbstractInsnNode> it, int opcode) {
        while (it.hasNext()) {
            AbstractInsnNode node = it.next();
            if (node.getOpcode() == opcode) {
                break;
            }
        }
    }

    protected void moveBefore(@NotNull ListIterator<AbstractInsnNode> it, int opcode) {
        moveAfter(it, opcode);
        it.previous();
    }

    protected void moveAfter(@NotNull ListIterator<AbstractInsnNode> it, @NotNull AbstractInsnNode targetNode) {
        while (it.hasNext()) {
            AbstractInsnNode node = it.next();
            if (node == targetNode) {
                break;
            }
        }
    }

    protected void moveBefore(@NotNull ListIterator<AbstractInsnNode> it, @NotNull AbstractInsnNode targetNode) {
        moveAfter(it, targetNode);
        it.previous();
    }

    protected boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) != 0);
    }

    protected boolean isPublicStatic(int access) {
        return ((access & 0x0F) == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC));
    }

    protected boolean isPublicInstance(int access) {
        return ((access & 0x0F) == (Opcodes.ACC_PUBLIC));
    }

    public abstract void process(@NotNull BaharLaunched launched, @NotNull Consumer<ClassNode> classEmitter);

}
