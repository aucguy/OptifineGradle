package com.github.aucguy.optifinegradle.lex;

import java.util.List;

public class ApplyRenames extends Rename
{
    protected List<String> renames;
    
    public ApplyRenames(List<String> renames)
    {
        this.renames = renames;
    }
    
    @Override
    protected AstToken createVariable(String rename)
    {
        return new AstBasic(rename, TokenType.IDENTIFIER);
    }

    @Override
    protected String handleDeclaration(int counter, String name)
    {
        return renames.get(counter);
    }

    @Override
    protected void addToken(AstGroup root, AstToken token)
    {
        root.content.add(token);
    }
}
