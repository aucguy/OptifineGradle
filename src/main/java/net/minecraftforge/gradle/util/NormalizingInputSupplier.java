/*
 * A Gradle plugin for the creation of Minecraft mods and MinecraftForge plugins.
 * Copyright (C) 2013 Minecraft Forge
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package net.minecraftforge.gradle.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.srg2source.util.io.InputSupplier;

public class NormalizingInputSupplier implements InputSupplier
{
    private static final char[] SEPARATORS = new char[]{'/', '\\'};
    
    protected InputSupplier parent;
    protected Map<String, InputStream> files = new HashMap<String, InputStream>();
    protected char separator;
    
    public NormalizingInputSupplier(InputSupplier parent, char separator)
    {
        this.parent = parent;
        this.separator = separator;
        
        for(String path : parent.gatherAll(""))
        {
            String normPath = normalize(path, separator);
            files.put(normPath, parent.getInput(path));
        }
    }
    
    public InputSupplier getParent()
    {
        return parent;
    }
    
    @Override
    public void close() throws IOException
    {
        parent.close();
    }

    @Override
    public String getRoot(String resource)
    {
        return parent.getRoot(resource);
    }

    @Override
    public InputStream getInput(String relPath)
    {
        return files.get(normalize(relPath, separator));
    }

    @Override
    public List<String> gatherAll(String endFilter)
    {
        LinkedList<String> out = new LinkedList<String>();

        for (String key : files.keySet())
            if (key.endsWith(endFilter))
                out.add(key);

        return out;
    }
    
    public static String normalize(String path, char separator)
    {
        for(char i : SEPARATORS)
            path = path.replace(i, separator);
        return path;
    }
}
