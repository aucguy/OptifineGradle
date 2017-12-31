package com.github.aucguy.optifinegradle.lex;

public enum TokenType
{
    LITERAL("\"'"),
    IDENTIFIER("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_", "0123456789"),
    SYMBOL("`~!@#$%^&*()-=+[{]}\\\\|;:,<.>/?"),
    NUMBER("0123456789"),
    WHITESPACE(" \r\n\t");
    
    String start;
    String all;
    
    TokenType(String start, String rest)
    {
        this.start = start;
        this.all = start + rest;
    }
    
    TokenType(String start)
    {
        this(start, "");
    }
   
}
