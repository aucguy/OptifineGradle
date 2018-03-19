package com.github.aucguy.optifinegradle.test;

import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.DIR_PROJECT_CACHE;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.REPLACE_PROJECT_CAP_NAME;

public class OptifineTestConstants 
{
    public static final String TASK_DEOBFUSCATE_JAR   = "optifineDeobfuscateJar";
    public static final String TASK_PROJECT_DEOBFUSCATE = "deobfuscateProj" + REPLACE_PROJECT_CAP_NAME;
    public static final String JAR_DEOBFUSCATED		  = DIR_PROJECT_CACHE + "/deobfuscatedJar.jar";
    
	public static final String TASK_DECOMPILE_CUSTOM  = "decompileCustom";
    public static final String TASK_GET_CALLERS		  = "getCallers";
}
