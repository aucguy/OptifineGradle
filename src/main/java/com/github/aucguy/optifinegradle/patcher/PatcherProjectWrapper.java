package com.github.aucguy.optifinegradle.patcher;

import java.io.File;
import java.lang.reflect.Method;

import com.github.aucguy.optifinegradle.ReflectHelper;

import groovy.lang.Closure;
import net.minecraftforge.gradle.patcher.PatcherProject;

public class PatcherProjectWrapper
{
    protected static Method getDelayedPatchDirMethod = ReflectHelper.retrieveMethod(PatcherProject.class, "getDelayedPatchDir");
    protected static Method doesGenPatchesMethod = ReflectHelper.retrieveMethod(PatcherProject.class, "doesGenPatches");
    
    @SuppressWarnings("unchecked")
    public static Closure<File> getDelayedPatchDir(PatcherProject patcher)
    {
        return (Closure<File>) ReflectHelper.invoke(getDelayedPatchDirMethod, patcher);
    }

    public static boolean doesGenPatches(PatcherProject patcher)
    {
        return (Boolean) ReflectHelper.invoke(doesGenPatchesMethod, patcher);
    }
}
