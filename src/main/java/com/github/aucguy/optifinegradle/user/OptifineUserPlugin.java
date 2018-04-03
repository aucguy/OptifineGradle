package com.github.aucguy.optifinegradle.user;

import static com.github.aucguy.optifinegradle.OptifineConstants.DEOBFUSCATED_CLASSES;
import static com.github.aucguy.optifinegradle.OptifineConstants.EXTRA_PATCH_EXCLUSIONS;
import static com.github.aucguy.optifinegradle.OptifineConstants.FORGE_FILTERED_USER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.MCP_FILTERED_USER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCHED;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_URL;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.REMOVE_EXTRAS_OUT_USER;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DL_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_MCP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_USER_FORGE_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_JOIN_JARS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_OPTIFINE_PATCH;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_REMOVE_EXTRAS;
import static com.github.aucguy.optifinegradle.OptifineConstants.USER_RENAMES;
import static net.minecraftforge.gradle.common.Constants.MCP_PATCHES_MERGED;
import static net.minecraftforge.gradle.user.UserConstants.*;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.TASK_PATCH;
import static net.minecraftforge.gradle.user.patcherUser.PatcherUserConstants.ZIP_UD_PATCHES;

import java.util.Arrays;
import java.io.File;

import com.github.aucguy.optifinegradle.ExtractRenames;
import com.github.aucguy.optifinegradle.FilterPatches;
import com.github.aucguy.optifinegradle.OptifineExtension;
import com.github.aucguy.optifinegradle.OptifinePlugin;
import com.github.aucguy.optifinegradle.RemoveExtras;

import groovy.lang.Closure;
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
        delegate.init();
    }
    
    @Override
    public void applyUserPlugin()
    {
        super.applyUserPlugin();
        delegate.applyPlugin(OptifineExtension.class);

        DownloadWithFile dlPatches = makeTask(TASK_DL_PATCHES, DownloadWithFile.class);
        {
        	dlPatches.setOutput(delayedFile(PATCH_ZIP));
        	dlPatches.setUrl(delayedString(PATCH_URL));
        	dlPatches.dependsOn(net.minecraftforge.gradle.common.Constants.TASK_DL_ASSET_INDEX);
        }

    	ExtractRenames extractRenames = (ExtractRenames) project.getTasks().getByName(TASK_EXTRACT_RENAMES);
    	{
    	    extractRenames.inZip = delayedFile(USER_RENAMES);
    		extractRenames.dependsOn(dlPatches);
    	}

        JoinJars join = (JoinJars) project.getTasks().getByName(TASK_JOIN_JARS);
        {
        	join.dependsOn(extractRenames);
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
