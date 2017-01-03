package com.github.aucguy.optifinegradle.user;

import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DL_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_JOIN_JARS;

import java.io.File;

import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.Copy;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import com.github.aucguy.optifinegradle.OptifinePlugin;

import groovy.lang.Closure;
import net.minecraftforge.gradle.tasks.Download;
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
        delegate.applyPlugin();
        
        Download dlPatches = makeTask(TASK_DL_PATCHES, Download.class);
        {
        	dlPatches.setOutput(delayedFile(PATCH_ZIP));
        	dlPatches.setUrl(delayedString(PATCH_URL));
        }
        
    	Copy extractRenames = (Copy) project.getTasks().getByName(TASK_EXTRACT_RENAMES);
    	{
    		extractRenames.from(new Closure<FileTree>(null)
    		{
    			public FileTree call()
    			{
    				return project.zipTree(delayedString(PATCH_ZIP).call());
    			}
    		});
    		extractRenames.dependsOn(dlPatches);
    	}
        
        JoinJars join = (JoinJars) project.getTasks().getByName(TASK_JOIN_JARS);
        {
        	join.dependsOn(extractRenames);
        }
    }
    
    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
    }
}
