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
package net.minecraftforge.gradle.util.mcp;

import net.minecraftforge.gradle.common.Constants;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

public class FFPatcher
{
    static final String MODIFIERS = "public|protected|private|static|abstract|final|native|synchronized|transient|volatile|strictfp";

    private static final String CALL_METHOD = "(?<main>(\\s)+public Object call\\(\\) throws Exception(\\s)+\\{(\\s)+return this.call\\(\\);(\\s)+\\})";
    private static final String CRASH_REPORT_FIELD = "final CrashReport field_(\\d)+_a;";

    // Remove TRAILING whitespace
    private static final String TRAILING = "(?m)[ \\t]+$";

    //Remove repeated blank lines
    private static final String NEWLINES = "(?m)^(\\r\\n|\\r|\\n){2,}";
    
    private static final String CLASS_REGEX = "(?<modifiers>(?:(?:" + MODIFIERS + ") )*)(?<type>enum|class|interface) (?<name>[\\w$]+)";

    public static String processFile(String text)
    {
        text = text.replaceAll(TRAILING, "");
        text = text.replaceAll(NEWLINES, Constants.NEWLINE);
        
        text = text.replaceAll(CALL_METHOD, ""); //remove synthetic call methods
        if(text.contains("class CrashReport ")) {
            text = text.replaceAll(CRASH_REPORT_FIELD, "");
        }
        Matcher matcher = Pattern.compile(CLASS_REGEX).matcher(text);
        while(matcher.find())
        {
            String oldName = matcher.group("name");
            if(oldName.equals("1") || oldName.equals("2"))
            {
                String newName = "Custom" + oldName;
                text = text.replace("class " + oldName, "class " + newName);
                text = text.replace("." + oldName + ".", "." + newName + ".");
            }
        }
        return text;
    }
}
