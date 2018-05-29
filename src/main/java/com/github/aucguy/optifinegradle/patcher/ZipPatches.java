package com.github.aucguy.optifinegradle.patcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.TaskAction;

import com.github.aucguy.optifinegradle.IOManager;

public class ZipPatches extends DefaultTask
{
    public Object outZip;
    protected Map<Object, Object> files = new HashMap<Object, Object>();
    
    @TaskAction
    public void doAction() throws IOException
    {
        try(ZipOutputStream zip = IOManager.openZipForWriting(this, outZip))
        {
            for(Entry<Object, Object> entry : files.entrySet())
            {
                File input = getProject().file(entry.getKey());
                String output = getProject().relativePath(entry.getValue());
                getProject().fileTree(input).visit(new FileVisitor()
                {
                    @Override
                    public void visitDir(FileVisitDetails details)
                    {
                    }
    
                    @Override
                    public void visitFile(FileVisitDetails details)
                    {
                        String path = new File(output, details.getPath()).getPath();
                        try(InputStream detailsFile = IOManager.openFileForReading(ZipPatches.this, details.getFile()))
                        {
                            zip.putNextEntry(new ZipEntry(path));
                            zip.write(IOManager.readAll(detailsFile));
                            zip.closeEntry();
                        }
                        catch (IOException e)
                        {
                            throw(new RuntimeException(e));
                        }
                    }
                });
            }
        }
    }
    
    public void addFiles(Object input, Object output)
    {
        files.put(input, output);
    }
}
