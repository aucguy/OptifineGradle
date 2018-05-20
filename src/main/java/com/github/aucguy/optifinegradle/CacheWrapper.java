package com.github.aucguy.optifinegradle;

import java.io.File;

import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;

public class CacheWrapper extends CachedTask
{
	public AbstractTask task;
	
	@InputFile
	public Object input1;
	
	@InputFile
	public Object input2;
	
	@OutputFile
	@Cached
	public Object output;
	
	@TaskAction
	public void doAction()
	{
	    File outFile = getProject().file(output);
	    outFile.getParentFile().mkdirs();
		task.execute();
	}
}
