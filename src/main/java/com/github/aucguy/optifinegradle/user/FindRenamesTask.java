package com.github.aucguy.optifinegradle.user;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import com.github.aucguy.optifinegradle.IOManager;
import com.github.aucguy.optifinegradle.lex.FindRenames;

import net.minecraftforge.gradle.common.Constants;

public class FindRenamesTask extends DefaultTask
{
    @InputFile
    public Object inJar;
    
    @OutputDirectory
    public Object outFolder;

    @TaskAction
    public void doAction() throws IOException
    {
        FileUtils.deleteDirectory(getOutFolder());
        IOManager manager = new IOManager(this);
        try
        {
            ZipFile input = manager.openZipForReading(inJar);
            for (@SuppressWarnings("unchecked")
            Enumeration<ZipEntry> iter = (Enumeration<ZipEntry>) input.entries(); iter.hasMoreElements();)
            {
                ZipEntry entry = iter.nextElement();
                if(entry.isDirectory() || !entry.getName().endsWith(".java"))
                {
                    continue;
                }
                handleSource(input, entry, manager);

                
            }
        }
        finally
        {
            manager.closeAll();
        }
    }

    private void handleSource(ZipFile input, ZipEntry entry, IOManager manager) throws IOException
    {
        InputStream stream = new BufferedInputStream(manager.openFileInZipForReading(input, entry));
        byte[] data = IOManager.readAll(stream);
        stream.close();
        
        FindRenames finder = new FindRenames();
        finder.rename(new String(data));
        
        File file = new File(getOutFolder(), entry.getName().replace(".java", ""));
        OutputStream out = manager.openFileForWriting(file);
        for(String name : finder.variableNames)
        {
            out.write((name + Constants.NEWLINE).getBytes(Charset.forName("UTF-8")));
        }
        out.close();
    }
    
    public File getOutFolder()
    {
        return new File(Constants.resolveString(outFolder));
    }
}
