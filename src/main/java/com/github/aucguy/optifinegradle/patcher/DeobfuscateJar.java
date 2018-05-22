package com.github.aucguy.optifinegradle.patcher;

import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

import com.github.aucguy.optifinegradle.AsmProcessingTask;

public class DeobfuscateJar extends AsmProcessingTask
{	
	public static class ParameterRemappingClassAdapter extends ClassRemapper
	{
	    public ParameterRemappingClassAdapter(ClassVisitor cv, Remapper remapper)
	    {
			super(cv, remapper);
		}
	    
	    @Override
		protected MethodVisitor createMethodRemapper(MethodVisitor mv)
		{
	        return new ParameterRemappingMethodAdapter(mv, this.remapper);
	    }
	}
	
	public static class ParameterRemappingMethodAdapter extends MethodRemapper
	{
		protected ParameterRemappingMethodAdapter(MethodVisitor mv, Remapper remapper)
		{
			super(mv, remapper);
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
		mapping = createRemapper(AsmProcessingTask.loadCsv(getProject().file(methodsCsv), getProject().file(fieldsCsv), getProject().file(paramsCsv)));
		copyJars(inJar);
	}

	@Override
	public byte[] asRead(Object inJar, String name, byte[] data)
	{
		if(name.endsWith(".class"))
		{
			data = AsmProcessingTask.processAsm(data, visitor -> new ParameterRemappingClassAdapter(visitor, mapping));
		}
		return data;
	}
}
