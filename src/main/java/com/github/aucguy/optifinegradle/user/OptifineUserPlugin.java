package com.github.aucguy.optifinegradle.user;

import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_URL;
import static com.github.aucguy.optifinegradle.OptifineConstants.PATCH_ZIP;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_DL_PATCHES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_EXTRACT_RENAMES;
import static com.github.aucguy.optifinegradle.OptifineConstants.TASK_JOIN_JARS;
import static com.github.aucguy.optifinegradle.OptifineConstants.USER_RENAMES;

import com.github.aucguy.optifinegradle.ExtractRenames;
import com.github.aucguy.optifinegradle.OptifineExtension;
import com.github.aucguy.optifinegradle.OptifinePlugin;

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
    }
    
    @Override
    public void afterEvaluate()
    {
        super.afterEvaluate();
        delegate.afterEvaluate();
    }
}
