package com.github.aucguy.optifinegradle.patcher;

import static com.github.aucguy.optifinegradle.OptifineConstants.EMPTY_DIR;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import groovy.lang.Closure;
import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;

public class PatcherProjectExtras
{
    static Map<PatcherProject, OptifinePatcherProject> mappings = new HashMap<PatcherProject, OptifinePatcherProject>();

    @SuppressWarnings("serial")
    public static Closure<File> getDelayedOptifinePatchDir(PatcherPlugin plugin, PatcherProject patcher)
    {
        return new Closure<File>(plugin.project, patcher) {

            public File call()
            {
                File patchDir = null;
                if(mappings.containsKey(patcher))
                {
                    OptifinePatcherProject optifinePatcher = mappings.get(patcher);
                    patchDir = optifinePatcher.getOptifinePatchDir();
                }
                if(patchDir == null)
                {
                    return plugin.delayedFile(EMPTY_DIR).call();
                }
                else
                {
                    return patchDir;
                }
            }
        };
    }

    public static File getRejectFolder(PatcherPlugin plugin, PatcherProject patcher)
    {
        if(mappings.containsKey(patcher))
        {
            return mappings.get(patcher).getRejectFolder();
        }
        else
        {
            return plugin.delayedFile(EMPTY_DIR).call();
        }
    }

    public static File getOutputPatchDir(PatcherProject patcher)
    {
        if(mappings.containsKey(patcher))
        {
            return mappings.get(patcher).getOutputPatchDir();
        }
        else
        {
            return patcher.getPatchDir();
        }
    }

    public static boolean getsModified(PatcherProject patcher)
    {
        if(mappings.containsKey(patcher))
        {
            return mappings.get(patcher).getsModified();
        }
        else
        {
            return patcher.getGenPatchesFrom() != null && "clean".equals(patcher.getGenPatchesFrom().toLowerCase());
        }
    }
}
