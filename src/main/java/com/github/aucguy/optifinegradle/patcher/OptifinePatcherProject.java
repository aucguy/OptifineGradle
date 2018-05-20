package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.patcher.OptifinePatcherConstants.EMPTY_DIR;

import java.io.File;

import groovy.lang.Closure;
import net.minecraftforge.gradle.patcher.PatcherProject;

public class OptifinePatcherProject
{
    public String name;
    public PatcherProject patcherProject;
    public OptifinePatcherPlugin plugin;
    private File outputPatchDir;
    private File optifinePatchDir;
    private File rejectFolder;
    private boolean modified = true;

    public OptifinePatcherProject(String name, OptifinePatcherPlugin plugin)
    {
        this.name = name;
        this.plugin = plugin;
    }

    public File getOptifinePatchDir()
    {
        return optifinePatchDir;
    }

    public void setOptifinePatchDir(Object optifinePatchDir)
    {
       this.optifinePatchDir = optifinePatchDir == null ? null : plugin.project.file(optifinePatchDir);
    }

    public File getOutputPatchDir()
    {
        return outputPatchDir != null ? outputPatchDir : patcherProject.getPatchDir();
    }

    public void setOutputPatchDir(Object outputPatchDir)
    {
        this.outputPatchDir = outputPatchDir == null ? null : plugin.project.file(outputPatchDir);
    }

    public void optifinePatchDir(Object optifinePatchDir)
    {
        setOptifinePatchDir(optifinePatchDir);
    }

    public void outputPatchDir(Object outputPatchDir)
    {
        setOutputPatchDir(outputPatchDir);
    }

    public File getRejectFolder()
    {
        return rejectFolder == null ? plugin.delayedFile(EMPTY_DIR).call() : rejectFolder;
    }

    public void setRejectFolder(Object rejectFolder)
    {
       this.rejectFolder = rejectFolder == null ? null : plugin.project.file(rejectFolder);
    }

    public void rejectFolder(Object rejectFolder)
    {
        setRejectFolder(rejectFolder);
    }

    public boolean getsModified()
    {
        return modified || (patcherProject.getGenPatchesFrom() != null && "clean".equals(patcherProject.getGenPatchesFrom().toLowerCase()));
    }

    public void setModified(boolean modified)
    {
        this.modified = modified;
    }

    @SuppressWarnings("serial")
    protected Closure<File> getDelayedOptifinePatchDir()
    {
        return new Closure<File>(plugin.project, this) {
            public File call()
            {
                File patchDir = getOptifinePatchDir();
                if(patchDir == null)
                {
                    return plugin.delayedFile(EMPTY_DIR).call();
                }
                return patchDir;
            }
        };
    }

    @SuppressWarnings("serial")
    protected Closure<File> getDelayedRejectFolder()
    {
        return new Closure<File>(plugin.project, this) {
            public File call()
            {
                return getRejectFolder();
            }
        };
    }
}
