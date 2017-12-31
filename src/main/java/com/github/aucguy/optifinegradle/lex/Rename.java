package com.github.aucguy.optifinegradle.lex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public abstract class Rename
{
    //from fernflower ConverterHelper
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(
            "abstract", "do", "if", "package", "synchronized", "boolean", "double", "implements", "private", "this", "break", "else", "import",
            "protected", "throw", "byte", "extends", "instanceof", "public", "throws", "case", "false", "int", "return", "transient", "catch",
            "final", "interface", "short", "true", "char", "finally", "long", "static", "try", "class", "float", "native", "strictfp", "void",
            "const", "for", "new", "super", "volatile", "continue", "goto", "null", "switch", "while", "default", "assert", "enum"));
    
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
                    scopeStack.peek().renames.put(basic.value, handleDeclaration(counter++, basic.value));
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
        if (basic.value.matches("field_.*|p_.*|[A-Z].*") || KEYWORDS.contains(basic.value))
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
    
    public String rename(String src)
    {
        StringBuilder output = new StringBuilder();
        rename(new Lex(src).lex()).reassemble(output);
        return output.toString();
    }
    
    protected abstract AstToken createVariable(String rename);

    protected abstract String handleDeclaration(int counter, String name);

    protected abstract void addToken(AstGroup root, AstToken token);
}
