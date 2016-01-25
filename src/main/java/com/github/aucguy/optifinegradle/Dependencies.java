package com.github.aucguy.optifinegradle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.Task;

public class Dependencies {
	public static Set<Task> getDependencies(Set<Task> initial, Project project) {
		Map<Object, Set<Task>> reverseDep = new HashMap<Object, Set<Task>>();
		Map<Task, Set<Task>> links = new HashMap<Task, Set<Task>>();
		Queue<Task> queue = new LinkedList<Task>(initial);
		Set<Task> tasks = new HashSet<Task>(initial);
		
		for(Task i : project.getTasks()) {
			for(Object k : i.getDependsOn()) {
				if(!reverseDep.containsKey(k)) reverseDep.put(k, new HashSet<Task>());
				reverseDep.get(k).add(i);
			}
		}
		
		for(Entry<Object, Set<Task>> i : reverseDep.entrySet()) {
			if(i.getKey() instanceof Task) {
				links.put((Task) i.getKey(), i.getValue());
			} else if(i.getKey() instanceof String) {
				for(Task k : project.getTasksByName((String) i.getKey(), true)) {
					links.put(k, i.getValue());
				}
			}
		}
		
		while(!queue.isEmpty()) {
			Task task = queue.remove();
			if(links.get(task) != null) { 
				for(Task i : links.get(task)) {
					if(!tasks.contains(i)) {
						tasks.add(i);
						queue.add(i);
					}
				}
			}
		}
		return tasks;
	}
}
