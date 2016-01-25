package com.github.aucguy.optifinegradle;

import static net.minecraftforge.gradle.common.Constants.REPLACE_MC_VERSION;

/**
 * random constants
 */
public class OptifineConstants {
	public static final String JAR_OPTIFINE_FRESH	= "{BUILD_DIR}/OptiFine_1.8.0_HD_U_D5.jar";
    public static final String FIELD_RENAMES		= "com/aucguy/optifinegradle/renames.properties";
    public static final String IGNORED_BROKEN_LINES = "com/aucguy/optifinegradle/ignoredBrokenLines.txt";
    public static final String OBFUSCATED_CLASSES	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-obfuscatedClasses.txt";
    public static final String DEOBFUSCATED_CLASSES	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-deobfuscatedClasses.txt";
    public static final String OPTIFINE_PATCHES     = "com/aucguy/optifinegradle/patches/index.txt";
    public static final String PATCH_PREFIX         = "com/aucguy/optifinegradle/patches/";
    public static final String PATCH_POSTFIX        = ".java.patch";
    public static final String TASK_OPTIFINE_PATCH  = "optifinePatch";
    public static final String OPTIFINE_PATCH_DIR   = "C:/Users/Owner/Documents/fun/java/modpath/forge-1.8-11.14.3.1512-mdk/realpatches/";
    public static final String TASK_JOIN_JARS		= "joinJars";
    public static final String JAR_CLIENT_JOINED	= "{CACHE_DIR}/net/minecraft/minecraft/" + REPLACE_MC_VERSION + "/minecraft-" + REPLACE_MC_VERSION + "-joined.jar";
}
