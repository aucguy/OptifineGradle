package com.github.aucguy.optifinegradle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public class ExtractRenames extends DefaultTask
{
    @Input
    public Object inZip;
    
    @OutputFile
    public Object extractTo;
    
    @TaskAction
    public void doAction() throws IOException
    {
        IOManager manager = new IOManager(this);
        try
        {
            InputStream input = manager.openFileSomewhereForReading(inZip);
            OutputStream output = manager.openFileForWriting(extractTo);
            output.write(IOManager.readAll(input));
        }
        finally
        {
            manager.closeAll();
        }
    }
}
