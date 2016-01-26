package com.github.aucguy.optifinegradle;

import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants {
	//normal setup
    public static final String TASK_OPTIFINE_PATCH  = "optifinePatch";
    public static final String TASK_JOIN_JARS		= "joinJars";
	public static final String JAR_OPTIFINE_FRESH	= "{BUILD_DIR}/OptiFine_1.8.0_HD_U_D5.jar";
	public static final String JAR_CLIENT_JOINED	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
    public static final String OBFUSCATED_CLASSES	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-obfuscatedClasses.txt";
    public static final String DEOBFUSCATED_CLASSES	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
	public static final String FIELD_RENAMES		= "com/github/aucguy/optifinegradle/renames.properties";
    public static final String IGNORED_BROKEN_LINES = "com/github/aucguy/optifinegradle/ignoredBrokenLines.txt";

    //patch making
    public static final String TASK_EXTRACT_SOURCES = "extractOptifineSrc";
    public static final String TASK_EXTRACT_RESOURCES = "extractOptifineResc";
    public static final String TASK_GEN_PATCHES		= "genOptifinePatches";
    public static final String TASK_ZIP_PATCHES		= "zipOptifinePatches";
    public static final String TASK_PATCH_ENVIRO	= "setupPatchEnviro";
    public static final String TASK_CREATE_PATCHES	= "createOptifinePatches";
    public static final String GROUP_OPTIFINE		= "Optifine";
    public static final String SRC_DIR				= "src/main/java";
    public static final String RESC_DIR				= "src/main/resources";
    public static final String GEN_PATCH_DIR		= "build/optifine/patchDir";
    public static final String PATCH_ZIP_DIR		= "build/optifine/";
    public static final String PATCH_ZIP_NAME		= "patches";
}
