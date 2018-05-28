package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.DEOBFUSCATED_CLASSES;
import static com.github.aucguy.optifinegradle.OptifineConstants.EXTENSION;
import static com.github.aucguy.optifinegradle.OptifineConstants.JAR_CLIENT_JOINED;
import static com.github.aucguy.optifinegradle.OptifineConstants.JAR_OPTIFINE_DIFFED;
import static com.github.aucguy.optifinegradle.OptifineConstants.JAR_OPTIFINE_FRESH;
import static com.github.aucguy.optifinegradle.OptifineConstants.JAR_METHODS_REMOVED;
import static com.github.aucguy.optifinegradle.OptifineConstants.REMOVED_METHODS_FILE;
import static com.github.aucguy.optifinegradle.OptifineConstants.RENAMES_FILE;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_MAIN_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_OPTIFINE_JAR;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_OPTIFINE_VERSION;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_PATCH_ARCHIVE;
import static com.github.aucguy.optifinegradle.OptifineConstants.REPLACE_PATCH_URL;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DIFF_EXEC;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DIFF_OPTIFINE;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_CONFIG;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_JOIN_JARS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_REMOVE_METHODS;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_VERSION;
import static net.minecraftforge.gradle.common.Constants.JAR_CLIENT_FRESH;
import static net.minecraftforge.gradle.common.Constants.JAR_MERGED;
import static net.minecraftforge.gradle.common.Constants.REPLACE_PROJECT_CACHE_DIR;
import static net.minecraftforge.gradle.common.Constants.SRG_NOTCH_TO_MCP;
import static net.minecraftforge.gradle.common.Constants.TASK_DL_CLIENT;
import static net.minecraftforge.gradle.common.Constants.TASK_GENERATE_SRGS;
import static net.minecraftforge.gradle.common.Constants.TASK_MERGE_JARS;
import static net.minecraftforge.gradle.common.Constants.REPLACE_CACHE_DIR;

import java.util.Scanner;

import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;

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
        modifyReplacement(REPLACE_PROJECT_CACHE_DIR);
    }

    public void applyPlugin(Class<? extends OptifineExtension> extensionClass)
    {
        extension = plugin.project.getExtensions().create(EXTENSION, extensionClass);

        Task extractConfig = plugin.makeTask(TASK_EXTRACT_CONFIG, Task.class);

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
            join.exclude("javax/"); //TODO put into a constant
            join.exclude("net/minecraftforge/");
            join.dependsOn(TASK_DL_CLIENT, diff, extractConfig, TASK_GENERATE_SRGS);
        }

        MergeJars merge = (MergeJars) plugin.project.getTasks().getByName(TASK_MERGE_JARS);
        {
            merge.setClient(plugin.delayedFile(JAR_CLIENT_JOINED));
            merge.setOutJar(plugin.delayedFile(JAR_METHODS_REMOVED));
            merge.dependsOn(join);
        }

        RemoveMethods preprocess = plugin.makeTask(TASK_REMOVE_METHODS, RemoveMethods.class);
        {
            preprocess.inJar = plugin.delayedFile(JAR_METHODS_REMOVED);
            preprocess.removedMethods = plugin.delayedFile(REMOVED_METHODS_FILE);
            preprocess.outJar = plugin.delayedFile(JAR_MERGED);
            preprocess.dependsOn(merge, extractConfig);
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
        plugin.replacer.putReplacement(key, plugin.replacer.get(key) + "-optifine-" + OPTIFINE_VERSION);
    }
}
