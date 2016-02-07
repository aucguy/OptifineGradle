package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

public class OptifineExtension {
	private String patchURL;
	private String patchArchive;
	
	public String getPatchURL() {
		return patchURL == null ? DEFAULT_PATCH_URL : patchURL;
	}
	
	public void setPatchURL(String x) {
		patchURL = x;
	}
	
	public String getPatchArchive() {
		return patchArchive == null ? DEFAULT_PATCH_ARCHIVE : patchArchive;
	}
	
	public void setPatchArchive(String x) {
		patchArchive = x;
	}
}
