package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.OptifineConstants.DEOBFUSCATED_CLASSES;
import static com.github.aucguy.optifinegradle.OptifineConstants.EMPTY_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.EXTRA_PATCH_EXCLUSIONS;
import static com.github.aucguy.optifinegradle.OptifineConstants.GROUP_OPTIFINE;
import static com.github.aucguy.optifinegradle.OptifineConstants.MCP_FILTERED_PATCHER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.REMOVE_EXTRAS_OUT_PATCHER;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_MCP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_GEN_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_MAKE_EMPTY_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_REMOVE_EXTRAS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_ZIP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PREPROCESS;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_GEN_PATCHES;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.JAR_DECOMP;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_DECOMP;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_GEN_PROJECTS;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_DEOBF;
import static net.minecraftforge.gradle.common.Constants.MCP_PATCHES_MERGED;
import static net.minecraftforge.gradle.user.UserConstants.TASK_POST_DECOMP;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Zip;

import com.github.aucguy.optifinegradle.ExtractRenames;
import com.github.aucguy.optifinegradle.FilterPatches;
import com.github.aucguy.optifinegradle.MakeDir;
import com.github.aucguy.optifinegradle.OptifinePlugin;
import com.github.aucguy.optifinegradle.RemoveExtras;
import com.github.aucguy.optifinegradle.TaskGenPatchesWrapper;

import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;
import net.minecraftforge.gradle.tasks.DeobfuscateJar;
import net.minecraftforge.gradle.tasks.PostDecompileTask;
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

        MakeDir makeDir = makeTask(TASK_MAKE_EMPTY_DIR, MakeDir.class);
        {
            makeDir.directory = delayedFile(EMPTY_DIR);
        }

        DeobfuscateJar deobfJar = (DeobfuscateJar) project.getTasks().getByName(TASK_DEOBF);
        {
            deobfJar.dependsOn(TASK_PREPROCESS);
        }

        RemoveExtras removeExtras = makeTask(TASK_REMOVE_EXTRAS, RemoveExtras.class);
        {
            removeExtras.setInJar(delayedFile(JAR_DECOMP));
            removeExtras.setOutJar(delayedFile(REMOVE_EXTRAS_OUT_PATCHER));
            removeExtras.dependsOn(TASK_DECOMP);
        }

        FilterPatches filterPatches = makeTask(TASK_FILTER_MCP_PATCHES, FilterPatches.class);
        {
            filterPatches.patchesIn = delayedFile(MCP_PATCHES_MERGED);
            filterPatches.excludeList = delayedFile(DEOBFUSCATED_CLASSES);
            filterPatches.extraExclusions = Arrays.asList(EXTRA_PATCH_EXCLUSIONS.split(";"));
            filterPatches.patchesOut = delayedFile(MCP_FILTERED_PATCHER_PATCHES);
            filterPatches.dependsOn(TASK_DECOMP); //change dependency to not be so expensive
        }

        PostDecompileTask postDecompileJar = (PostDecompileTask) project.getTasks().getByName(TASK_POST_DECOMP);
        {
            postDecompileJar.setInJar(delayedFile(REMOVE_EXTRAS_OUT_PATCHER));
            postDecompileJar.setPatches(delayedFile(MCP_FILTERED_PATCHER_PATCHES));
            postDecompileJar.dependsOn(removeExtras, filterPatches);
        }

        Task createProjects = project.getTasks().getByName(TASK_GEN_PROJECTS);
        {
            TaskGenSubProjectsWrapper.removeProject(createProjects, "clean");
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
