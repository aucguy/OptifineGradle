package com.github.aucguy.optifinegradle;

import java.io.PrintStream;

public final class Output
{
    private static boolean initialized = false;
    private static PrintStream output;
    
    public static void init()
    {
        if(!initialized)
        {
            initialized = true;
            output = System.out;
        }
        
    }
    
    public static void println(String message)
    {
        output.println(message);
    }
}
