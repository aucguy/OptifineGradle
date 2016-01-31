package com.github.aucguy.optifinegradle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

import net.md_5.specialsource.JarMapping;
import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.caching.CachedTask;
import net.minecraftforge.gradle.util.delayed.DelayedFile;

public class JoinJars extends CachedTask {
	@InputFile
	public Object client;

	@InputFile
	public Object optifine;
	
	@InputFile
	public DelayedFile srg;
	
	@OutputFile
	@Cached
	public Object obfuscatedClasses;
	
	@OutputFile
	@Cached
	public Object outJar;
	
	@TaskAction
	public void doAction() throws IOException {
		IOManager manager = new IOManager(this);
		try {
			JarMapping m = new JarMapping(); //load renames
			BufferedReader reader = new BufferedReader(new FileReader(getProject().file(srg)));
			m.loadMappings(reader, null, null, true);
			Map<String, String> renames = new HashMap<String, String>();
			for(Entry<String, String> entry : m.fields.entrySet()) {
				String[] parts = entry.getKey().split("/(?=[^/]*$)");
				renames.put(m.classes.get(parts[0]) + ".val$" + parts[1], entry.getValue());
			}
			for(Entry<String, String> entry : m.classes.entrySet()) {
				renames.put(entry.getValue() + ".this$0", "c");
			}
			Remapper mapping = new SimpleRemapper(renames);
			
			copyJarsInto(manager.openFileForWriting(obfuscatedClasses), 
					manager.openZipForWriting(outJar),
					mapping,
					manager.openZipForReading(optifine),
					manager.openZipForReading(client));
		} finally {
			manager.closeAll();
		}
	}

	@SuppressWarnings("unchecked")
	private void copyJarsInto(OutputStream obfClasses, ZipOutputStream output, Remapper mapping, ZipFile... inputs) throws IOException {
		Set<String> copiedEntries = new HashSet<String>();
		boolean optifine = true;
		
		for (ZipFile input : inputs) {
			for (Enumeration<ZipEntry> iter = (Enumeration<ZipEntry>) input.entries(); iter.hasMoreElements();) {
				ZipEntry entry = iter.nextElement();
				if (!entry.isDirectory() && !copiedEntries.contains(entry.getName())) {
					copiedEntries.add(entry.getName());
					
					output.putNextEntry(new JarEntry(entry.getName()));
					InputStream stream = new BufferedInputStream(input.getInputStream(entry));
					byte[] bytes = IOManager.readAll(stream);
					
					if(optifine && entry.getName().endsWith(".class")) {
						Patching.addObfClass(entry.getName(), obfClasses);
						bytes = processOptifine(bytes, mapping);
					}
					
					output.write(bytes);
					stream.close();
					output.closeEntry();
				}
			}
			optifine = false;
		}
	}
	
	public static byte[] processOptifine(byte[] data, Remapper mapping) {
		ClassReader reader = new ClassReader(data);
		ClassWriter writer = new ClassWriter(reader, 0);
		RemappingClassAdapter transformer1 = new RemappingClassAdapter(writer, mapping);
		FieldRemover transformer2 = new FieldRemover(transformer1);
		reader.accept(transformer2,  ClassReader.EXPAND_FRAMES);
		return writer.toByteArray();
	}
}
