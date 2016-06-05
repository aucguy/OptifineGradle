package com.github.aucguy.optifinegradle.patcher;

import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.RemappingMethodAdapter;

import com.github.aucguy.optifinegradle.AsmProcessingTask;

public class DeobfuscateJar extends AsmProcessingTask
{	
	protected class CustomTransformerFactory extends AsmProcessingTask.TransformerFactory
	{	
		@Override
		public ClassVisitor create(ClassVisitor visitor)
		{
			return new ParameterRemappingClassAdapter(visitor, mapping);
		}	
	}
	
	public static class ParameterRemappingClassAdapter extends RemappingClassAdapter
	{
	    public ParameterRemappingClassAdapter(ClassVisitor cv, Remapper remapper)
	    {
			super(cv, remapper);
		}
	    
	    @Override
		protected MethodVisitor createRemappingMethodAdapter(int access,
	            String newDesc, MethodVisitor mv)
		{
	        return new ParameterRemappingMethodAdapter(access, newDesc, mv, remapper);
	    }
	}
	
	public static class ParameterRemappingMethodAdapter extends RemappingMethodAdapter
	{
		protected ParameterRemappingMethodAdapter(int access, String desc, MethodVisitor mv, Remapper remapper)
		{
			super(Opcodes.ASM5, access, desc, mv, remapper);
		}
		
		@Override
		public void visitParameter(String name, int access)
		{
			if(remapper instanceof ParameterRemapper)
			{
				name = ((ParameterRemapper) remapper).mapParameterName(name);
			}
			super.visitParameter(name, access);
		}
		
		@Override
	    public void visitLocalVariable(String name, String desc, String signature,
	            Label start, Label end, int index) {
			if(remapper instanceof ParameterRemapper)
			{
				name = ((ParameterRemapper) remapper).mapParameterName(name);
			}
			super.visitLocalVariable(name, desc, signature, start, end, index);
		}

	}
	
	@InputFile
	public Object inJar;
	
	@InputFile
	public Object methodsCsv;
	
	@InputFile
	public Object fieldsCsv;
	
	@InputFile
	public Object paramsCsv;
	
	protected Remapper mapping;

	@TaskAction
	public void doAction() throws Exception
	{
		mapping = AsmProcessingTask.loadCsv(getProject().file(methodsCsv), getProject().file(fieldsCsv), getProject().file(paramsCsv));
		copyJars(inJar);
	}

	@Override
	public byte[] asRead(String name, byte[] data)
	{
		if(name.endsWith(".class"))
		{
			data = AsmProcessingTask.processAsm(data, new CustomTransformerFactory());
		}
		return data;
	}
}
