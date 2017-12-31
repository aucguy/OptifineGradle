package com.github.aucguy.optifinegradle.lex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestRenames
{
    public static void main(String[] args) throws IOException
    {
        String src = readFile(args[0]);
        List<String> renames = Arrays.asList(readFile(args[1]).split("\n"));
        renames = renames.stream().map(i -> i.trim()).collect(Collectors.toList());
        System.out.println(new ApplyRenames(renames).rename(src));
    }
    
    public static String readFile(String name) throws IOException
    {
        InputStream input = TestRenames.class.getResourceAsStream(name);
        byte[] data = new byte[input.available()];
        input.read(data);
        input.close();
        return new String(data);
    }
}
