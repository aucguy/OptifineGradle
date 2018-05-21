package com.github.aucguy.optifinegradle.patcher;

import com.github.aucguy.optifinegradle.ReflectHelper;

public final class PatcherConstantsWrapper
{
    private static final String CLASS_NAME = "net.minecraftforge.gradle.patcher.PatcherConstants";
    
    //TODO organize constants
    public static final String REPLACE_PROJECT_CAP_NAME = getStringConstant("REPLACE_PROJECT_CAP_NAME");
    public static final String DIR_PROJECT_CACHE = getStringConstant("DIR_PROJECT_CACHE");
    public static final String TASK_PROJECT_GEN_PATCHES = getStringConstant("TASK_PROJECT_GEN_PATCHES");
    public static final String JAR_DEOBF = getStringConstant("JAR_DEOBF");
    public static final String DIR_LOCAL_CACHE = getStringConstant("DIR_LOCAL_CACHE");
    public static final String JAR_DECOMP = getStringConstant("JAR_DECOMP");
    public static final String TASK_DECOMP = getStringConstant("TASK_DECOMP");
    public static final String TASK_GEN_PROJECTS = getStringConstant("TASK_GEN_PROJECTS");
    public static final String TASK_DEOBF = getStringConstant("TASK_DEOBF");
    public static final String JAR_PROJECT_PATCHED = getStringConstant("JAR_PROJECT_PATCHED");
    public static final String TASK_PROJECT_PATCH = getStringConstant("TASK_PROJECT_PATCH");
    public static final String TASK_PROJECT_EXTRACT_SRC = getStringConstant("TASK_PROJECT_EXTRACT_SRC");
    public static final String TASK_PROJECT_EXTRACT_RES = getStringConstant("TASK_PROJECT_EXTRACT_RES");
    public static final String TASK_PROJECT_REMAP_JAR = getStringConstant("TASK_PROJECT_REMAP_JAR");
    public static final String JAR_DECOMP_POST = getStringConstant("JAR_DECOMP_POST");
    public static final String TASK_PROJECT_RETROMAP = getStringConstant("TASK_PROJECT_RETROMAP");
    public static final String TASK_POST_DECOMP = getStringConstant("TASK_POST_DECOMP");
    
    private static String getStringConstant(String name)
    {
        return (String) ReflectHelper.getField(ReflectHelper.retrieveField(CLASS_NAME, name), null);
    }
}
