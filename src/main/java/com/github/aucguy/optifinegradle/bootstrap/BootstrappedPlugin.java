package com.github.aucguy.optifinegradle.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class BootstrappedPlugin implements Plugin<Project>
{
    public static final String CUSTOM_CONFIG = "optifinegradleWrapped";
    public static final String FORGE_COORD = "net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT";

    protected String mainPlugin;

    public static final PrintStream sysErr = System.err;

    public BootstrappedPlugin(String mainPlugin)
    {
        this.mainPlugin = mainPlugin;
    }

    @Override
    public void apply(Project project)
    {
        try
        {
            doApply(project);
        }
        catch(Throwable exception)
        {
            throw(new RuntimeException(exception));
        }
    }

    public void doApply(Project project) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        Configuration config = project.getBuildscript().getConfigurations().create(CUSTOM_CONFIG);
        config.setTransitive(false);
        config.getDependencies().add(project.getDependencies().create(FORGE_COORD));
        Set<File> files = config.resolve();

        URL[] urls = new URL[files.size()];
        int i = 0;
        for(File file : files)
        {
            urls[i++] = file.toURI().toURL();
        }

        ClassLoader classloader = new BootstrapClassLoader(this.getClass().getClassLoader(), new URLClassLoader(urls));

        Class<?> clazz = classloader.loadClass(mainPlugin);
        ((Plugin<Project>) clazz.newInstance()).apply(project);
    }
}
