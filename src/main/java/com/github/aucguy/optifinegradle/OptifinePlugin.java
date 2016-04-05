package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import java.util.Scanner;

import net.minecraftforge.gradle.common.BasePlugin;

public class OptifinePlugin
{
    protected BasePlugin<?> plugin;
    
    public OptifinePlugin(BasePlugin<?> x)
    {
        plugin = x;
    }
    
    public void init()
    {
        plugin.isOptifine = true;
    }

    public void applyPlugin()
    {
        plugin.project.getExtensions().create(EXTENSION, OptifineExtension.class);
    }

    public void afterEvaluate()
    {
        OptifineExtension ext = (OptifineExtension) plugin.project.getExtensions().getByName(EXTENSION);
        plugin.replacer.putReplacement(REPLACE_PATCH_ARCHIVE, ext.getPatchArchive());
        plugin.replacer.putReplacement(REPLACE_PATCH_URL, ext.getPatchURL());
        plugin.replacer.putReplacement(REPLACE_OPTIFINE_JAR, ext.getOptifineJar());
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
