package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import java.util.Scanner;

import net.minecraftforge.gradle.common.BasePlugin;

public class OptifinePlugin
{
    protected BasePlugin<?> plugin;
    public OptifineExtension extension;
    
    public OptifinePlugin(BasePlugin<?> x)
    {
        plugin = x;
    }
    
    public void init()
    {
        plugin.isOptifine = true;
    }

    public void applyPlugin(Class<? extends OptifineExtension> extensionClass)
    {
    	extension = plugin.project.getExtensions().create(EXTENSION, extensionClass);
    }

    public void afterEvaluate()
    {
        plugin.replacer.putReplacement(REPLACE_OPTIFINE_VERSION, extension.getOptifineVersion());
        plugin.replacer.putReplacement(REPLACE_PATCH_ARCHIVE, extension.getPatchArchive());
        plugin.replacer.putReplacement(REPLACE_PATCH_URL, extension.getPatchURL());
        plugin.replacer.putReplacement(REPLACE_OPTIFINE_JAR, extension.getOptifineJar());
        plugin.replacer.putReplacement(REPLACE_MAIN_DIR, plugin.project.getBuildFile().getParent());
    }

    public void askPermission()
    {
        plugin.project.getLogger().warn("WARNING. This will overwrite you workspace. Continue (Y/N)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();
        scanner.close();
        if (!response.toLowerCase().startsWith("y")) {
            throw (new RuntimeException("Failure to continue"));
        }
    }
}
