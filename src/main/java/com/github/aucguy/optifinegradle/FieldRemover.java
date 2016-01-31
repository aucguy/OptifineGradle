package com.github.aucguy.optifinegradle;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * removes '__OBFID' fields
 */
public class FieldRemover extends ClassVisitor {
	String name;
	
	public FieldRemover(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (name.equals("__OBFID")) {
			return null;
		}
		return super.visitField(access, name, desc, signature, value);
	}
}
