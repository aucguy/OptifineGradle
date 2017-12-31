package com.github.aucguy.optifinegradle.lex;

public class Lex
{
    String   src;
    int      index;
    AstGroup root;
    int line;

    public Lex(String src)
    {
        index = 0;
        this.src = src;
        root = new AstGroup();
    }
    
    void syntaxError(String message)
    {
        throw(new RuntimeException(message + " at " + index));
    }

    char getChar()
    {
        if (index < src.length())
        {
            return src.charAt(index);
        }
        else
        {
            return 0;
        }
    }

    void advance()
    {
        if(getChar() == '\n')
        {
            line++;
        }
        index++;
    }

    void advanceTo(char target)
    {
        while (getChar() != target)
        {
            advance();
        }
    }

    String advanceWhile(String target)
    {
        int start = index;
        while (target.indexOf(getChar()) != -1)
        {
            advance();
        }
        return src.substring(start, index);
    }

    void addTokenLiteral(String value)
    {
        root.content.add(new AstBasic(value, TokenType.LITERAL));
    }

    void addTokenNumber(String value)
    {
        root.content.add(new AstBasic(value, TokenType.NUMBER));
    }

    void addTokenIdentifier(String value)
    {
        root.content.add(new AstBasic(value, TokenType.IDENTIFIER));
    }

    void addTokenSymbol(String value)
    {
        root.content.add(new AstBasic(value, TokenType.SYMBOL));
    }
    
    void addTokenWhitespace(String value)
    {
        root.content.add(new AstBasic(value, TokenType.WHITESPACE));
    }

    void lexLiteral()
    {
        char ends = getChar(); //is it a double or single quote?
        advance(); // go to after the quote
        int start = index;
        boolean slash = false;
        while(getChar() != ends || slash)
        {
            if(slash)
            {
                slash = false;
            }
            else if(getChar() == '\\')
            {
                slash = true;
            }
            advance();
        }
        addTokenLiteral(ends + src.substring(start, index) + ends);
        advance(); // avoid closing quote
    }

    void lexIdentifier()
    {
        addTokenIdentifier(advanceWhile(TokenType.IDENTIFIER.all));
    }

    void lexNumber()
    {
        addTokenNumber(advanceWhile(TokenType.NUMBER.all));
    }

    void lexSymbol()
    {
        addTokenSymbol(advanceWhile(TokenType.SYMBOL.all));
    }

    void lexWhitespace()
    {
        addTokenWhitespace(advanceWhile(TokenType.WHITESPACE.all));
    }

    public AstGroup lex()
    {
        while (index < src.length())
        {
            int begin = index;
            char c = getChar();
            if (TokenType.LITERAL.start.contains(charToString(c)))
            {
                lexLiteral();
            }
            else if (TokenType.IDENTIFIER.start.contains(charToString(c)))
            {
                lexIdentifier();
            }
            else if (TokenType.NUMBER.start.contains(charToString(c)))
            {
                lexNumber();
            }
            else if (TokenType.SYMBOL.start.contains(charToString(c)))
            {
                lexSymbol();
            }
            else if (TokenType.WHITESPACE.start.contains(charToString(c)))
            {
                lexWhitespace();
            }
            else
            {
                syntaxError("invalid character (" + c + ")");
            }
            if (begin == index)
            {
                // prevent infinite loop
                syntaxError("internal error: character not advanced");
            }
        }
        return root;
    }
    
    public static String charToString(char value)
    {
        return new StringBuilder().append(value).toString();
    }
}