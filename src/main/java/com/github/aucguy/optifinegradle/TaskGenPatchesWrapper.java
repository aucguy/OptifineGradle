package com.github.aucguy.optifinegradle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gradle.api.Task;

import net.minecraftforge.gradle.common.BasePlugin;

public class TaskGenPatchesWrapper
{
    public static final String CLASS_NAME = "net.minecraftforge.gradle.patcher.TaskGenPatches";
    private Method setPatchDirMethod;
    private Method getOriginalSourceMethod;
    private Method addOriginalSourceMethod;
    private Method getChangedSourceMethod;
    private Method addChangedSourceMethod;
    private Field originalsField;
    
    public Task instance;
    
    public TaskGenPatchesWrapper(Task instance)
    {
        this.instance = instance;
        setPatchDirMethod = getMethod("setPatchDir");
        getOriginalSourceMethod = getMethod("getOriginalSource");
        addOriginalSourceMethod = getMethod("addOriginalSource");
        getChangedSourceMethod = getMethod("getChangedSource");
        addChangedSourceMethod = getMethod("addChangedSource");
        setPatchDirMethod = getMethod("setPatchDir");
        originalsField = getField("originals");
    }
    
    private Method getMethod(String name)
    {
        Class<?> clazz = instance.getClass();
        while(clazz != null)
        {
            for(Method method : clazz.getDeclaredMethods())
            {
                if(method.getName().equals(name))
                {
                    method.setAccessible(true);
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw(new RuntimeException("method not found: " + name));
    }
    
    private Field getField(String name)
    {
        Class<?> clazz = instance.getClass();
        while(clazz != null)
        {
            for(Field field : clazz.getDeclaredFields())
            {
                if(field.getName().equals(name))
                {
                    field.setAccessible(true);
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw(new RuntimeException("field not found: " + name));
    }

    public static TaskGenPatchesWrapper makeTask(BasePlugin plugin, String name)
    {
        Class<?> clazz;
        try
        {
            clazz = TaskGenPatchesWrapper.class.getClassLoader().loadClass(CLASS_NAME);
        }
        catch (ClassNotFoundException e)
        {
            throw(new RuntimeException(e));
        }
        return new TaskGenPatchesWrapper(plugin.makeTask(name, clazz));
    }

    public void setPatchDir(Object delayedFile)
    {
        try
        {
            setPatchDirMethod.invoke(instance, delayedFile);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public List<File> getOriginalSource()
    {
        try
        {
            return (List<File>) getOriginalSourceMethod.invoke(instance);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public void addOriginalSource(Object file)
    {
        try
        {
            addOriginalSourceMethod.invoke(instance, file);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public List<File> getChangedSource()
    {
        try
        {
            return (List<File>) getChangedSourceMethod.invoke(instance);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public void addChangedSource(File file)
    {
        try
        {
            addChangedSourceMethod.invoke(instance, file);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public Set<Object> getDependsOn()
    {
        return instance.getDependsOn();
    }

    public void dependsOn(Object dependency)
    {
        instance.dependsOn(dependency);
    }

    public void setOriginals(LinkedList<Object> originals)
    {
        try
        {
            originalsField.set(instance, originals);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw(new RuntimeException(e));
        }
    }
}
