package com.github.aucguy.optifinegradle;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;

import com.github.aucguy.optifinegradle.lex.ApplyRenames;
import com.github.aucguy.optifinegradle.user.Patching;

import net.minecraftforge.gradle.tasks.AbstractEditJarTask;

public class ApplyRenamesTask extends AbstractEditJarTask
{
    @InputFile
    public Object ignoredSources;
    
    @InputDirectory
    public Object renamesDir;
    
    protected Set<String> skipping;
    
    @Override
    public void doStuffBefore() throws Exception
    {
        skipping = Patching.getIgnoredPatches(this, getIgnoredSources());
    }

    @Override
    public String asRead(String name, String file) throws Exception
    {
        if(!Patching.shouldSkip(name, skipping, true))
        {
            System.out.println(name);
            IOManager manager = new IOManager(this);
            try
            {
                File renamefile = new File(getRenamesDir(), name.replace(".java", ""));
                if(renamefile.exists())
                {
                    InputStream renamesInput = manager.openFileForReading(renamefile);
                    List<String> renames = IOManager.readLines(renamesInput);
                    ApplyRenames applier = new ApplyRenames(renames);
                    file = applier.rename(file);
                }
            }
            finally
            {
                manager.closeAll();
            }
        }
        return file;
    }

    @Override
    public void doStuffMiddle(Map<String, String> sourceMap, Map<String, byte[]> resourceMap) throws Exception
    {
    }

    @Override
    public void doStuffAfter() throws Exception
    {
    }

    @Override
    protected boolean storeJarInRam()
    {
        return false;
    }
    
    public File getIgnoredSources()
    {
        return getProject().file(ignoredSources);
    }
    
    public File getRenamesDir()
    {
        return getProject().file(renamesDir);
    }
}
