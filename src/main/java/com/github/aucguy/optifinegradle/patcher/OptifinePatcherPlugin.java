package com.github.aucguy.optifinegradle.patcher;

import com.github.aucguy.optifinegradle.OptifinePlugin;

import net.minecraftforge.gradle.patcher.PatcherPlugin;

public class OptifinePatcherPlugin extends PatcherPlugin
{
    OptifinePlugin delegate;
    
    public OptifinePatcherPlugin()
    {
        super();
        delegate = new OptifinePlugin(this);
        delegate.init();
    }
    
    @Override
    public void applyPlugin()
    {
        super.applyPlugin();
        delegate.applyPlugin();
    }
}
