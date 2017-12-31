package com.github.aucguy.optifinegradle.lex;

import java.util.HashMap;
import java.util.Map;

class Scope
{
    Scope parent;
    Map<String, String> renames = new HashMap<String, String>();
    
    Scope(Scope parent)
    {
        this.parent = parent;
    }
    
    String getRename(String identifier)
    {
        String rename = renames.get(identifier);
        if(rename == null && parent != null)
        {
            return parent.getRename(identifier);
        }
        else
        {
            return rename;
        }
    }
}
