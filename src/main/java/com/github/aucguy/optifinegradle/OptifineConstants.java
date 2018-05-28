package com.github.aucguy.optifinegradle;

import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants
{
    // replacements
    public static final String REPLACE_OPTIFINE_VERSION = "{optifineVersion}";
    public static final String REPLACE_PATCH_ARCHIVE    = "{patchArchive}";
    public static final String REPLACE_PATCH_URL        = "{patchUrl}";
    public static final String REPLACE_OPTIFINE_JAR     = "{optifineJar}";
    public static final String REPLACE_MAIN_DIR         = "{mainDir}";

    // generics
    public static final String EXTENSION                = "optifine";
    public static final String GROUP_OPTIFINE           = "Optifine";
    public static final String OPTIFINE_CACHE           = "{CACHE_DIR}/com/github/aucguy/optifinegradle/";
    
    // tasks
    public static final String TASK_DIFF_OPTIFINE       = "diffOptifine";
    public static final String TASK_DIFF_EXEC           = "diffExecOptifine";
    public static final String TASK_JOIN_JARS           = "joinJars";
    public static final String TASK_EXTRACT_CONFIG      = "extractConfig";
    public static final String TASK_REMOVE_METHODS          = "removeMethods";
    public static final String TASK_REMOVE_EXTRAS       = "removeExtras";
    public static final String TASK_FILTER_MCP_PATCHES  = "filterMcpPatches";

    //files
    public static final String JAR_OPTIFINE_FRESH       = REPLACE_OPTIFINE_JAR;
    public static final String JAR_CLIENT_JOINED        = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/joined.jar";
    public static final String JAR_OPTIFINE_DIFFED      = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/diffed.jar";
    public static final String DEOBFUSCATED_CLASSES     = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/deobfuscatedClasses.txt";
    public static final String JAR_METHODS_REMOVED           = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/methodsRemoved.jar";

    //patch archive
    public static final String PATCH_EXTRACT            = OPTIFINE_CACHE + "patchExtracts/" + REPLACE_OPTIFINE_VERSION + "/";
    public static final String CONFIG_DIR               = PATCH_EXTRACT + "config/";
    public static final String RENAMES_FILE             = CONFIG_DIR + "optifine-renames.properties";
    public static final String REMOVED_METHODS_FILE     = CONFIG_DIR +  "optifine-removed-methods.properties";
    public static final String EXTRA_PATCH_EXCL_FILE    = CONFIG_DIR + "extra-patch-exclusions.txt";

    // defaults
    public static final String OPTIFINE_VERSION         = "D2";
    public static final String DEFAULT_OPTIFINE_VERSION = "1.12.2_HD_U_" + OPTIFINE_VERSION;
    public static final String DEFAULT_PATCH_URL        = "https://aucguy.github.io/downloads/optifinegradle/patches/" + REPLACE_PATCH_ARCHIVE;
    public static final String DEFAULT_PATCH_ARCHIVE    = "patches-" + REPLACE_OPTIFINE_VERSION + ".zip";
    public static final String DEFAULT_OPTIFINE_JAR     = "OptiFine_" + REPLACE_OPTIFINE_VERSION + ".jar";
}