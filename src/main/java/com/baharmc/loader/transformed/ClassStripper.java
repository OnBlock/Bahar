package com.baharmc.loader.transformed;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassStripper extends ClassVisitor {

	private final Collection<String> stripInterfaces;
	private final Collection<String> stripFields;
	private final Collection<String> stripMethods;

	public ClassStripper(int api, ClassVisitor classVisitor, Collection<String> stripInterfaces, Collection<String> stripFields, Collection<String> stripMethods) {
		super(api, classVisitor);
		this.stripInterfaces = stripInterfaces;
		this.stripFields = stripFields;
		this.stripMethods = stripMethods;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (!this.stripInterfaces.isEmpty()) {
			List<String> interfacesList = new ArrayList<>();
			for (String itf : interfaces) {
				if (!this.stripInterfaces.contains(itf)) {
					interfacesList.add(itf);
				}
			}
			interfaces = interfacesList.toArray(new String[0]);
		}
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
		if (stripFields.contains(name + descriptor)) return null;
		return super.visitField(access, name, descriptor, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		if (stripMethods.contains(name + descriptor)) return null;
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
}
