package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.OptifineConstants.DEOBFUSCATED_CLASSES;
import static com.github.aucguy.optifinegradle.OptifineConstants.EMPTY_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.EXTRA_PATCH_EXCLUSIONS;
import static com.github.aucguy.optifinegradle.OptifineConstants.FORGE_FILTERED_PATCHER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.GROUP_OPTIFINE;
import static com.github.aucguy.optifinegradle.OptifineConstants.MCP_FILTERED_PATCHER_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCHED_PROJECT;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.OPTIFINE_PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.PROJECT_REJECTS_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.PROJECT_REMAPPED_REJECTS_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.REMOVE_EXTRAS_OUT_PATCHER;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_MCP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_FILTER_PATCHER_FORGE_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_GEN_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_MAKE_EMPTY_DIR;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_OPTIFINE_PATCH_PROJECT;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_REMOVE_EXTRAS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_ZIP_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PREPROCESS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PROJECT_DELETE_REJECTS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PROJECT_EXTRACT_REJECTS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PROJECT_REMAP_REJECTS;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_PROJECT_RETRIEVE_REJECTS;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_GEN_PATCHES;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.JAR_DECOMP;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_DECOMP;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_GEN_PROJECTS;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_DEOBF;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.JAR_PROJECT_PATCHED;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_PATCH;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_EXTRACT_SRC;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_EXTRACT_RES;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_REMAP_JAR;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.JAR_DECOMP_POST;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_PROJECT_RETROMAP;
import static com.github.aucguy.optifinegradle.patcher.PatcherConstantsWrapper.TASK_POST_DECOMP;
import static net.minecraftforge.gradle.common.Constants.MCP_PATCHES_MERGED;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.Task;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.bundling.Zip;

import com.github.aucguy.optifinegradle.ExtractRenames;
import com.github.aucguy.optifinegradle.FilterPatches;
import com.github.aucguy.optifinegradle.MakeDir;
import com.github.aucguy.optifinegradle.OptifinePlugin;
import com.github.aucguy.optifinegradle.RemapRejects;
import com.github.aucguy.optifinegradle.RemoveExtras;
import com.github.aucguy.optifinegradle.RetrieveRejects;
import com.github.aucguy.optifinegradle.TaskGenPatchesWrapper;

