package com.github.aucguy.optifinegradle.patcher;

import org.gradle.api.Task;

import com.github.aucguy.optifinegradle.ReflectHelper;

public class TaskGenSubProjectsWrapper
{
    static final String CLASS_NAME = "net.minecraftforge.gradle.patcher.TaskGenSubprojects";

    static void removeProject(Task self, String name)
    {
        ReflectHelper.invoke(ReflectHelper.retrieveMethod(CLASS_NAME, "removeProject", String.class), self, name);
    }
}
