package com.github.aucguy.optifinegradle.patcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.gradle.api.Task;

import com.github.aucguy.optifinegradle.ReflectHelper;

public class TaskGenPatchesWrapper
{
    public static final Class<? extends Task> CLASS = (Class<? extends Task>) ReflectHelper.retrieveClass("net.minecraftforge.gradle.patcher.TaskGenPatches");
    private static final Method setPatchDirMethod = ReflectHelper.retrieveMethod(CLASS, "setPatchDir", Object.class);
    private static final Method getOriginalSourceMethod = ReflectHelper.retrieveMethod(CLASS, "getOriginalSource");
    private static final Method addOriginalSourceMethod = ReflectHelper.retrieveMethod(CLASS, "addOriginalSource", Object.class);
    private static final Method getChangedSourceMethod = ReflectHelper.retrieveMethod(CLASS, "getChangedSource");
    private static final Method addChangedSourceMethod = ReflectHelper.retrieveMethod(CLASS, "addChangedSource", Object.class);
    private static final Field originalsField = ReflectHelper.retrieveField(CLASS, "originals");

    public static void setPatchDir(Task self, Object delayedFile)
    {
        ReflectHelper.invoke(setPatchDirMethod, self, delayedFile);
    }

    public static List<File> getOriginalSource(Task self)
    {
        return (List<File>) ReflectHelper.invoke(getOriginalSourceMethod, self);
    }

    public static void addOriginalSource(Task self, Object file)
    {
        ReflectHelper.invoke(addOriginalSourceMethod, self, file);
    }

    public static List<File> getChangedSource(Task self)
    {
        return (List<File>) ReflectHelper.invoke(getChangedSourceMethod, self);
    }

    public static void addChangedSource(Task self, File file)
    {
        ReflectHelper.invoke(addChangedSourceMethod, self, file);
    }

    public static void setOriginals(Task self, LinkedList<Object> originals)
    {
        ReflectHelper.setField(originalsField, self, originals);
    }
}
