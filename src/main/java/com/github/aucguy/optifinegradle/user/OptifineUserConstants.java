package com.github.aucguy.optifinegradle.user;

import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_CACHE;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_PATCH_ARCHIVE;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_PATCH_URL;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_EXTRACT;
import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.REPLACE_API_VERSION;

public class OptifineUserConstants
{
    // tasks
    public static final String TASK_DL_PATCHES                = "dlPatches";
    public static final String TASK_EXTRACT_USER_CONFIG       = "extractUserConfig";
    public static final String TASK_FILTER_USER_FORGE_PATCHES = "filterUserForgePatches";
    public static final String TASK_OPTIFINE_PATCH            = "optifinePatch";
    
    // files
    public static final String REMOVE_EXTRAS_OUT_USER         = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/extrasRemovedUser.jar";
    public static final String MCP_FILTERED_USER_PATCHES      = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/filteredUserPatches/";
    public static final String FORGE_FILTERED_USER_PATCHES    = OPTIFINE_CACHE + "/forge/"     + REPLACE_API_VERSION + "/filteredForgePatches/";
    public static final String OPTIFINE_PATCHED               = OPTIFINE_CACHE + "/minecraft/" + REPLACE_MC_VERSION  + "/optifinePatched.jar";
    
    // patches
    public static final String PATCH_ARCHIVE                  = OPTIFINE_CACHE + "patchArchives/" + REPLACE_PATCH_ARCHIVE;
    public static final String PATCH_DIR                      = PATCH_EXTRACT + "patches/";
    public static final String PATCH_URL                      = REPLACE_PATCH_URL;
}
