package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_PROJECT_CACHE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.REPLACE_PROJECT_CAP_NAME;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_LOCAL_CACHE;
import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.DIR_USERDEV;

/**
 * random constants
 */
public class OptifineConstants
{
    // replacements
    public static final String REPLACE_OPTIFINE_VERSION = "{optifineVersion}";
    public static final String REPLACE_PATCH_ARCHIVE  = "{patchArchive}";
    public static final String REPLACE_PATCH_URL      = "{patchUrl}";
    public static final String REPLACE_OPTIFINE_JAR   = "{optifineJar}";
    public static final String REPLACE_MAIN_DIR       = "{mainDir}";

    // normal setup
    // general
    public static final String EXTENSION              = "optifine";
    public static final String GROUP_OPTIFINE         = "Optifine";
    
    // tasks
    public static final String TASK_JOIN_JARS         = "joinJars";        
    public static final String TASK_DIFF_OPTIFINE     = "diffOptifine";
    public static final String TASK_DIFF_EXEC         = "diffExecOptifine";
    public static final String TASK_DL_PATCHES        = "dlPatches";
    public static final String TASK_EXTRACT_CONFIG    = "extractConfig";
    public static final String TASK_EXTRACT_USER_CONFIG = "extractUserConfig";
    public static final String TASK_EXTRACT_PATCHER_CONFIG = "extractPatcherConfig";
    public static final String TASK_ASK_PERMISSION    = "askPermission";
    public static final String TASK_PREPROCESS        = "preprocess";
    public static final String TASK_REMOVE_EXTRAS     = "removeExtras";
    public static final String TASK_FILTER_MCP_PATCHES = "filterMcpPatches";
    public static final String TASK_FILTER_USER_FORGE_PATCHES = "filterUserForgePatches";
    public static final String TASK_OPTIFINE_PATCH    = "optifinePatch";

    //patching tasks
    public static final String TASK_PROJECT_DELETE_REJECTS    = "delete" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_REMAP_REJECTS     = "remap" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_EXTRACT_REJECTS   = "extract" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    
    //files
    public static final String JAR_OPTIFINE_FRESH     = "{optifineJar}";
    public static final String JAR_CLIENT_JOINED      = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
    public static final String JAR_OPTIFINE_DIFFED    = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-diffed.jar";
    public static final String DEOBFUSCATED_CLASSES   = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
    public static final String JAR_PREPROCESS       = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-preprocessed.jar";
    public static final String REMOVE_EXTRAS_OUT_USER      = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-extrasRemovedUser.jar";
    public static final String REMOVE_EXTRAS_OUT_PATCHER   = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-extrasRemovedPatcher.jar";
    public static final String MCP_FILTERED_USER_PATCHES   = "{CACHE_DIR}/de/oceanlabs/mcp/mcp/" + REPLACE_MC_VERSION
            + "/filteredUserPatches/";
    public static final String FORGE_FILTERED_USER_PATCHES = DIR_USERDEV + "/filteredForgePatches";
    public static final String OPTIFINE_PATCHED            = "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION
            + "/minecraft-" + REPLACE_MC_VERSION + "-optifinePatched.jar";
    
    public static final String OPTIFINE_CACHE         = "{CACHE_DIR}/com/github/aucguy/optifinegradle/";
    
    //TODO combine bases and files into one
    //TODO rename patches_dir and patch_dir
    public static final String PATCHES_DIR            = "patches/";
    public static final String PATCH_ZIP              = OPTIFINE_CACHE + "patchArchives/" + "{patchArchive}";
    public static final String PATCH_EXTRACT          = OPTIFINE_CACHE + "patchExtracts/" + REPLACE_OPTIFINE_VERSION + "/";
    public static final String CONFIG_ZIP_DIR         = "optifine-config/";
    public static final String CONFIG_DIR             = PATCH_EXTRACT + "config/";
    public static final String RENAMES_BASE           = "optifine-renames.properties";
    public static final String RENAMES_FILE           = CONFIG_DIR + RENAMES_BASE;
    public static final String REMOVED_METHODS_BASE   = "optifine-removed-methods.properties";
    public static final String REMOVED_METHODS_FILE   = CONFIG_DIR + REMOVED_METHODS_BASE;
    public static final String EXTRA_PATCH_EXCL_BASE  = "extra-patch-exclusions.txt";
    public static final String EXTRA_PATCH_EXCL_FILE  = CONFIG_DIR + EXTRA_PATCH_EXCL_BASE;
    public static final String PATCH_CONFIG_DIR       = PATCHES_DIR + CONFIG_ZIP_DIR;
    public static final String PATCH_DIR              = PATCH_EXTRACT + "patches/";
    
    //patching files
    public static final String PATCH_ARCHIVE          = "{patchArchive}";
    public static final String PATCH_URL              = "{patchUrl}";
    
    public static final String PROJECT_REJECTS_ZIP            = DIR_PROJECT_CACHE + "/rejects.zip";
    public static final String PROJECT_REMAPPED_REJECTS_ZIP   = DIR_PROJECT_CACHE + "/rejects-remapped.zip";
    public static final String MCP_FILTERED_PATCHER_PATCHES   = DIR_LOCAL_CACHE + "/filteredPatcherPatches/";
    public static final String OPTIFINE_PATCHED_PROJECT       = DIR_PROJECT_CACHE + "/optifinePatched.zip";
    public static final String EMPTY_DIR                      = DIR_LOCAL_CACHE + "/empty/";
    public static final String OPTIFINE_VERSION               = "D2";
    
    // defaults
    public static final String DEFAULT_OPTIFINE_VERSION = "1.12.2_HD_U_" + OPTIFINE_VERSION;
    public static final String DEFAULT_PATCH_URL      = "https://aucguy.github.io/downloads/optifinegradle/patches/{patchArchive}";
    public static final String DEFAULT_PATCH_ARCHIVE  = "patches-{optifineVersion}.zip";
    public static final String DEFAULT_OPTIFINE_JAR   = "{mainDir}/OptiFine_{optifineVersion}.jar";
    
    //patching tasks
    public static final String TASK_FILTER_PATCHER_FORGE_PATCHES = "filterForge" + REPLACE_PROJECT_CAP_NAME +"Patches";
    public static final String FORGE_FILTERED_PATCHER_PATCHES = DIR_PROJECT_CACHE + "/filteredPatches/";
    public static final String TASK_OPTIFINE_PATCH_PROJECT = "optifinePatch" + REPLACE_PROJECT_CAP_NAME;
    public static final String TASK_MAKE_EMPTY_DIR = "makeEmptyDir";
    public static final String TASK_PROJECT_RETRIEVE_REJECTS = "retrieve" + REPLACE_PROJECT_CAP_NAME + "Rejects";

    // packaging setup tasks
    public static final String TASK_GEN_PATCHES       = "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES       = "zipOptifinePatches";
    
    // user visible files
    public static final String OPTIFINE_PATCH_ZIP     = "{BUILD_DIR}/optifine/{patchArchive}";
    public static final String OPTIFINE_PATCH_DIR     = "{BUILD_DIR}/optifine/patches";
}
