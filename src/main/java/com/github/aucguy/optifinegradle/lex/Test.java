package com.github.aucguy.optifinegradle.lex;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class Test
{
    private static final Pattern SINGLETON_ARRAY_PATTERN = Pattern.compile("new \\w+\\[\\] \\{(?<content>[^{}]+)\\}");

    public static void main(String[] args) throws IOException
    {
        String hunk = "return (Entity)clazz.getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldIn});";
        hunk = SINGLETON_ARRAY_PATTERN.matcher(hunk).replaceAll("${content}");
        System.out.println(hunk);
        InputStream input = Test.class.getResourceAsStream(args[0]);
        byte[] data = new byte[input.available()];
        input.read(data);
        input.close();
        
        String src = new String(data);
        StringBuilder output = new StringBuilder();
        new Rename2().rename(new Lex(src).lex()).reassemble(output);
        System.out.println(output.toString());
    }
}
