package com.github.aucguy.optifinegradle;

import java.util.zip.ZipEntry;

import net.minecraftforge.gradle.tasks.RemapSources;

public class RemapRejects extends RemapSources
{
    protected boolean isSourceFile(ZipEntry entry)
    {
        return super.isSourceFile(entry) || entry.getName().endsWith(".java.patch.rej");
    }
}
