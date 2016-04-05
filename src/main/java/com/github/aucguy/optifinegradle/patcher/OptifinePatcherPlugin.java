package com.github.aucguy.optifinegradle.patcher;

import com.github.aucguy.optifinegradle.OptifinePlugin;
import static com.github.aucguy.optifinegradle.OptifineConstants.*;
import static net.minecraftforge.gradle.patcher.PatcherConstants.JAR_PROJECT_PATCHED;
import static net.minecraftforge.gradle.patcher.PatcherConstants.JAR_PROJECT_RETROMAPPED;
import static net.minecraftforge.gradle.patcher.PatcherConstants.TASK_PROJECT_RETROMAP;

import java.io.File;

import static net.minecraftforge.gradle.patcher.PatcherConstants.TASK_PROJECT_PATCH;

import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Zip;

import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;
import net.minecraftforge.gradle.patcher.TaskGenPatches;

public class OptifinePatcherPlugin extends PatcherPlugin
{
    OptifinePlugin delegate;
    
    public OptifinePatcherPlugin()
    {
        super();
        delegate = new OptifinePlugin(this);
        delegate.init();
    }
    
    @Override
    public void applyPlugin()
    {
        super.applyPlugin();
        delegate.applyPlugin();
        
        //no prefix
        TaskGenPatches genPatches = makeTask(TASK_GEN_PATCHES, TaskGenPatches.class);
        {
            //original and changed sources set in afterEval
            genPatches.setPatchDir(delayedFile(OPTIFINE_PATCH_DIR));
        }
        
        Zip zipPatches = makeTask(TASK_ZIP_PATCHES, Zip.class);
        {
            zipPatches.from(delayedFile(OPTIFINE_PATCH_DIR));
            zipPatches.dependsOn(genPatches);
        }
        
        Task createPatches = makeTask(TASK_BUILD_PATCHES);
        createPatches.dependsOn(zipPatches);
        createPatches.setGroup(GROUP_OPTIFINE);
        createPatches.setDescription("Create the optifine patch archive");
    }
    
    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
        
        PatcherProject projectMod = patchersList.get(patchersList.size() - 1);
        PatcherProject projectOrig = getExtension().getProjects().getByName(projectMod.getPatchAfter());

        //no prefix
        TaskGenPatches genPatches = (TaskGenPatches) project.getTasks().getByName(TASK_GEN_PATCHES);
        if (projectOrig.getsModified()) //TODO make common method with PatcherPlugin.afterEvaluate()
        {
            genPatches.addOriginalSource(delayedFile(projectString(JAR_PROJECT_RETROMAPPED, projectOrig)));
            genPatches.dependsOn(projectString(TASK_PROJECT_RETROMAP, projectOrig));
        }
        else
        {
            genPatches.addOriginalSource(delayedFile(projectString(JAR_PROJECT_PATCHED, projectOrig)));
            genPatches.dependsOn(projectString(TASK_PROJECT_PATCH, projectOrig));
        }
        genPatches.addChangedSource(delayedFile(projectString(JAR_PROJECT_RETROMAPPED, projectMod)));
        genPatches.dependsOn(projectString(TASK_PROJECT_RETROMAP, projectMod));
        
        Zip zipPatches = (Zip) project.getTasks().getByName(TASK_ZIP_PATCHES);
        File out = delayedFile(OPTIFINE_PATCH_ZIP).call();
        zipPatches.setDestinationDir(out.getParentFile());
        zipPatches.setArchiveName(out.getName());
    }
}
