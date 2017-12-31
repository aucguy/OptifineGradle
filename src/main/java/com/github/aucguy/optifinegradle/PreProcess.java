package com.github.aucguy.optifinegradle;

import org.gradle.api.tasks.InputFile;
import org.objectweb.asm.ClassVisitor;

public class PreProcess extends AsmProcessingTask
{
    @InputFile
    public Object       inJar;
    
    protected void middle() throws Throwable
    {
        copyJars(inJar);
    }
    
    @Override
    protected byte[] asRead(Object inJar, String name, byte[] data)
    {
        if(name.equals("net/minecraft/server/MinecraftServer.class") || name.equals("bib.class"))
        {
            return processAsm(data, new TransformerFactory()
            {
                @Override
                public ClassVisitor create(ClassVisitor visitor)
                {
                    return new MethodRemover(visitor, name.substring(0, name.length() - 6));
                }
            });
        }
        else
        {
            return data;
        }
    }
}
