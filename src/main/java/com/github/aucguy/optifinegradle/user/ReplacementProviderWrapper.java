package com.github.aucguy.optifinegradle.user;

import java.lang.reflect.Field;

import com.github.aucguy.optifinegradle.ReflectHelper;

import net.minecraftforge.gradle.util.delayed.ReplacementProvider;

public class ReplacementProviderWrapper
{
    protected static final Field replaceMapField = ReflectHelper.getField(ReplacementProvider.class, "replaceMap");

    public static Object getReplaceMap(ReplacementProvider self)
    {
        return ReflectHelper.accessField(replaceMapField, self);
    }
}
