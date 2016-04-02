package com.github.aucguy.optifinegradle.user;

import com.github.aucguy.optifinegradle.OptifinePlugin;

import net.minecraftforge.gradle.user.patcherUser.forge.ForgePlugin;

public class OptifineUserPlugin extends ForgePlugin
{
    OptifinePlugin delegate;
    
    public OptifineUserPlugin()
    {
        super();
        delegate = new OptifinePlugin(this);
        delegate.init();
    }
    
    @Override
    public void applyUserPlugin()
    {
        super.applyUserPlugin();
        delegate.applyPlugin();
    }
}
