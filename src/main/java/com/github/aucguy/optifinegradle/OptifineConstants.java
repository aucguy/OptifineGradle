package com.github.aucguy.optifinegradle;

import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants
{
    // normal setup
    // general
    public static final String EXTENSION              = "optifine";
    public static final String GROUP_OPTIFINE         = "Optifine";
    
    // tasks
    public static final String TASK_OPTIFINE_PATCH    = "optifinePatch";
    public static final String TASK_JOIN_JARS         = "joinJars";
    public static final String TASK_DL_PATCHES        = "dlPatches";
    public static final String TASK_ASK_PERMISSION    = "askPermission";
    
    //files
    public static final String JAR_OPTIFINE_FRESH     = "{optifineJar}";
    public static final String JAR_CLIENT_JOINED      = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
    public static final String OBFUSCATED_CLASSES     = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-obfuscatedClasses.txt";
    public static final String DEOBFUSCATED_CLASSES   = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
    public static final String PATCH_ZIP              = "{CACHE_DIR}/com/github/aucguy/optifinegradle/{patchArchive}";
    public static final String PATCH_ARCHIVE          = "{patchArchive}";
    public static final String PATCH_URL              = "{patchUrl}";
    
    // defaults
    public static final String DEFAULT_PATCH_URL      = "https://aucguy.github.io/downloads/optifinegradle/patches/{patchArchive}";
    public static final String DEFAULT_PATCH_ARCHIVE  = "patches-1.8.9-H2-0.zip";
    public static final String DEFAULT_OPTIFINE_JAR   = "{mainDir}/OptiFine_1.8.9_HD_U_H2.jar";
    
    // replacements
    public static final String REPLACE_PATCH_ARCHIVE  = "{patchArchive}";
    public static final String REPLACE_PATCH_URL      = "{patchUrl}";
    public static final String REPLACE_OPTIFINE_JAR   = "{optifineJar}";
    public static final String REPLACE_MAIN_DIR       = "{mainDir}";
    
    // packaging setup
    // tasks
    public static final String TASK_GEN_PATCHES       = "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES       = "zipOptifinePatches";
    public static final String TASK_BUILD_PATCHES     = "buildOptifinePatches";
    
    // files
    public static final String OPTIFINE_PATCH_DIR     = "{BUILD_DIR}/optifine/patchDir";
    public static final String OPTIFINE_PATCH_ZIP     = "{BUILD_DIR}/optifine/{patchArchive}";
}
