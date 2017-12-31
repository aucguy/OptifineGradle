package com.github.aucguy.optifinegradle.lex;

public class AstBasic extends AstToken
{
    String value;
    TokenType type;
    
    protected AstBasic(String value, TokenType type)
    {
        this.value = value;
        this.type = type;
    }

    @Override
    public void print(Printer output)
    {
        output.printIndention();
        output.print(type.name());
        output.print(" '");
        output.print(value);
        output.print("'");
        output.print("\n");
    }

    @Override
    void reassemble(StringBuilder output)
    {
        output.append(value);
    }
}
