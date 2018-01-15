package com.github.aucguy.optifinegradle.bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

public class BootstrapClassLoader extends ClassLoader
{
    protected ClassLoader providedDelegate;
    protected ClassLoader forgeDelegate;
    protected String[] overriddenPackages;
    protected Manifest manifest;

    BootstrapClassLoader(ClassLoader providedDelegate, ClassLoader forgeDelegate, String[] overriddenPackages) throws IOException
    {
        this.providedDelegate = providedDelegate;
        this.forgeDelegate = forgeDelegate;
        this.overriddenPackages = overriddenPackages;
        manifest = new Manifest();
        manifest.read(forgeDelegate.getResourceAsStream("META-INF/MANIFEST.MF"));
    }

    @Override
    public InputStream getResourceAsStream(String name)
    {
        InputStream input = providedDelegate.getResourceAsStream(name);
        if(input != null)
        {
            return input;
        }
        input = forgeDelegate.getResourceAsStream(name);
        if(input != null)
        {
            return input;
        }
        return getSystemResourceAsStream(name);
    }

    @Override
    public URL getResource(String name)
    {
        URL input = providedDelegate.getResource(name);
        if(input != null)
        {
            return input;
        }
        input = forgeDelegate.getResource(name);
        if(input != null)
        {
            return input;
        }
        return getSystemResource(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolveIt) throws ClassNotFoundException
    {
        Class<?> clazz = findLoadedClass(name);
        if(clazz != null)
        {
            return clazz;
        }
        if(isPackageOverridden(name))
        {
            InputStream input = getResourceAsStream(name.replace('.', '/') + ".class");
            if(input != null)
            {
                byte[] data;
                try
                {
                    data = readAll(input);
                }
                catch (IOException e)
                {
                    throw(new RuntimeException(e));
                }

                clazz = defineClass(name, data, 0, data.length);
            }
            String pkgname = name.substring(0, name.lastIndexOf('.'));
            if(getPackage(pkgname) == null)
            {
                createPackage(pkgname);
            }
        }
        else
        {
            clazz = providedDelegate.loadClass(name);
        }

        if(resolveIt)
        {
            resolveClass(clazz);
        }
        return clazz;
    }

    //from java.net.URLClassLoader.definePackage
    private void createPackage(String name)
    {
        String path = name.replace('.', '/').concat("/");
        String specTitle = null, specVersion = null, specVendor = null;
        String implTitle = null, implVersion = null, implVendor = null;
        String sealed = null;
        URL sealBase = null;

        Attributes attr = manifest.getAttributes(path);
        if (attr != null)
        {
            specTitle   = attr.getValue(Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            specVendor  = attr.getValue(Name.SPECIFICATION_VENDOR);
            implTitle   = attr.getValue(Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            implVendor  = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            sealed      = attr.getValue(Name.SEALED);
        }
        attr = manifest.getMainAttributes();
        if (attr != null)
        {
            if (specTitle == null)
            {
                specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null)
            {
                specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null)
            {
                specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null)
            {
                implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null)
            {
                implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null)
            {
                implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null)
            {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    protected boolean isPackageOverridden(String name)
    {
        while(name.indexOf('.') != -1)
        {
            if(arrayContains(overriddenPackages, name))
            {
                return true;
            }
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return arrayContains(overriddenPackages, name);
    }

    public static <T> boolean arrayContains(T[] array, T item)
    {
        for(T i : array)
        {
            if(i.equals(item))
            {
                return true;
            }
        }
        return false;
    }

    //from https://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
    //TODO make this used in IOManager.readAll
    public static byte[] readAll(InputStream stream) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = stream.read(data, 0, data.length)) != -1)
        {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}