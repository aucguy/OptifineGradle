package com.github.aucguy.optifinegradle;

import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodRemover extends ClassVisitor
{
    public String className;
    protected Map<String, String[]> removals;
    
    public MethodRemover(ClassVisitor cv, String className, Map<String, String[]> removals)
    {
        super(Opcodes.ASM5, cv);
        this.className = className;
        this.removals = removals;
    }
    
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
}
