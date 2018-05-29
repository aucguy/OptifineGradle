package com.github.aucguy.optifinegradle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;

import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;

public abstract class AsmProcessingTask extends CachedTask
{
	public static class ParameterRemapper extends Remapper
	{
		public String mapParameterName(String name)
		{
			return name;
		}
	}
	
	public static class CustomRemapper extends ParameterRemapper
	{
	    private final Map<String, String> mapping;

	    public CustomRemapper(Map<String, String> mapping)
	    {
	        this.mapping = mapping;
	    }

	    @Override
	    public String mapMethodName(String owner, String name, String desc)
	    {
	        String s = map(name);
	        return s == null ? name : s;
	    }

	    @Override
	    public String mapInvokeDynamicMethodName(String name, String desc)
	    {
	        String s = map(name);
	        return s == null ? name : s;
	    }

	    @Override
	    public String mapFieldName(String owner, String name, String desc)
	    {
	        String s = map(name);
	        return s == null ? name : s;
	    }
	    
	    @Override
	    public String mapParameterName(String name)
	    {
	        String s = map(name);
	        return s == null ? name : s;
	    }

	    @Override
	    public String map(String key)
	    {
	        return mapping.get(key);
	    }
	}
	
	@OutputFile
	@Cached
	@Optional
	public Object outJar;

	@TaskAction
	public void doAction() throws Throwable
	{
		try
		{
			this.middle();
		}
		finally
		{
		}
	}
	
	public void copyJars(Object ... inJars) throws IOException
	{
		try(ZipOutputStream output = outJar == null ? null : IOManager.openZipForWriting(this, outJar))
		{
			Set<String> copiedEntries = new HashSet<String>();

			for (Object inJar : inJars)
			{
			    try(ZipFile input = IOManager.openZipForReading(this, inJar))
			    {
        			for (Enumeration<ZipEntry> iter = (Enumeration<ZipEntry>) input.entries(); iter.hasMoreElements();)
        			{
        				ZipEntry entry = iter.nextElement();
        				String name = entry.getName();
        				if(entry.isDirectory() || copiedEntries.contains(name) || !acceptsFile(name)) continue;
        					
        				copiedEntries.add(name);
        				
        				byte[] data;
        				//TODO necessary BufferedInputStream?
        				try(InputStream stream = new BufferedInputStream(IOManager.openFileInZipForReading(this, input, entry)))
        				{
        				    data = IOManager.readAll(stream);
        				}
                        
                        data = asRead(inJar, name, data);
                        if(output != null)
                        {
                        	output.putNextEntry(new JarEntry(name));
                        	output.write(data);
                        	output.closeEntry();
                        }
        			}
			    }
			}
		}
	}
	
	protected void middle() throws Throwable
	{
	}
	
	protected abstract byte[] asRead(Object inJar, String name, byte[] data);
	
	protected boolean acceptsFile(String name)
	{
		return true;
	}
	
	public static byte[] processAsm(byte[] data, Function<ClassVisitor, ClassVisitor> factory)
	{
        ClassReader reader = new ClassReader(data);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor transformer = factory.apply(writer);
        reader.accept(transformer, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
	}
}
