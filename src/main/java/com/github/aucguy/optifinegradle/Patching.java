package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Task;

import net.minecraftforge.gradle.tasks.DeobfuscateJar.ErroringRemappingAccessMap;

public class Patching {
	public static void addObfClass(String name, OutputStream obfClasses) throws UnsupportedEncodingException, IOException {
		if(name.endsWith(".class")) {
			name = name.substring(0, name.length() - 6);
		}
		obfClasses.write(name.getBytes("UTF-8"));
		obfClasses.write('\n');
	}

	public static void deobfuscatedList(Task task, File obfClasses, File deobfClasses, Map<String, String> mapping) throws IOException {
		if(obfClasses == null || deobfClasses == null) return;
		
		IOManager manager = new IOManager(task);
		BufferedReader obf = new BufferedReader(new InputStreamReader(manager.openFileForReading(obfClasses)));
		BufferedOutputStream deobf = manager.openFileForWriting(deobfClasses);
		Set<String> classes = new HashSet<String>();
		
		String name;
		while((name = obf.readLine()) != null) {
			if(mapping.containsKey(name)) {
				name = mapping.get(name);
				if(name.contains("$")) {
					name = name.split("\\$")[0];
				}
				classes.add(name.replace('/', '.'));
			}
		}
		
		for(String i : classes) {
			deobf.write(i.getBytes("UTF-8"));
			deobf.write('\n');
		}
		manager.closeAll();
	}
	
    public static void removeIgnoredBrokenLines(Task task, ErroringRemappingAccessMap accessMap) throws IOException {
    	IOManager manager = new IOManager(task);
    	Set<String> lines = IOManager.readLines(manager.openResourceForReading(IGNORED_BROKEN_LINES));
    	for(String line : lines) {
    		if(accessMap.brokenLines.containsKey(line)) {
    			accessMap.brokenLines.remove(line);
    		}
    	}
    	manager.closeAll();
	}
    
    public static Set<String> getIgnoredPatches(Task task, File deobfClasses) throws IOException {
        IOManager manager = new IOManager(task);
        Set<String> IPS = null;
        if(deobfClasses != null) {
        	IPS = IOManager.readLines(manager.openFileForReading(deobfClasses));
        }
        return IPS;
    }
    
    public static boolean shouldSkip(String name, Set<String> ignoredPatches, boolean replace) {
    	if(ignoredPatches == null) return false;
    	name = name.endsWith(".java.patch") ? name.substring(0, name.length() - 11) : name;
    	name = replace ? name.replace('/', '.') : name;
    	return ignoredPatches.contains(name);
    }
}
