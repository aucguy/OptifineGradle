package com.github.aucguy.optifinegradle.test;

import net.minecraftforge.gradle.common.Constants;
import net.minecraftforge.gradle.patcher.PatcherProject;
import net.minecraftforge.gradle.tasks.ApplyFernFlowerTask;

import static com.github.aucguy.optifinegradle.OptifineConstants.GROUP_OPTIFINE;
import static com.github.aucguy.optifinegradle.test.OptifineTestConstants.*;
import static net.minecraftforge.gradle.common.Constants.CSV_FIELD;
import static net.minecraftforge.gradle.common.Constants.CSV_METHOD;
import static net.minecraftforge.gradle.common.Constants.CSV_PARAM;
import static net.minecraftforge.gradle.patcher.PatcherConstants.JAR_DEOBF;

import org.gradle.api.Task;

import com.github.aucguy.optifinegradle.patcher.DeobfuscateJar;
import com.github.aucguy.optifinegradle.patcher.GetCallersTask;
import com.github.aucguy.optifinegradle.patcher.OptifinePatcherPlugin;

public class OptifineTestPlugin extends OptifinePatcherPlugin
{	
	@Override
	public void applyPlugin()
	{
		super.applyPlugin();
		Object decompileJar = project.getProperties().get("decompileJar");
		if(decompileJar != null && decompileJar instanceof String)
		{
			ApplyFernFlowerTask applyFF = (ApplyFernFlowerTask) makeTask(TASK_DECOMPILE_CUSTOM, ApplyFernFlowerTask.class);
			{
				String name = (String) decompileJar;
				applyFF.setInJar(delayedFile(name));
				name = name.replaceFirst("[.]", "-processed.");
				applyFF.setOutJar(delayedFile(name));
				applyFF.setClasspath(project.getConfigurations().getByName(Constants.CONFIG_MC_DEPS));
				applyFF.setDoesCache(false);
			}
		}
		
        Task deobfuscateJar = makeTask(TASK_DEOBFUSCATE_JAR);
        deobfuscateJar.setGroup(GROUP_OPTIFINE);
        deobfuscateJar.setDescription("Deobfuscates a jar");
	}
	
	@Override
	public void afterEvaluate()
	{
		super.afterEvaluate();
        Task deobfuscateJar = project.getTasks().getByName(TASK_DEOBFUSCATE_JAR);
        
        for(PatcherProject patcher : patchersList)
        {
        	DeobfuscateJar deobfTask = makeTask(projectString(TASK_PROJECT_DEOBFUSCATE, patcher), DeobfuscateJar.class);
        	{
        		deobfTask.inJar = delayedFile(JAR_DEOBF);
        		deobfTask.methodsCsv = delayedFile(CSV_METHOD);
        		deobfTask.fieldsCsv = delayedFile(CSV_FIELD);
        		deobfTask.paramsCsv = delayedFile(CSV_PARAM);
        		deobfTask.outJar = delayedFile(projectString(JAR_DEOBFUSCATED, patcher));
        		deobfuscateJar.dependsOn(deobfTask);
        	}
        }
        
        PatcherProject projectMod = patchersList.get(patchersList.size() - 1);
        
        GetCallersTask getCallers = makeTask(TASK_GET_CALLERS, GetCallersTask.class);
        {
        	getCallers.inJar = delayedFile(projectString(JAR_DEOBFUSCATED, projectMod));
        	getCallers.searchMethod = (String) project.getProperties().get("searchMethod");
        	getCallers.setGroup(GROUP_OPTIFINE);
        	getCallers.setDescription("searches the deobfuscated binary jar for a call of a particular method");
        }
	}
}
