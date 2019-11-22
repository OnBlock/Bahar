package com.baharmc.loader.utils.version;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class MethodConstantRetVisited extends ClassVisitor implements Analyzed {

    @NotNull
    private final String methodName;

    @NotNull
    private String result = "";

    public MethodConstantRetVisited(@NotNull String methodName) {
        super(Opcodes.ASM7);

        this.methodName = methodName;
    }

    @NotNull
    @Override
    public String getResult() {
        return result;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (!result.isEmpty()
            || !methodName.isEmpty() && !name.equals(methodName)
            || !descriptor.endsWith(McVersion.STRING_DESC)
            || descriptor.charAt(descriptor.length() - McVersion.STRING_DESC.length() - 1) != ')') {
            return null;
        }

        return new InsnFwdMethodVisitor() {
            @Override
            public void visitLdcInsn(Object value) {
                String str;

                if (value instanceof String && McVersion.VERSION_PATTERN.matcher(str = (String) value).matches()) {
                    lastLdc = str;
                } else {
                    lastLdc = null;
                }
            }

            @Override
            public void visitInsn(int opcode) {
                if (result.isEmpty()
                    && lastLdc != null
                    && opcode == Opcodes.ARETURN) {
                    result = lastLdc;
                }

                lastLdc = null;
            }

            @Override
            protected void visitAnyInsn() {
                lastLdc = null;
            }

            String lastLdc;
        };
    }

}
