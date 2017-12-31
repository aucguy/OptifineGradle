package com.github.aucguy.optifinegradle.lex;

public abstract class AstToken
{
    abstract void print(Printer output);
    abstract void reassemble(StringBuilder output);
}
