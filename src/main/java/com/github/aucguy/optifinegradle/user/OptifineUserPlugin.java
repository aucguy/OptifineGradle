package com.github.aucguy.optifinegradle.user;

import static com.github.aucguy.optifinegradle.OptifineConstants.CONFIG_ZIP_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.DEOBFUSCATED_CLASSES;
import static com.github.aucguy.optifinegradle.OptifineConstants.EXTRA_PATCH_EXCLUSIONS;
import static com.github.aucguy.optifinegradle.OptifineConstants.FORGE_FILTERED_USER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.MCP_FILTERED_USER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_CACHE;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCHED;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_URL;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.REMOVE_EXTRAS_OUT_USER;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DL_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_CONFIG;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_MCP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_USER_FORGE_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_JOIN_JARS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_OPTIFINE_PATCH;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_REMOVE_EXTRAS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PREPROCESS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_USER_CONFIG;
import static net.minecraftforge.gradle.common.Constants.MCP_PATCHES_MERGED;
import static net.minecraftforge.gradle.common.Constants.REPLACE_ASSET_INDEX;
import static net.minecraftforge.gradle.common.Constants.TASK_DL_VERSION_JSON;
import static net.minecraftforge.gradle.common.Constants.TASK_MERGE_JARS;   
import static net.minecraftforge.gradle.user.UserConstants.*;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.TASK_PATCH;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.ZIP_UD_PATCHES;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Task;

import java.io.File;

import com.github.aucguy.optifinegradle.FilterPatches;
import com.github.aucguy.optifinegradle.OptifineExtension;
import com.github.aucguy.optifinegradle.OptifinePlugin;
import com.github.aucguy.optifinegradle.RemoveExtras;
import com.github.aucguy.optifinegradle.patcher.PatcherPluginWrapper;

import groovy.lang.Closure;
import net.minecraftforge.gradle.tasks.DeobfuscateJar;
import net.minecraftforge.gradle.tasks.EtagDownloadTask;
import net.minecraftforge.gradle.tasks.ExtractTask;
import net.minecraftforge.gradle.tasks.PatchSourcesTask;
import net.minecraftforge.gradle.tasks.PostDecompileTask;
import net.minecraftforge.gradle.tasks.RemapSources;
import net.minecraftforge.gradle.tasks.fernflower.ApplyFernFlowerTask;
import net.minecraftforge.gradle.user.patcherUser.forge.ForgePlugin;

public class OptifineUserPlugin extends ForgePlugin
{
    OptifinePlugin delegate;
    
    public OptifineUserPlugin()
    {
        super();
        delegate = new OptifinePlugin(this);
    }
    
