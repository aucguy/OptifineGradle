package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_RETROMAP;
import static net.minecraftforge.gradle.common.Constants.JAR_CLIENT_FRESH;
import static net.minecraftforge.gradle.common.Constants.JAR_MERGED;
import static net.minecraftforge.gradle.common.Constants.SRG_NOTCH_TO_MCP;
import static net.minecraftforge.gradle.common.Constants.TASK_GENERATE_SRGS;
import static net.minecraftforge.gradle.common.Constants.TASK_DL_CLIENT;
import static net.minecraftforge.gradle.common.Constants.TASK_MERGE_JARS;
import static net.minecraftforge.gradle.common.Constants.REPLACE_CACHE_DIR;
import static net.minecraftforge.gradle.common.Constants.REPLACE_PROJECT_CACHE_DIR;
import static net.minecraftforge.gradle.common.Constants.REPLACE_BUILD_DIR;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;

import com.github.aucguy.optifinegradle.patcher.PatcherPluginWrapper;
import com.github.aucguy.optifinegradle.user.JoinJars;

import net.minecraftforge.gradle.common.BasePlugin;
import net.minecraftforge.gradle.tasks.MergeJars;

public class OptifinePlugin
{
    protected BasePlugin<?> plugin;
    public OptifineExtension extension;
    
    public OptifinePlugin(BasePlugin<?> x)
    {
        plugin = x;
    }
    
    public void applyRenames()
    {
        modifyReplacement(REPLACE_CACHE_DIR);
        modifyReplacement(REPLACE_BUILD_DIR);
        modifyReplacement(REPLACE_PROJECT_CACHE_DIR);
    }

    public void applyPlugin(Class<? extends OptifineExtension> extensionClass)
    {
        extension = plugin.project.getExtensions().create(EXTENSION, extensionClass);

    	ExtractRenames extractRenames = plugin.makeTask(TASK_EXTRACT_RENAMES, ExtractRenames.class);
        {
            extractRenames.extractTo = plugin.delayedFile(RENAMES_FILE);
        }

        CacheWrapper diff = plugin.makeTask(TASK_DIFF_OPTIFINE, CacheWrapper.class);
        {
            Exec task = plugin.makeTask(TASK_DIFF_EXEC, Exec.class);
            task.executable("java");
            task.args("-cp", plugin.delayedString(JAR_OPTIFINE_FRESH), "optifine.Patcher",
                    plugin.delayedString(JAR_CLIENT_FRESH), plugin.delayedString(JAR_OPTIFINE_FRESH), plugin.delayedString(JAR_OPTIFINE_DIFFED));
            task.setStandardOutput(System.out);
            task.setErrorOutput(System.err);
            diff.task = task;
            diff.input1 = plugin.delayedFile(JAR_CLIENT_FRESH);
            diff.input2 = plugin.delayedFile(JAR_OPTIFINE_FRESH);
            diff.output = plugin.delayedFile(JAR_OPTIFINE_DIFFED);
            diff.dependsOn(TASK_DL_CLIENT);
        }

        JoinJars join = plugin.makeTask(TASK_JOIN_JARS, JoinJars.class);
        {
            join.client = plugin.delayedFile(JAR_CLIENT_FRESH);
            join.optifine = plugin.delayedFile(JAR_OPTIFINE_DIFFED);
            join.classList = plugin.delayedFile(DEOBFUSCATED_CLASSES);
            join.outJar = plugin.delayedFile(JAR_CLIENT_JOINED);
            join.renames = plugin.delayedFile(RENAMES_FILE);
            join.srg = plugin.delayedFile(SRG_NOTCH_TO_MCP);
            join.exclude("javax/");
            join.exclude("net/minecraftforge/");
            join.dependsOn(TASK_DL_CLIENT, diff, extractRenames, TASK_GENERATE_SRGS);
        }

        MergeJars merge = (MergeJars) plugin.project.getTasks().getByName(TASK_MERGE_JARS);
        {
            merge.setClient(plugin.delayedFile(JAR_CLIENT_JOINED));
            merge.setOutJar(plugin.delayedFile(JAR_PREPROCESS));
            merge.dependsOn(join);
        }

        PreProcess preprocess = plugin.makeTask(TASK_PREPROCESS, PreProcess.class);
        {
            preprocess.inJar = plugin.delayedFile(JAR_PREPROCESS);
            preprocess.outJar = plugin.delayedFile(JAR_MERGED);
            preprocess.dependsOn(merge);
        }
    }

    public void afterEvaluate()
    {
        plugin.replacer.putReplacement(REPLACE_OPTIFINE_VERSION, extension.getOptifineVersion());
        plugin.replacer.putReplacement(REPLACE_PATCH_ARCHIVE, extension.getPatchArchive());
        plugin.replacer.putReplacement(REPLACE_PATCH_URL, extension.getPatchURL());
        plugin.replacer.putReplacement(REPLACE_OPTIFINE_JAR, extension.getOptifineJar());
        plugin.replacer.putReplacement(REPLACE_MAIN_DIR, plugin.project.getBuildFile().getParent());
    }

    public void modifyReplacement(String key)
    {
        if (key.charAt(0) == '{' && key.charAt(key.length() - 1) == '}')
        {
            key = key.substring(1, key.length() - 1);
        }
        plugin.replacer.putReplacement(key, plugin.replacer.get(key) + "-optifine");
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
