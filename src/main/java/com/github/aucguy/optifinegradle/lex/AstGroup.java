package com.github.aucguy.optifinegradle.lex;

import java.util.LinkedList;
import java.util.List;

public class AstGroup extends AstToken
{
    public List<AstToken> content;

    public AstGroup()
    {
        this.content = new LinkedList<AstToken>();
    }

    @Override
    public void print(Printer output)
    {
        output.printIndention();
        output.print("group");
        output.incrIndention();
        for (AstToken i : this.content)
        {
            i.print(output);
        }
        output.decrIndention();
    }

    @Override
    public void reassemble(StringBuilder output)
    {
        for(AstToken child : content)
        {
            child.reassemble(output);
        }
    }
}