    @Override
    public void applyUserPlugin()
    {
        delegate.applyRenames();
        super.applyUserPlugin();
        delegate.applyPlugin(OptifineExtension.class);

        replacer.putReplacement(REPLACE_ASSET_INDEX, "tmp");
        EtagDownloadTask getVersionJson = (EtagDownloadTask) project.getTasks().getByName(TASK_DL_VERSION_JSON);
        getVersionJson.doFirst(new Closure<Object>(OptifineUserPlugin.class)
        {
            @Override
            public Object call()
            {
                Map<String, String> replaceMap = (Map<String, String>) ReplacementProviderWrapper.getReplaceMap(replacer);
                replaceMap.remove(REPLACE_ASSET_INDEX);
                return null;
            }
        });

        DeobfuscateJar deobfBin = (DeobfuscateJar) project.getTasks().getByName(TASK_DEOBF);
        {
            List<Object> dependencies = new LinkedList<Object>(deobfBin.getDependsOn());
            dependencies.remove(TASK_MERGE_JARS);
            dependencies.add(0, TASK_PREPROCESS);
            deobfBin.setDependsOn(dependencies);
        }

        DownloadWithFile dlPatches = makeTask(TASK_DL_PATCHES, DownloadWithFile.class);
        {
        	dlPatches.setOutput(delayedFile(PATCH_ZIP));
        	dlPatches.setUrl(delayedString(PATCH_URL));
        	dlPatches.dependsOn(net.minecraftforge.gradle.common.Constants.TASK_DL_ASSET_INDEX);
        }


        ExtractTask extractUserConfig = makeTask(TASK_EXTRACT_USER_CONFIG, ExtractTask.class);
    	{
    	    extractUserConfig.from(delayedFile(PATCH_ZIP));
    	    extractUserConfig.into(delayedFile(OPTIFINE_CACHE));
    	    extractUserConfig.include(CONFIG_ZIP_DIR);
    	    extractUserConfig.dependsOn(dlPatches);
    	}
    	
        Task extractConfig = project.getTasks().getByName(TASK_EXTRACT_CONFIG);
        extractConfig.dependsOn(extractUserConfig);

        JoinJars join = (JoinJars) project.getTasks().getByName(TASK_JOIN_JARS);
        {
            //TODO remove redundant dependency (other in OptifinePlugin)
        	join.dependsOn(extractConfig);
        }

        ApplyFernFlowerTask decompile = (ApplyFernFlowerTask) project.getTasks().getByName(TASK_DECOMPILE);
        RemoveExtras removeExtras = makeTask(TASK_REMOVE_EXTRAS, RemoveExtras.class);
        {
            removeExtras.setInJar(new Closure<File>(null)
            {
                public File call()
                {
                    return decompile.getOutJar();
                }
            });
            removeExtras.setOutJar(delayedFile(REMOVE_EXTRAS_OUT_USER));
            removeExtras.dependsOn(decompile);
        }

        FilterPatches filterMcpPatches = makeTask(TASK_FILTER_MCP_PATCHES, FilterPatches.class);
        {
            filterMcpPatches.patchesIn = delayedFile(MCP_PATCHES_MERGED);
            filterMcpPatches.excludeList = delayedFile(DEOBFUSCATED_CLASSES);
            filterMcpPatches.extraExclusions = Arrays.asList(EXTRA_PATCH_EXCLUSIONS.split(";"));
            filterMcpPatches.patchesOut = delayedFile(MCP_FILTERED_USER_PATCHES);
            filterMcpPatches.dependsOn(decompile);
        }

        PostDecompileTask postDecomp = (PostDecompileTask) project.getTasks().getByName(TASK_POST_DECOMP);
        {
            postDecomp.setInJar(delayedFile(REMOVE_EXTRAS_OUT_USER));
            postDecomp.setPatches(delayedFile(MCP_FILTERED_USER_PATCHES));
            postDecomp.dependsOn(removeExtras, filterMcpPatches);
        }

        FilterPatches filterForgePatches = makeTask(TASK_FILTER_USER_FORGE_PATCHES, FilterPatches.class);
        {
            filterForgePatches.patchesIn = delayedFile(ZIP_UD_PATCHES);
            filterForgePatches.excludeList = delayedFile(DEOBFUSCATED_CLASSES);
            filterForgePatches.extraExclusions = null;
            filterForgePatches.patchesOut = delayedFile(FORGE_FILTERED_USER_PATCHES);
            filterForgePatches.dependsOn(TASK_POST_DECOMP);
        }

        PatchSourcesTask patch = (PatchSourcesTask) project.getTasks().getByName(TASK_PATCH);
        {
            patch.setPatches(delayedFile(FORGE_FILTERED_USER_PATCHES));
            patch.dependsOn(filterForgePatches, dlPatches);
        }

        PatchSourcesTask optifinePatch = makeTask(TASK_OPTIFINE_PATCH, PatchSourcesTask.class);
        {
            optifinePatch.setPatches(delayedFile(PATCH_ZIP));
            optifinePatch.setFailOnError(true);
            optifinePatch.setMakeRejects(false);
            optifinePatch.setPatchStrip(1);
            optifinePatch.setInJar(new Closure<File>(null)
            {
                public File call()
                {
                    return patch.getOutJar();
                }
            });
            optifinePatch.setOutJar(delayedFile(OPTIFINE_PATCHED));
            optifinePatch.dependsOn(patch);
        }

        RemapSources remap = (RemapSources) project.getTasks().getByName(TASK_REMAP);
        {
            remap.setInJar(delayedFile(OPTIFINE_PATCHED));
            remap.dependsOn(optifinePatch);
        }
    }
    
    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
    }
}
