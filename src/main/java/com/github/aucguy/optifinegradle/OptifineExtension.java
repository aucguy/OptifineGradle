package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

public class OptifineExtension
{
	private String optifineVersion;
    private String patchURL;
    private String patchArchive;
    private String optifineJar;
    
    public String getOptifineVersion()
    {
    	return optifineVersion == null ? DEFAULT_OPTIFINE_VERSION : optifineVersion;
    }
    
    public void setOptifineVersion(String x)
    {
    	optifineVersion = x;
    }
    
    public String getPatchURL()
    {
        return patchURL == null ? DEFAULT_PATCH_URL : patchURL;
    }

    public void setPatchURL(String x)
    {
        patchURL = x;
    }

    public String getPatchArchive()
    {
        return patchArchive == null ? DEFAULT_PATCH_ARCHIVE : patchArchive;
    }

    public void setPatchArchive(String x)
    {
        patchArchive = x;
    }
    
    public String getOptifineJar()
    {
        return optifineJar == null ? DEFAULT_OPTIFINE_JAR : optifineJar;
    }
    
    public void setOptifineJar(String x)
    {
        optifineJar = x;
    }
}
