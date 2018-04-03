package com.github.aucguy.optifinegradle.patcher;

import java.io.File;
import java.lang.reflect.Method;

import com.github.aucguy.optifinegradle.ReflectHelper;

import groovy.lang.Closure;
import net.minecraftforge.gradle.patcher.PatcherProject;

public class PatcherProjectWrapper
{
    protected static Method getDelayedPatchDirMethod = ReflectHelper.getMethod(PatcherProject.class, "getDelayedPatchDir");
    
    public static Closure<File> getDelayedPatchDir(PatcherProject patcher)
    {
        return (Closure<File>) ReflectHelper.invoke(getDelayedPatchDirMethod, patcher);
    }
}
