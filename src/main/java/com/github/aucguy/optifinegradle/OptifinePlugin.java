package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Zip;

import net.minecraftforge.gradle.patcher.TaskGenPatches;
import net.minecraftforge.gradle.tasks.ExtractTask;
import net.minecraftforge.gradle.user.patcherUser.forge.ForgePlugin;

public class OptifinePlugin extends ForgePlugin {
	public OptifinePlugin() {
		super();
		isOptifine = true;
	}
	
	@Override
	public void applyUserPlugin() {
		super.applyUserPlugin();
		
		String global = getGlobalPattern();
        String local = getLocalPattern();
        
        final Object remappedJar = chooseDeobfOutput(global, local, "", "remapped", true);
		final Object optifinePatchedJar = chooseDeobfOutput(global, local, "Src", "sources", true);
		
		ExtractTask extractSource = makeTask(TASK_EXTRACT_SOURCES, ExtractTask.class);
		{
			extractSource.from(optifinePatchedJar);
			extractSource.into(project.file(SRC_DIR));
			extractSource.include("*.java", "**/*.java");
			extractSource.dependsOn(TASK_OPTIFINE_PATCH);
		}
		
		ExtractTask extractResource = makeTask(TASK_EXTRACT_RESOURCES, ExtractTask.class);
		{
			extractResource.from(optifinePatchedJar);
			extractResource.into(project.file(RESC_DIR));
			extractResource.exclude("*.java", "**/*.java");
			extractResource.dependsOn(TASK_OPTIFINE_PATCH);
		}
		
		TaskGenPatches genPatches = makeTask(TASK_GEN_PATCHES, TaskGenPatches.class);
		{
			genPatches.addOriginalSource(remappedJar);
			genPatches.addChangedSource(project.file(SRC_DIR));
			genPatches.setPatchDir(delayedFile(GEN_PATCH_DIR));
			genPatches.setFlattened(true);
		}
		
		Zip zipPatches = makeTask(TASK_ZIP_PATCHES, Zip.class);
		{
			zipPatches.from(delayedFile(GEN_PATCH_DIR));
			zipPatches.setDestinationDir(new File(PATCH_ZIP_DIR));
			zipPatches.setBaseName(PATCH_ZIP_NAME);
			zipPatches.setVersion("0.0.0");
			zipPatches.setExtension("zip");
			zipPatches.dependsOn(genPatches);
		}
		
		Task setupPatchEnviro = makeTask(TASK_PATCH_ENVIRO, DefaultTask.class);
		setupPatchEnviro.setGroup(GROUP_OPTIFINE);
		setupPatchEnviro.setDescription("Sets up the enviroment for making optifine patches");
		setupPatchEnviro.dependsOn(extractSource, extractResource);
		
		Task createPatches = makeTask(TASK_CREATE_PATCHES, DefaultTask.class);
		createPatches.setGroup(GROUP_OPTIFINE);
		createPatches.setDescription("Generates a zip containing patches");
		createPatches.dependsOn(zipPatches);
	}
}
