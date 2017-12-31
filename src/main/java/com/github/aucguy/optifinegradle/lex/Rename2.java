package com.github.aucguy.optifinegradle.lex;

import java.util.Stack;

public class Rename2
{
    public static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";

    public AstGroup rename(AstGroup root)
    {
        int counter = 0;
        AstGroup newRoot = new AstGroup();
        Stack<Scope> scopeStack = new Stack<Scope>();
        scopeStack.push(new Scope(null));
        //any identifiers followed by a dot are not variables
        boolean lastDot = false;

        for(int i = 0; i < root.content.size(); i++)
        {
            AstToken child = root.content.get(i);
            if(!(child instanceof AstBasic))
            {
                throw (new RuntimeException("type not implemented"));
            }
            AstBasic basic = (AstBasic) child;
            if(basic.type == TokenType.SYMBOL)
            {
                //enter and exit blocks
                for(int k = 0; k < basic.value.length(); k++)
                {
                    char c = basic.value.charAt(k);
                    if(c == '{')
                    {
                        scopeStack.push(new Scope(scopeStack.peek()));
                    }
                    else if(c == '}')
                    {
                        scopeStack.pop();
                    }
                }
                addToken(newRoot, basic);
            }
            else if(basic.type == TokenType.IDENTIFIER && !lastDot)
            {
                //local variable declarations
                if(localVariableDeclaration(root, i))
                {
                    scopeStack.peek().renames.put(basic.value, handleDeclaration(counter++));
                }
                String rename = scopeStack.peek().getRename(basic.value);
                if(rename == null)
                {
                    addToken(newRoot, basic);
                }
                else
                {                   
                    addToken(newRoot, createVariable(rename));
                }
            }
            else
            {
                addToken(newRoot, basic);
            }
            lastDot = basic.type == TokenType.SYMBOL && basic.value.equals(".");
        }
        return newRoot;
    }

    //matches 'x =' or 'x;'
    private boolean localVariableDeclaration(AstGroup root, int i)
    {
        if (i >= root.content.size() - 1)
        {
            return false;
        }
        AstToken token = root.content.get(i);
        if (!isType(token, TokenType.IDENTIFIER))
        {
            return false;
        }
        AstBasic basic = (AstBasic) token;

        if (basic.value.startsWith("field") || !LOWER_CASE.contains(Lex.charToString(basic.value.charAt(0))))
        {
            return false;
        }
        int offset = isType(root.content.get(i + 1), TokenType.WHITESPACE) ? 2 : 1;
        if (i >= root.content.size() - offset)
        {
            return false;
        }
        token = root.content.get(i + offset);
        if (!isType(token, TokenType.SYMBOL))
        {
            return false;
        }
        basic = (AstBasic) token;
        if (basic.value.equals("=") || basic.value.equals(";"))
        {
            return true;
        }
        return false;
    }

    private boolean isType(AstToken token, TokenType type)
    {
        return token instanceof AstBasic && ((AstBasic) token).type == type;
    }
    
    protected AstToken createVariable(String rename)
    {
        return new AstBasic(rename, TokenType.IDENTIFIER);
    }

    protected String handleDeclaration(int counter)
    {
        return "v_" + counter;
    }

    protected void addToken(AstGroup root, AstToken basic)
    {
        root.content.add(basic);
    }
}
