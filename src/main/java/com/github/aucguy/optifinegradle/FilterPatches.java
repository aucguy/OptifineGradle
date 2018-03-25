package com.github.aucguy.optifinegradle;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    public List<String> extraExclusions;
    
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
            String name = new File(details.getPath()).getName();
            if(name.endsWith(".java.patch"))
            {
                name = name.substring(0, name.length() - 11);
            }
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
        IOManager manager = new IOManager(this);
        File dest = getProject().file(patchesOut);
        delete(dest);
        dest.mkdirs();
        List<String> exclusions = IOManager.readLines(manager.openFileForReading(excludeList));
        exclusions.addAll(extraExclusions);
        ExtractionVisitor visitor = new ExtractionVisitor(dest, exclusions);
        getProject().fileTree(getProject().file(patchesIn)).visit(visitor);
        manager.closeAll();
    }
    
    private void delete(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File c : f.listFiles())
                delete(c);
        }
        f.delete();
    }
}
