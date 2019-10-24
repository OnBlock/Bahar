package com.baharmc.loader.utils.version;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodConstantVisitor extends ClassVisitor implements Analyzed {

    @NotNull
    private final String methodName;

    @NotNull
    private String result = "";

    private boolean foundInMethodHint;

    public MethodConstantVisitor(@NotNull String methodName) {
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
        final boolean isRequestedMethod = name.equals(methodName);

        if (!result.isEmpty() && !isRequestedMethod) {
            return null;
        }

        return new MethodVisitor(Opcodes.ASM7) {
            @Override
            public void visitLdcInsn(Object value) {
                String str;

                if ((result.isEmpty() || !foundInMethodHint && isRequestedMethod)
                    && value instanceof String
                    && McVersion.VERSION_PATTERN.matcher(str = (String) value).matches()) {
                    result = str;
                    foundInMethodHint = isRequestedMethod;
                }
            }
        };
    }

}
