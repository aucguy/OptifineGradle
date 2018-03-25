package com.github.aucguy.optifinegradle.patcher;

import java.lang.reflect.Field;

public final class PatcherConstantsWrapper
{
    private static final String CLASS_NAME = "net.minecraftforge.gradle.patcher.PatcherConstants";
    private static Class<?> clazz = null;
    
    public static final String REPLACE_PROJECT_CAP_NAME = getStringConstant("REPLACE_PROJECT_CAP_NAME");
    public static final String DIR_PROJECT_CACHE = getStringConstant("DIR_PROJECT_CACHE");
    public static final String TASK_PROJECT_GEN_PATCHES = getStringConstant("TASK_PROJECT_GEN_PATCHES");
    public static final String JAR_DEOBF = getStringConstant("JAR_DEOBF");
    public static final String DIR_LOCAL_CACHE = getStringConstant("DIR_LOCAL_CACHE");
    
    private static String getStringConstant(String name)
    {
        if(clazz == null)
        {
            try
            {
                clazz = PatcherConstantsWrapper.class.getClassLoader().loadClass(CLASS_NAME);
            }
            catch (ClassNotFoundException e)
            {
                throw(new RuntimeException(e));
            }
        }
        try
        {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (String) field.get(null);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
        {
            throw(new RuntimeException(e));
        }
    }
}
