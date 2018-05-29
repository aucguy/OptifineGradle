package com.github.aucguy.optifinegradle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import net.minecraftforge.gradle.util.caching.CachedTask;

public class FilterPatches extends CachedTask
{
    @InputDirectory
    public Object patchesIn;
    
    @InputFile
    public Object excludeList;
    
    @Input
    public Object extraExclusions;
    
    @OutputDirectory
    public Object patchesOut;
    
    public class ExtractionVisitor implements FileVisitor
    {
        private final File outputDir;
        private final List<String> exclusions;

        public ExtractionVisitor(File outDir, List<String> exclusions)
        {
            this.outputDir = outDir;
            this.exclusions = exclusions;
        }

        @Override
        public void visitDir(FileVisitDetails details)
        {
            new File(outputDir, details.getPath()).mkdirs();
        }

        @Override
        public void visitFile(FileVisitDetails details)
        {
            File file = new File(details.getPath());
            List<String> parts = new LinkedList<String>();
            while(file != null)
            {
                parts.add(0, file.getName());
                file = file.getParentFile();
            }
            String name = String.join(".", parts);
            if(name.endsWith(".java.patch"))
            {
                name = name.substring(0, name.length() - 11);
            }
            //TODO remove null case
            if(name == null || exclusions.contains(name))
            {
                return;
            }

            File out = new File(outputDir, details.getPath());
            out.getParentFile().mkdirs();
            details.copyTo(out);
        }
    }

    @TaskAction
    public void doAction() throws IOException
    {
        File dest = getProject().file(patchesOut);
        IOManager.delete(dest);
        dest.mkdirs();
        List<String> exclusions;
        try(InputStream exclusionsFile = IOManager.openFileForReading(this, excludeList))
        {
            exclusions = IOManager.readLines(exclusionsFile);
        }
        
        if(extraExclusions != null)
        {
            try(InputStream exclusionsFile = IOManager.openFileForReading(this, extraExclusions))
            {
                exclusions.addAll(IOManager.readLines(exclusionsFile));
            }
        }
        ExtractionVisitor visitor = new ExtractionVisitor(dest, exclusions);

        File input = getProject().file(patchesIn);
        FileTree tree;
        if(input.isDirectory())
        {
            tree = getProject().fileTree(input);
        }
        else
        {
            tree = getProject().zipTree(input);
        }
        tree.visit(visitor);
    }
}
