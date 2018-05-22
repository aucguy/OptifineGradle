package com.github.aucguy.optifinegradle.patcher;

import org.gradle.api.NamedDomainObjectContainer;

import com.github.aucguy.optifinegradle.OptifineExtension;

import groovy.lang.Closure;

public class OptifinePatcherExtension extends OptifineExtension
{
    NamedDomainObjectContainer<OptifinePatcherProject> container;
    
    public void projects(Closure<?> closure)
    {
        container.configure(closure);
    }
}
