package com.github.aucguy.optifinegradle;

import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants
{
    // normal setup
    public static final String EXTENSION              = "optifine";

    public static final String TASK_OPTIFINE_PATCH    = "optifinePatch";
    public static final String TASK_JOIN_JARS         = "joinJars";
    public static final String TASK_DL_PATCHES        = "dlPatches";
    public static final String TASK_ASK_PERMISSION    = "askPermission";
    public static final String JAR_OPTIFINE_FRESH     = "{BUILD_DIR}/OptiFine_1.8.0_HD_U_D5.jar";
    public static final String JAR_CLIENT_JOINED      = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
    public static final String OBFUSCATED_CLASSES     = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-obfuscatedClasses.txt";
    public static final String DEOBFUSCATED_CLASSES   = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
    public static final String PATCH_ZIP              = "{CACHE_DIR}/com/github/aucguy/optifinegradle/{patchArchive}";
    public static final String PATCH_ARCHIVE          = "{patchArchive}";
    public static final String PATCH_URL              = "{patchUrl}";
    public static final String DEFAULT_PATCH_URL      = "http://localhost:4000/downloads/optifinegradle/patches/{patchArchive}";
    public static final String DEFAULT_PATCH_ARCHIVE  = "patches-1.8.0-D5-0.zip";
    public static final String REPLACE_PATCH_ARCHIVE  = "{patchArchive}";
    public static final String REPLACE_PATCH_URL      = "{patchUrl}";

    // patch making
    public static final String TASK_EXTRACT_SOURCES   = "extractOptifineSrc";
    public static final String TASK_EXTRACT_RESOURCES = "extractOptifineResc";
    public static final String TASK_GEN_PATCHES       = "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES       = "zipOptifinePatches";
    public static final String TASK_PATCH_ENVIRO      = "setupPatchEnviro";
    public static final String TASK_CREATE_PATCHES    = "createOptifinePatches";
    public static final String GROUP_OPTIFINE         = "Optifine";
    public static final String SRC_DIR                = "src/main/java";
    public static final String RESC_DIR               = "src/main/resources";
    public static final String GEN_PATCH_DIR          = "build/optifine/patchDir";
    public static final String PATCH_ZIP_DIR          = "build/optifine/";
    public static final String PATCH_ZIP_NAME         = "patches";
}
