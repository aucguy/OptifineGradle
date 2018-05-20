package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_CACHE;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_PATCH_ARCHIVE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_LOCAL_CACHE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_PROJECT_CACHE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.REPLACE_PROJECT_CAP_NAME;
import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

public class OptifinePatcherConstants
{
    // setup tasks
    public static final String TASK_MAKE_EMPTY_DIR               = "makeEmptyDir";
    public static final String TASK_EXTRACT_PATCHER_CONFIG       = "extractPatcherConfig";
    public static final String TASK_FILTER_PATCHER_FORGE_PATCHES = "filterForge" + REPLACE_PROJECT_CAP_NAME +"Patches";
    public static final String TASK_OPTIFINE_PATCH_PROJECT       = "optifinePatch" + REPLACE_PROJECT_CAP_NAME;
    public static final String TASK_PROJECT_RETRIEVE_REJECTS     = "retrieve" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    
    // rejects tasks
    public static final String TASK_PROJECT_DELETE_REJECTS       = "delete" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_REMAP_REJECTS        = "remap" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    public static final String TASK_PROJECT_EXTRACT_REJECTS      = "extract" + REPLACE_PROJECT_CAP_NAME + "Rejects";
    
    // packaging tasks
    public static final String TASK_GEN_PATCHES                  = "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES                  = "zipOptifinePatches";
    
    // setup files
    public static final String EMPTY_DIR                         = DIR_LOCAL_CACHE + "/empty/";
    public static final String REMOVE_EXTRAS_OUT_PATCHER         = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/extrasRemovedPatcher.jar";
    public static final String MCP_FILTERED_PATCHER_PATCHES      = DIR_LOCAL_CACHE + "/filteredPatcherPatches/";
    public static final String FORGE_FILTERED_PATCHER_PATCHES    = DIR_PROJECT_CACHE + "/filteredPatches/";
    
    // reject files
    public static final String OPTIFINE_PATCHED_PROJECT          = DIR_PROJECT_CACHE + "/optifinePatched.zip";
    public static final String PROJECT_REJECTS_ZIP               = DIR_PROJECT_CACHE + "/rejects.zip";
    public static final String PROJECT_REMAPPED_REJECTS_ZIP      = DIR_PROJECT_CACHE + "/rejects-remapped.zip";
    
    // patching files
    public static final String OPTIFINE_PATCH_ZIP                = "{BUILD_DIR}/optifine/" + REPLACE_PATCH_ARCHIVE;
    public static final String OPTIFINE_PATCH_DIR                = "{BUILD_DIR}/optifine/patches";
    public static final String PATCH_CONFIG_DIR                  = "patches/optifine-config/";
}