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
	
	/*
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if(this.name.equals("c")) {
			System.out.println("MD: " + this.name + "." + name);
		}
		if (name.equals("call") && desc.split("\\)")[1].equals("Ljava/lang/Object;")) {
			return null;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}*/
}
