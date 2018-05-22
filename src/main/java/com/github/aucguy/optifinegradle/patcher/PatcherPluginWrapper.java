package com.github.aucguy.optifinegradle.patcher;

import java.lang.reflect.Method;
import java.util.List;

import org.gradle.api.NamedDomainObjectContainer;

import com.github.aucguy.optifinegradle.ReflectHelper;

import net.minecraftforge.gradle.patcher.PatcherPlugin;
import net.minecraftforge.gradle.patcher.PatcherProject;

public class PatcherPluginWrapper
{
    protected static final Method sortByPatchingMethod = ReflectHelper.retrieveMethod(PatcherPlugin.class, "sortByPatching", NamedDomainObjectContainer.class);
    protected static final Method projectStringMethod = ReflectHelper.retrieveMethod(PatcherPlugin.class, "projectString", String.class, PatcherProject.class);
    
    @SuppressWarnings("unchecked")
    public static List<PatcherProject> sortByPatching(OptifinePatcherPlugin self, NamedDomainObjectContainer<PatcherProject> projects)
    {
        return (List<PatcherProject>) ReflectHelper.invoke(sortByPatchingMethod, self, projects);
    }

    public static String projectString(OptifinePatcherPlugin self, String str, PatcherProject project)
    {
        return (String) ReflectHelper.invoke(projectStringMethod, self, str, project);
    }
}
