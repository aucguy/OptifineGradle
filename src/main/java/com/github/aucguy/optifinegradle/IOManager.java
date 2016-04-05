package com.github.aucguy.optifinegradle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.gradle.api.Task;

import net.minecraftforge.gradle.common.Constants;

/**
 * handles resources
 */
public class IOManager
{
    protected Task           task;
    protected Set<Closeable> handles = new HashSet<Closeable>();

    public IOManager(Task task)
    {
        this.task = task;
    }

    public ZipFile openZipForReading(Object file) throws IOException
    {
        ZipFile f;
        try
        {
            f = new ZipFile(this.getFile(file));
        } catch (IOException e)
        {
            throw new IOException("Couldn't open input zip: " + e.getMessage());
        }
        this.handles.add(f);
        return f;
    }

    public ZipOutputStream openZipForWriting(Object file) throws IOException
    {
        ZipOutputStream f;
        try
        {
            f = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.getFile(file))));
        } catch (IOException e)
        {
            throw new IOException("Couldn't open output zip: " + e.getMessage());
        }
        this.handles.add(f);
        return f;
    }

    public InputStream openFileInZipForReading(Object zip, Object file) throws IOException
    {
        ZipFile z = this.openZipForReading(zip);
        InputStream f;
        try
        {
            f = z.getInputStream(z.getEntry((String) file));
        } catch (IOException e)
        {
            throw new IOException("Couldn't open input file in zip: " + e.getMessage());
        }
        this.handles.add(f);
        return f;

    }

    public BufferedInputStream openFileForReading(Object file) throws IOException
    {
        BufferedInputStream f;
        try
        {
            f = new BufferedInputStream(new FileInputStream(this.getFile(file)));
        } catch (IOException e)
        {
            throw new IOException("Couldn't open input file: " + e.getMessage());
        }
        this.handles.add(f);
        return f;
    }

    public BufferedOutputStream openFileForWriting(Object file) throws IOException
    {
        BufferedOutputStream f;
        try
        {
            f = new BufferedOutputStream(new FileOutputStream(this.getFile(file)));
        } catch (IOException e)
        {
            throw new IOException("Couldn't open input file: " + e.getMessage());
        }
        this.handles.add(f);
        return f;
    }

    public BufferedInputStream openResourceForReading(String file)
    {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedInputStream f = new BufferedInputStream(resource);
        this.handles.add(f);
        return f;
    }

    protected File getFile(Object file)
    {
        File f = this.task.getProject().file(file);
        f.getParentFile().mkdirs();
        return f;
    }

    public void closeAll() throws IOException
    {
        for (Closeable i : this.handles)
        {
            try
            {
                i.close();
            } catch (IOException e)
            {
                throw new IOException("Couldn't close file: " + e.getMessage());
            }
        }
    }

    public static BufferedReader toBufferedReader(InputStream inputStream)
    {
        Reader reader = new InputStreamReader(inputStream);
        return new BufferedReader(reader);
    }

    public static Set<String> readLines(InputStream inputStream) throws IOException
    {
        BufferedReader reader = toBufferedReader(inputStream);
        Set<String> ret = new HashSet<String>();

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

    public static byte[] readAll(InputStream inputStream) throws IOException
    {
        byte[] data = new byte[inputStream.available()];
        inputStream.read(data);
        return data;
    }

    public static String readAllAsString(InputStream inputStream) throws IOException
    {
        return new String(readAll(inputStream), Constants.CHARSET);
    }
}
