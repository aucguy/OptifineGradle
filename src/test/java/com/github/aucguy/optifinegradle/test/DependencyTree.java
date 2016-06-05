package com.github.aucguy.optifinegradle.test;

import java.util.HashSet;
import java.util.Set;

import org.gradle.api.Task;

public class DependencyTree
{
    public static String getDependencies(Task task)
    {
        return getDependencies(task, "", new HashSet<Task>());
    }
    
    private static String getDependencies(Task task, String prefix, Set<Task> visited)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(task.getName());
        if(visited.contains(task))
        {
            sb.append(" ^");
        }
        sb.append("\n");
        if(!visited.contains(task))
        {
            prefix += "    ";
            for(Object i : task.getDependsOn())
            {
                if(i instanceof String)
                {
                    i = task.getProject().getTasks().getByName((String) i);
                }
                if(i instanceof Task)
                {
                    task = (Task) i;
                    sb.append(getDependencies(task, prefix, visited));
                }
            }
        }   
        visited.add(task);
        return sb.toString();
    }
    
    public static String getDependsOn(Task task)
    {
    	StringBuilder sb = new StringBuilder();
    	for(Task i : task.getProject().getTasks())
    	{
    		if(i.getDependsOn().contains(task))
    		{
    			sb.append(task.getName());
    			sb.append("\n");
    		}
    	}
    	String ret = sb.toString();
    	return ret.substring(0, ret.length() - 1);
    }
}
