package com.github.aucguy.optifinegradle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectHelper
{
    public static Class<?> retrieveClass(String name)
    {
        try
        {
            return ReflectHelper.class.getClassLoader().loadClass(name);
        }
        catch (ClassNotFoundException e)
        {
            throw(new RuntimeException(name + " not found"));
        }
    }

    public static Method retrieveMethod(Class<?> clazz, String name, Class<?> ... parameters)
    {
        Method method = null;
        while(clazz != null && method == null)
        {
            try
            {
                method = clazz.getDeclaredMethod(name, parameters);
            }
            catch(NoSuchMethodException | SecurityException e)
            {
            }
            clazz = clazz.getSuperclass();
        }
        if(method == null)
        {
            throw(new RuntimeException(name + " method not found on class " + clazz.getName()));
        }
        method.setAccessible(true);
        return method;
    }

    public static Method retrieveMethod(String className, String name, Class<?> ... parameters)
    {
        return retrieveMethod(retrieveClass(className), name, parameters);
    }
    
    public static Object invoke(Method method, Object self, Object ... parameters)
    {
        try
        {
            return method.invoke(self, parameters);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public static Field retrieveField(Class<?> clazz, String name)
    {
        Field field = null;
        while(clazz != null && field == null)
        {
            try
            {
                field = clazz.getDeclaredField(name);
            }
            catch(NoSuchFieldException | SecurityException e)
            {
            }
            clazz = clazz.getSuperclass();
        }
        if(field == null)
        {
            throw(new RuntimeException(name + " method not found on class " + clazz.getName()));
        }
        field.setAccessible(true);
        return field;
    }

    public static Field retrieveField(String className, String name)
    {
        return retrieveField(retrieveClass(className), name);
    }

    public static Object getField(Field field, Object self)
    {
        try
        {
            return field.get(self);
        }
        catch (IllegalAccessException | IllegalArgumentException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public static void setField(Field field, Object self, Object value)
    {
        try
        {
            field.set(self, value);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw(new RuntimeException(e));
        }
    }
}
