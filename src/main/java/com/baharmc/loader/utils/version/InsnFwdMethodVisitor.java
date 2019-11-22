package com.baharmc.loader.utils.version;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class InsnFwdMethodVisitor extends MethodVisitor {

	public InsnFwdMethodVisitor() {
		super(Opcodes.ASM7);
	}

	protected abstract void visitAnyInsn();

	@Override
	public void visitLdcInsn(Object value) {
		visitAnyInsn();
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		visitAnyInsn();
	}

	@Override
	public void visitInsn(int opcode) {
		visitAnyInsn();
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		visitAnyInsn();
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		visitAnyInsn();
	}

	@Override
	public void visitTypeInsn(int opcode, java.lang.String type) {
		visitAnyInsn();
	}

	@Override
	public void visitMethodInsn(int opcode, java.lang.String owner, java.lang.String name, java.lang.String descriptor, boolean isInterface) {
		visitAnyInsn();
	}

	@Override
	public void visitInvokeDynamicInsn(java.lang.String name, java.lang.String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
		visitAnyInsn();
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		visitAnyInsn();
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		visitAnyInsn();
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		visitAnyInsn();
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		visitAnyInsn();
	}

	@Override
	public void visitMultiANewArrayInsn(java.lang.String descriptor, int numDimensions) {
		visitAnyInsn();
	}

}