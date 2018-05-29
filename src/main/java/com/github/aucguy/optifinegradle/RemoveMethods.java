package com.github.aucguy.optifinegradle;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.tasks.InputFile;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RemoveMethods extends AsmProcessingTask
{
    @InputFile
    public Object       inJar;

    @InputFile
    public Object       removedMethods;

    protected Map<String, String[]> removals;
    
    protected void middle() throws Throwable
    {
        Properties properties = new Properties();
        try(InputStream removedMethodsFile = IOManager.openFileSomewhereForReading(this, removedMethods))
        {
            properties.load(removedMethodsFile);
        }
        removals = new HashMap<String, String[]>();
        for(Object key : properties.keySet())
        {
            String[] value = properties.getProperty((String) key).split(",");
            if(value.length != 2)
            {
                throw(new RuntimeException("remove methods file invalid for key " + key));
            }
            for(int i=0; i<value.length; i++)
            {
                value[i] = value[i].trim();
            }
            removals.put(((String) key).trim(), value);
        }
        copyJars(inJar);
    }
    
    @Override
    protected byte[] asRead(Object inJar, String name, byte[] data)
    {
        final String className = name.endsWith(".class") ? name.substring(0, name.length() - 6) : name;
        if(removals.containsKey(className))
        {
            return processAsm(data, visitor -> new ClassVisitor(Opcodes.ASM5, visitor)
            {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
                {
                    if(removals.containsKey(className))
                    {
                        String[] value = removals.get(className);
                        if(name.equals(value[0]) && desc.equals(value[1]))
                        {
                            return null;
                        }
                    }
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
            });
        }
        else
        {
            return data;
        }
    }
}
