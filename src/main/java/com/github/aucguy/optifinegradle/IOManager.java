package com.github.aucguy.optifinegradle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.gradle.api.Task;

import net.minecraftforge.gradle.common.Constants;

/**
 * handles resources
 */
public class IOManager
{
    public static ZipFile openZipForReading(Task task, Object file) throws IOException
    {
        return new ZipFile(getFile(task, file));
    }

    public static ZipOutputStream openZipForWriting(Task task, Object file) throws IOException
    {
        File f = getFile(task, file);
        f.getParentFile().mkdirs();
        return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
    }

    public static InputStream openFileInZipForReading(Task task, Object zip, String file) throws IOException
    {
        ZipFile z = openZipForReading(task, zip);
        return openFileInZipForReading(task, z, z.getEntry(file));
    }
    
    public static InputStream openFileInZipForReading(Task task, ZipFile z, ZipEntry file) throws IOException
    {
        return z.getInputStream(file);
    }
    
    public static InputStream openFileSomewhereForReading(Task task, Object obj) throws IOException
    {
    	File file = getFile(task, obj);
    	if(file.exists()) //directly in filesystem
    	{
    		return openFileForReading(task, file);
    	}
    	
    	File archive = file;
    	while(archive != null && !archive.exists()) //nonexistant or in archive
    	{
    		archive = archive.getParentFile();
    	}
    	
    	if(archive != null && archive.isFile()) //in archive
    	{
    		return openFileInZipForReading(task, archive, archive.toPath().relativize(file.toPath()).toString());
    	}
    	else //nonexistant
    	{
    		throw new IOException("file does not exist: " + file.getAbsolutePath());
    	}
    }

    public static BufferedInputStream openFileForReading(Task task, Object file) throws IOException
    {
        return new BufferedInputStream(new FileInputStream(getFile(task, file)));
    }

    public static BufferedOutputStream openFileForWriting(Task task, Object file) throws IOException
    {
        File f = getFile(task, file);
        f.getParentFile().mkdirs();
        return new BufferedOutputStream(new FileOutputStream(f));
    }
    
    protected static File getFile(Task task, Object file)
    {
    	return task.getProject().file(file);
    }
    
    public static BufferedReader toBufferedReader(InputStream inputStream)
    {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
    
    //from https://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
    public static byte[] readAll(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data)) != -1)
        {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
    
    public static String readAllAsString(InputStream inputStream) throws IOException
    {
        return new String(readAll(inputStream), Constants.CHARSET);
    }


    public static List<String> readLines(InputStream inputStream) throws IOException
    {
        BufferedReader reader = toBufferedReader(inputStream);
        List<String> ret = new LinkedList<String>();

        String line;
        if (reader.ready())
        {
            while ((line = reader.readLine()) != null)
            {
                line = line.split("#")[0].trim();
                ret.add(line);
            }
        }
        return ret;
    }

    public static Set<String> readLinesAsSet(InputStream inputStream) throws IOException
    {
        return new HashSet<String>(readLines(inputStream));
    }
    
    public static void delete(File f) throws IOException
    {
        if (f.isDirectory())
        {
            for (File c : f.listFiles())
            {
                delete(c);
            }
        }
        f.delete();
    }
}
