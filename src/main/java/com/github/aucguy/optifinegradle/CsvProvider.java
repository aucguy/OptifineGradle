package com.github.aucguy.optifinegradle;

import java.io.File;

public interface CsvProvider
{
	File getMethodsCsv();
	File getFieldsCsv();
	File getParamsCsv();
	boolean addsJavadocs();
}
