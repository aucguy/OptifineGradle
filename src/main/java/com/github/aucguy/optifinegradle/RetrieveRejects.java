package com.github.aucguy.optifinegradle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public class RetrieveRejects extends DefaultTask
{
    @InputDirectory
    public Object inFolder;

    @OutputFile
    public Object outZip;

    public class RetrieverVisitor implements FileVisitor
    {
        ZipOutputStream output;

        public RetrieverVisitor(ZipOutputStream output)
        {
            this.output = output;
        }

        @Override
        public void visitDir(FileVisitDetails details)
        {
        }

        @Override
        public void visitFile(FileVisitDetails details)
        {
            String name = details.getName();
            if(name.endsWith(".java.patch.rej"))
            {
                try
                {
                    output.putNextEntry(new ZipEntry(name));
                    InputStream input = new FileInputStream(details.getFile());
                    output.write(IOManager.readAll(input));
                    input.close();
                }
                catch (IOException e)
                {
                    throw(new RuntimeException(e));
                }
            }
        }
    }

    @TaskAction
    public void doTask() throws IOException
    {
        IOManager manager = new IOManager(this);
        ZipOutputStream output = manager.openZipForWriting(outZip);
        getProject().fileTree(inFolder).visit(new RetrieverVisitor(output));
        manager.closeAll();
    }
}
