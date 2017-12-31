package com.github.aucguy.optifinegradle;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodRemover extends ClassVisitor
{
    public String className;
    
    public MethodRemover(ClassVisitor cv, String className)
    {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
    {
        if(this.className.equals("net/minecraft/server/MinecraftServer") && name.equals("aM") && desc.equals("()V"))
        {
            return null;
        }
        else if(this.className.equals("bib") && name.equals("a") && desc.equals("()V"))
        {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
