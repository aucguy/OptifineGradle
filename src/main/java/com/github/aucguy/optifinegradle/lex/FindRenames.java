package com.github.aucguy.optifinegradle.lex;

import java.util.LinkedList;
import java.util.List;

public class FindRenames extends Rename
{
    public List<String> variableNames = new LinkedList<String>();
    
    @Override
    protected AstToken createVariable(String rename)
    {
        return null;
    }

    @Override
    protected String handleDeclaration(int counter, String name)
    {
        variableNames.add(name);
        return "";
    }

    @Override
    protected void addToken(AstGroup root, AstToken token)
    {
    }
}
