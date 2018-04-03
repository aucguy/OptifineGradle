package com.github.aucguy.optifinegradle.patcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.gradle.api.Task;

public class TaskGenSubProjectsWrapper
{
    static void removeProject(Task createProjects, String name)
    {
        Class<?> clazz = createProjects.getClass();
        Method method = null;
        while(clazz != null)
        {
            try
            {
                method = clazz.getDeclaredMethod("removeProject", String.class);
                break;
            }
            catch(NoSuchMethodException | SecurityException e)
            {
            }
            clazz = clazz.getSuperclass();
        }
        if(method == null)
        {
            throw(new RuntimeException("removeProject method not found"));
        }
        try
        {
            method.setAccessible(true);
            method.invoke(createProjects, name);
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }
}
