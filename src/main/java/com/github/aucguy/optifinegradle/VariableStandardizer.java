package com.github.aucguy.optifinegradle;

import java.util.Map;

import com.github.aucguy.optifinegradle.lex.Lex;
import com.github.aucguy.optifinegradle.lex.Rename2;

import net.minecraftforge.gradle.tasks.AbstractEditJarTask;

public class VariableStandardizer extends AbstractEditJarTask
{
    @Override
    public void doStuffBefore() throws Exception
    {
    }

    @Override
    public String asRead(String name, String file) throws Exception
    {
        System.out.println(name);
        if(name.equals("net/minecraft/src/HttpPipelineConnection.java") || name.equals("shadersmod/client/ShaderParser.java"))
        {
            //the decompiler messes up the quotations, so the lexer can't handle it
            return file;
        }
        StringBuilder output = new StringBuilder();
        new Rename2().rename(new Lex(file).lex()).reassemble(output);
        return output.toString();
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
