package com.github.aucguy.optifinegradle;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

import au.com.bytecode.opencsv.CSVReader;
import net.minecraftforge.gradle.common.Constants;

public class CsvData
{
    public Map<String, String> methods;
    public Map<String, String> methodDocs;
    public Map<String, String> fields;
    public Map<String, String> fieldDocs;
    public Map<String, String> params;
    
   protected CsvData(Map<String, String> methods, Map<String, String> methodDocs,
		   Map<String, String> fields, Map<String, String> fieldDocs, Map<String, String> params)
   {
	   this.methods = methods;
	   this.methodDocs = methodDocs;
	   this.fields = fields;
	   this.fieldDocs = fieldDocs;
	   this.params = params;
   }
   
   public static CsvData create(CsvProvider provider) throws IOException
   {
	    Map<String, String> methods      = Maps.newHashMap();
	    Map<String, String> methodDocs   = Maps.newHashMap();
	    Map<String, String> fields       = Maps.newHashMap();
	    Map<String, String> fieldDocs    = Maps.newHashMap();
	    Map<String, String> params       = Maps.newHashMap();
	   
       // read CSV files
       CSVReader reader = Constants.getReader(provider.getMethodsCsv());
       for (String[] s : reader.readAll())
       {
           methods.put(s[0], s[1]);
           if (!s[3].isEmpty() && provider.addsJavadocs())
               methodDocs.put(s[0], s[3]);
       }

       reader = Constants.getReader(provider.getFieldsCsv());
       for (String[] s : reader.readAll())
       {
           fields.put(s[0], s[1]);
           if (!s[3].isEmpty() && provider.addsJavadocs())
               fieldDocs.put(s[0], s[3]);
       }

       reader = Constants.getReader(provider.getParamsCsv());
       for (String[] s : reader.readAll())
       {
           params.put(s[0], s[1]);
       }
       
       return new CsvData(methods, methodDocs, fields, fieldDocs, params);
   }
}
