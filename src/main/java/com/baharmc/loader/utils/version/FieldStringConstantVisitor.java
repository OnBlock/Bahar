package com.baharmc.loader.utils.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class FieldStringConstantVisitor extends ClassVisitor implements Analyzed {

    @NotNull
    private final String fieldName;

    @NotNull
    private String className = "";

    @NotNull
    private String result = "";

    public FieldStringConstantVisitor(@NotNull String fieldName) {
        super(Opcodes.ASM7);

        this.fieldName = fieldName;
    }

    @NotNull
    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
    }

    @Override
    @Nullable
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (result.isEmpty() && name.equals(fieldName) && descriptor.equals(McVersion.STRING_DESC) && value instanceof String) {
            result = (String) value;
        }

        return null;
    }

    @Override
    @Nullable
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (!result.isEmpty() || !name.equals("<clinit>")) {
            return null;
        }

        return new InsnFwdMethodVisitor() {

            @NotNull
            private String lastLdc = "";

            @Override
            public void visitLdcInsn(Object value) {
                String str;

                if (value instanceof String && McVersion.VERSION_PATTERN.matcher((str = (String) value)).matches()) {
                    lastLdc = str;
                } else {
                    lastLdc = "";
                }
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                if (result.isEmpty()
                    && !lastLdc.isEmpty()
                    && opcode == Opcodes.PUTSTATIC
                    && owner.equals(className)
                    && name.equals(fieldName)
                    && descriptor.equals(McVersion.STRING_DESC)) {
                    result = lastLdc;
                }

                lastLdc = "";
            }

            @Override
            protected void visitAnyInsn() {
                lastLdc = "";
            }

        };
    }

}