package com.github.aucguy.optifinegradle.lex;

import java.util.Arrays;

public class Printer
{
    int           indention = 0;
    StringBuilder output = new StringBuilder();

    public void printIndention()
    {
        int count = 4 * this.indention;
        char[] indention = new char[count];
        Arrays.fill(indention, ' ');
        this.output.append(new String(indention));
    }

    public void print(String str)
    {
        this.output.append(str);
    }

    public void incrIndention()
    {
        this.indention++;
    }

    public void decrIndention()
    {
        this.indention--;
    }

    public String toString()
    {
        return this.output.toString();
    }
}
