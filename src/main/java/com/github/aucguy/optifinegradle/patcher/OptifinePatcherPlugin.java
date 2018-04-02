package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.OptifineConstants.GROUP_OPTIFINE;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_GEN_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_ZIP_PATCHES;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_GEN_PATCHES;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.tasks.bundling.Zip;

import com.github.aucguy.optifinegradle.ExtractRenames;
import com.github.aucguy.optifinegradle.OptifinePlugin;
import com.github.aucguy.optifinegradle.TaskGenPatchesWrapper;

import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;
import net.minecraftforge.gradle.patcher.PatcherProjectFactory;
import net.minecraftforge.gradle.tasks.fernflower.ApplyFernFlowerTask;
import net.minecraftforge.gradle.util.GradleConfigurationException;

public class OptifinePatcherPlugin extends PatcherPlugin
{
    OptifinePlugin delegate;
    Map<String, OptifinePatcherProject> projects;
    
    public OptifinePatcherPlugin()
    {
        super();
        delegate = new OptifinePlugin(this);
        delegate.init();
        projects = new HashMap<String, OptifinePatcherProject>();
    }
    
    @Override
    public void applyPlugin()
    {
        super.applyPlugin();
        delegate.applyPlugin(OptifinePatcherExtension.class);

        OptifinePatcherPlugin self = this;
        NamedDomainObjectContainer<OptifinePatcherProject> container = project.container(OptifinePatcherProject.class,
                new NamedDomainObjectFactory<OptifinePatcherProject>()
        {
            @Override
            public OptifinePatcherProject create(String name)
            {
                if(name.equals("clean"))
                {
                    throw(new GradleConfigurationException("project name cannot be clean"));
                }
                return new OptifinePatcherProject(name, self);
            }
        });
        ((OptifinePatcherExtension) delegate.extension).container = container;
        container.whenObjectAdded(new Action<OptifinePatcherProject>()
        {
            @Override
            public void execute(OptifinePatcherProject project)
            {
                projects.put(project.name, project);
            }

        });
        container.whenObjectRemoved(new Action<OptifinePatcherProject>()
        {
            @Override
            public void execute(OptifinePatcherProject project)
            {
                projects.remove(project.name);
            }

        });
        
        TaskGenPatchesWrapper optifineGenPatches = TaskGenPatchesWrapper.makeTask(this, TASK_GEN_PATCHES);
        {
            optifineGenPatches.setPatchDir(delayedFile(OPTIFINE_PATCH_DIR));
        }
        
        Zip zipPatches = makeTask(TASK_ZIP_PATCHES, Zip.class);
        {
            zipPatches.from(delayedFile(OPTIFINE_PATCH_DIR));
            zipPatches.setGroup(GROUP_OPTIFINE);
            zipPatches.setDescription("Create the optifine patch archive");
            zipPatches.dependsOn(optifineGenPatches);
        }

        ApplyFernFlowerTask testFernFlower = makeTask("testFernFlower", ApplyFernFlowerTask.class);
        {
            testFernFlower.setInJar(delayedFile("{BUILD_DIR}/localCache/test-in.zip"));
            testFernFlower.setOutJar(delayedFile("{BUILD_DIR}/localCache/test-out.zip"));
            testFernFlower.setDoesCache(false);
            testFernFlower.setClasspath(project.getConfigurations().getByName(Constants.CONFIG_MC_DEPS));
            testFernFlower.setForkedClasspath(project.getConfigurations().getByName(Constants.CONFIG_FFI_DEPS));
        }
    }
    
    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
        
        for(OptifinePatcherProject optifinePatcherProject : projects.values())
        {
            for(PatcherProject patcherProject : patchersList)
            {
                if(patcherProject.getName().equals(optifinePatcherProject.name))
                {
                    optifinePatcherProject.patcherProject = patcherProject;
                    PatcherProjectExtras.mappings.put(patcherProject, optifinePatcherProject);
                    break;
                }
            }
        }

        final PatcherProject projectMod = patchersList.get(patchersList.size() - 1);
        
        ExtractRenames extractRenames = (ExtractRenames) project.getTasks().getByName(TASK_EXTRACT_RENAMES);
        {
            extractRenames.inZip = delayedFile(PATCH_RENAMES);
        }
        
        TaskGenPatchesWrapper modGenPatches = new TaskGenPatchesWrapper(project.getTasks().getByName(projectString(TASK_PROJECT_GEN_PATCHES, projectMod)));
        TaskGenPatchesWrapper optifineGenPatches = new TaskGenPatchesWrapper(project.getTasks().getByName(TASK_GEN_PATCHES));
        {
            for(File file : modGenPatches.getOriginalSource())
            {
                optifineGenPatches.addOriginalSource(file);
            }
            for(File file : modGenPatches.getChangedSource())
            {
                optifineGenPatches.addChangedSource(file);
            }
            for(Object dependency : modGenPatches.getDependsOn())
            {
                optifineGenPatches.dependsOn(dependency);
            }
        }
        
        Zip zipPatches = (Zip) project.getTasks().getByName(TASK_ZIP_PATCHES);
        {
            File out = delayedFile(OPTIFINE_PATCH_ZIP).call();
            zipPatches.setDestinationDir(out.getParentFile());
            zipPatches.setArchiveName(out.getName());
            zipPatches.from(delayedFile(PATCH_RENAMES));
        }
    }
}