import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;
import net.minecraftforge.gradle.tasks.DeobfuscateJar;
import net.minecraftforge.gradle.tasks.ExtractTask;
import net.minecraftforge.gradle.tasks.PatchSourcesTask;
import net.minecraftforge.gradle.tasks.PostDecompileTask;
import net.minecraftforge.gradle.tasks.RemapSources;
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
        projects = new HashMap<String, OptifinePatcherProject>();
    }
    
    @Override
    public void applyPlugin()
    {
        delegate.applyRenames();
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
    public void createProject(PatcherProject patcher)
    {
        super.createProject(patcher);
        FilterPatches filterPatches = makeTask(PatcherPluginWrapper.projectString(this, TASK_FILTER_PATCHER_FORGE_PATCHES, patcher), FilterPatches.class);
        {
            filterPatches.patchesIn = PatcherProjectWrapper.getDelayedPatchDir(patcher);
            filterPatches.excludeList = delayedFile(DEOBFUSCATED_CLASSES);
            filterPatches.extraExclusions = null;
            filterPatches.patchesOut = delayedFile(PatcherPluginWrapper.projectString(this, FORGE_FILTERED_PATCHER_PATCHES, patcher));
            filterPatches.dependsOn(TASK_DECOMP);
        }
        
        PatchSourcesTask patch = (PatchSourcesTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_PATCH, patcher));
        {
            patch.setPatches(delayedFile(PatcherPluginWrapper.projectString(this, FORGE_FILTERED_PATCHER_PATCHES, patcher)));
            patch.dependsOn(filterPatches);
        }

        PatchSourcesTask optifinePatch = makeTask(PatcherPluginWrapper.projectString(this, TASK_OPTIFINE_PATCH_PROJECT, patcher), PatchSourcesTask.class);
        {
            optifinePatch.setPatches(PatcherProjectExtras.getDelayedOptifinePatchDir(this, patcher));
            optifinePatch.setInJar(delayedFile(PatcherPluginWrapper.projectString(this, JAR_PROJECT_PATCHED, patcher)));
            optifinePatch.setOutJar(delayedFile(PatcherPluginWrapper.projectString(this, OPTIFINE_PATCHED_PROJECT, patcher)));
            optifinePatch.setDoesCache(false);
            optifinePatch.setMaxFuzz(2);
            optifinePatch.setFailOnError(false);
            optifinePatch.setMakeRejects(true);
            optifinePatch.dependsOn(patch, TASK_MAKE_EMPTY_DIR);
        }

        Delete deleteRejects = makeTask(PatcherPluginWrapper.projectString(this, TASK_PROJECT_DELETE_REJECTS, patcher), Delete.class);
        {
            deleteRejects.delete(PatcherPluginWrapper.projectString(this, PROJECT_REMAPPED_REJECTS_ZIP, patcher));
            //deletes and depending on depending on settings
        }

        RetrieveRejects retrieveRejects = makeTask(PatcherPluginWrapper.projectString(this, TASK_PROJECT_RETRIEVE_REJECTS, patcher), RetrieveRejects.class);
        {
            retrieveRejects.inFolder = delayedFile(PatcherPluginWrapper.projectString(this, FORGE_FILTERED_PATCHER_PATCHES, patcher));
            retrieveRejects.outZip = delayedFile(PatcherPluginWrapper.projectString(this, PROJECT_REJECTS_ZIP, patcher));
        }

        RemapRejects remapRejects = makeTask(PatcherPluginWrapper.projectString(this, TASK_PROJECT_REMAP_REJECTS, patcher), RemapRejects.class);
        {
            remapRejects.setInJar(delayedFile(PatcherPluginWrapper.projectString(this, PROJECT_REJECTS_ZIP, patcher)));
            remapRejects.setOutJar(delayedFile(PatcherPluginWrapper.projectString(this, PROJECT_REMAPPED_REJECTS_ZIP, patcher)));
            remapRejects.setMethodsCsv(delayedFile(Constants.CSV_METHOD));
            remapRejects.setFieldsCsv(delayedFile(Constants.CSV_FIELD));
            remapRejects.setParamsCsv(delayedFile(Constants.CSV_PARAM));
            remapRejects.setAddsJavadocs(false);
            remapRejects.setDoesCache(false);
            remapRejects.dependsOn(retrieveRejects);
        }

        ExtractTask extractRejects = makeTask(PatcherPluginWrapper.projectString(this, TASK_PROJECT_EXTRACT_REJECTS, patcher), ExtractTask.class);
        {
            // set into() thing in afterEval
            extractRejects.from(delayedFile(PatcherPluginWrapper.projectString(this, PROJECT_REMAPPED_REJECTS_ZIP, patcher)));
            extractRejects.include("*.java.patch.rej");
            extractRejects.setDoesCache(false);
            extractRejects.setClean(true);
            extractRejects.dependsOn(remapRejects, deleteRejects);
            //gets depended on depending on settings
        }

        ExtractTask extractSrc = (ExtractTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_EXTRACT_SRC, patcher));
        {
            extractSrc.dependsOn(optifinePatch);
        }

        ExtractTask extractRes = (ExtractTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_EXTRACT_RES, patcher));
        {
            extractRes.dependsOn(optifinePatch);
        }
    }

    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
        
        List<PatcherProject> patchersList = PatcherPluginWrapper.sortByPatching(this, getExtension().getProjects());
        for(PatcherProject patcher : patchersList)
        {
            if(PatcherProjectExtras.getRejectFolder(this, patcher) != null)
            {
                ExtractTask extractSrc = (ExtractTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_EXTRACT_SRC, patcher));
                ExtractTask extractRejects = (ExtractTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, "extract{CAPNAME}Rejects", patcher));
                Delete deleteRejects = (Delete) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, "delete{CAPNAME}Rejects", patcher));
                extractRejects.into(PatcherProjectExtras.getRejectFolder(this, patcher));
                deleteRejects.delete(PatcherProjectExtras.getRejectFolder(this, patcher));
                extractSrc.dependsOn(extractRejects);
            }

            if(patcher.isApplyMcpPatches())
            {
                throw(new RuntimeException("applyMcpPatches option for project " + patcher.getName() + " not supported for optifinegradle"));
                //TODO remove JAR_PROJECT_PATCHED and add OPTIFINE_PATCHED_PROJECT to TASK_PROJECT_EXTRACT_SRC and TASK_PROJECT_EXTRACT_RES
            }
            else
            {
                PatchSourcesTask patch = (PatchSourcesTask) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_PATCH, patcher));
                RemapSources remap = (RemapSources) project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_REMAP_JAR, patcher));
                remap.setInJar(delayedFile(PatcherPluginWrapper.projectString(this, OPTIFINE_PATCHED_PROJECT, patcher)));
                remap.dependsOn(PatcherPluginWrapper.projectString(this, TASK_OPTIFINE_PATCH_PROJECT, patcher));

                patch.setInjects(new LinkedList<Object>());
                patch.setInJar(delayedFile(JAR_DECOMP_POST));
            }

            if(PatcherProjectWrapper.doesGenPatches(patcher))
            {
                TaskGenPatchesWrapper genPatches = new TaskGenPatchesWrapper(project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_GEN_PATCHES, patcher)));
                PatcherProject genFrom = getExtension().getProjects().getByName(patcher.getGenPatchesFrom());
                genPatches.setPatchDir(PatcherProjectExtras.getOutputPatchDir(patcher));

                if(!PatcherProjectExtras.getsModified(genFrom))
                {
                    //clear from PatcherPlugin.afterEvaluate
                    genPatches.setOriginals(new LinkedList<Object>());
                    List<Object> dependencies = new LinkedList<Object>();
                    dependencies.add(PatcherPluginWrapper.projectString(this, TASK_PROJECT_RETROMAP, patcher));
                    genPatches.instance.setDependsOn(dependencies);

                    genPatches.addOriginalSource(delayedFile(PatcherPluginWrapper.projectString(this, JAR_PROJECT_PATCHED, genFrom)));
                    genPatches.dependsOn(PatcherPluginWrapper.projectString(this, TASK_PROJECT_PATCH, genFrom));
                }
            }
        }

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
        
        TaskGenPatchesWrapper modGenPatches = new TaskGenPatchesWrapper(project.getTasks().getByName(PatcherPluginWrapper.projectString(this, TASK_PROJECT_GEN_PATCHES, projectMod)));
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
