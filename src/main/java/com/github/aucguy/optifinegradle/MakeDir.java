package com.github.aucguy.optifinegradle;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class MakeDir extends DefaultTask
{
    public Object directory;
    
    @TaskAction
    public void doAction() throws IOException
    {
        File file = getProject().file(directory);
        IOManager.delete(file);
        file.mkdirs();
    }
}
