package com.github.aucguy.optifinegradle;

import static com.github.aucguy.optifinegradle.OptifineConstants.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.SimpleRemapper;

import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;

public class JoinJars extends CachedTask {
	@InputFile
	public Object client;

	@InputFile
	public Object optifine;
	
	@OutputFile
	@Cached
	public Object obfuscatedClasses;
	
	@OutputFile
	@Cached
	public Object outJar;
	
	private static Remapper renameMappings;
	
	@TaskAction
	public void doAction() throws IOException {
		IOManager manager = new IOManager(this);
		try {
			if(renameMappings == null) {
				loadRenames(manager.openResourceForReading(FIELD_RENAMES));
			}
			copyJarsInto(manager.openFileForWriting(obfuscatedClasses), 
					manager.openZipForWriting(outJar), 
					manager.openZipForReading(optifine),
					manager.openZipForReading(client));
		} finally {
			manager.closeAll();
		}
	}

	private void copyJarsInto(OutputStream obfClasses, ZipOutputStream output, ZipFile... inputs) throws IOException {
		Set<String> copiedEntries = new HashSet<String>();
		boolean optifine = true;
		for (ZipFile input : inputs) {
			for (@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> iter = (Enumeration<ZipEntry>) input.entries(); iter.hasMoreElements();) {
				ZipEntry entry = iter.nextElement();
				if (!entry.isDirectory() && !copiedEntries.contains(entry.getName())) {
					copiedEntries.add(entry.getName());
					output.putNextEntry(new JarEntry(entry.getName()));
					InputStream stream = new BufferedInputStream(input.getInputStream(entry));
					byte[] bytes = IOManager.readAll(stream);
					
					if(optifine && entry.getName().endsWith(".class")) {
						Patching.addObfClass(entry.getName(), obfClasses);
						bytes = processOptifine(bytes);
					}
					output.write(bytes);
					stream.close();
					output.closeEntry();
				}
			}
			optifine = false;
		}
	}
	
	public static byte[] processOptifine(byte[] data) {
		ClassReader reader = new ClassReader(data);
		ClassWriter writer = new ClassWriter(reader, 0);
		RemappingClassAdapter transformer1 = new RemappingClassAdapter(writer, renameMappings);
		FieldRemover transformer2 = new FieldRemover(transformer1);
		reader.accept(transformer2,  ClassReader.EXPAND_FRAMES);
		return writer.toByteArray();
	}
	
	public static void loadRenames(InputStream renamesFile) throws IOException {
		Properties properties = new Properties();
		properties.load(renamesFile);
		
		Map<String, String> renames = new HashMap<String, String>();
		for(String key : properties.stringPropertyNames()) {
			renames.put(key, properties.getProperty(key));
		}
		renameMappings = new SimpleRemapper(renames);
		renamesFile.close();
	}
}
