package com.github.aucguy.optifinegradle.patcher;

import java.io.IOException;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.aucguy.optifinegradle.AsmProcessingTask;

public class GetCallersTask extends AsmProcessingTask
{
	protected static class HierarchyClassVisitor extends ClassVisitor
	{
		String searchMethod;
		String owner;
		
		public HierarchyClassVisitor(ClassVisitor cv, String searchMethod)
		{
			super(Opcodes.ASM5, cv);
			this.searchMethod = searchMethod;
		}
		
		@Override
		public void visit(int version, int access, String name, String signature,
	            String superName, String[] interfaces)
		{
			owner = name;
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
	            String signature, String[] exceptions) {
			return new HierarchyMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), 
					searchMethod, owner + "." + name + desc);
		}
	}
	
	protected static class HierarchyMethodVisitor extends MethodVisitor
	{
		protected String searchMethod;
		protected String methodName;
		
		protected HierarchyMethodVisitor(MethodVisitor mv, String searchMethod, String methodName)
		{
			super(Opcodes.ASM5, mv);
			this.searchMethod = searchMethod;
			this.methodName = methodName;
		}
		
		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
	            String desc, boolean itf)
		{
			if(searchMethod.equals(owner + "." + name + desc))
			{
				System.out.println("found call: " + methodName);
			}
		}
	}
	
	@InputFile
	public Object inJar;
	
	@Input
	public String searchMethod;
	
	@TaskAction
	public void doAction() throws IOException
	{
		copyJars(getProject().file(inJar));
	}

	@Override
	protected byte[] asRead(Object inJar, String name, byte[] data)
	{
		if(name.endsWith(".class"))
		{
			ClassReader reader = new ClassReader(data);
			ClassVisitor visitor = new HierarchyClassVisitor(null, searchMethod);
			reader.accept(visitor, 0);
		}
		return data;
	}
}
