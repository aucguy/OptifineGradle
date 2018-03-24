package com.github.aucguy.optifinegradle;

import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

import net.minecraftforge.gradle.tasks.AbstractEditJarTask;

public class RemoveExtras extends AbstractEditJarTask
{
    static final String MODIFIERS = "public|protected|private|static|abstract|final|native|synchronized|transient|volatile|strictfp";
    private static final String CALL_METHOD = "(?<main>(\\s)+public Object call\\(\\) throws Exception(\\s)+\\{(\\s)+return this.call\\(\\);(\\s)+\\})";
    private static final String CRASH_REPORT_FIELD = "final CrashReport field_(\\d)+_a;";
    private static final String CLASS_REGEX = "(?<modifiers>(?:(?:" + MODIFIERS + ") )*)(?<type>enum|class|interface) (?<name>[\\w$]+)";
    
    @Override
    public void doStuffBefore() throws Exception
    {
    }

    @Override
    public String asRead(String name, String file) throws Exception
    {
        file = file.replaceAll(CALL_METHOD, ""); //remove synthetic call methods
        if(file.contains("class CrashReport "))
        {
            file = file.replaceAll(CRASH_REPORT_FIELD, "");
        }
        Matcher matcher = Pattern.compile(CLASS_REGEX).matcher(file);
        while(matcher.find())
        {
            String oldName = matcher.group("name");
            if(oldName.equals("1") || oldName.equals("2"))
            {
                String newName = "Custom" + oldName;
                file = file.replace("class " + oldName, "class " + newName);
                file = file.replace("." + oldName + ".", "." + newName + ".");
            }
        }
        return file;
    }

    @Override
    public void doStuffMiddle(Map<String, String> sourceMap, Map<String, byte[]> resourceMap) throws Exception
    {
    }

    @Override
    public void doStuffAfter() throws Exception
    {
    }

    @Override
    protected boolean storeJarInRam()
    {
        return false;
    }
}
