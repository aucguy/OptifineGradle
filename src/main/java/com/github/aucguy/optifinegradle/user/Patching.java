package com.github.aucguy.optifinegradle.user;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Task;

import com.github.aucguy.optifinegradle.IOManager;

public class Patching
{
    public static void addObfClass(String name, OutputStream obfClasses, Map<String, String> mapping) throws UnsupportedEncodingException, IOException
    {
        if (name.endsWith(".class"))
        {
            name = name.substring(0, name.length() - 6);
        }
        if (mapping.containsKey(name))
        {
            name = mapping.get(name);
            if (name.contains("$"))
            {
                name = name.split("\\$")[0];
            }
            name = name.replace('/', '.');
        }
        obfClasses.write(name.getBytes("UTF-8"));
        obfClasses.write('\n');
    }

    public static Set<String> getIgnoredPatches(Task task, File deobfClasses) throws IOException
    {
        IOManager manager = new IOManager(task);
        Set<String> IPS = null;
        if (deobfClasses != null)
        {
            IPS = IOManager.readLinesAsSet(manager.openFileForReading(deobfClasses));
        }
        return IPS;
    }

    public static boolean shouldSkip(String name, Set<String> ignoredPatches, boolean replace)
    {
        if (ignoredPatches == null)
            return false;
        name = name.endsWith(".java.patch") ? name.substring(0, name.length() - 11) : name;
        name = name.endsWith(".java") ? name.substring(0, name.length() - 5) : name;
        name = replace ? name.replace('/', '.').replace('\\', '.') : name;
        return ignoredPatches.contains(name);
    }
}
