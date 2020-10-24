package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;

public abstract class Manager {
    protected final Levels plugin;

    public Manager(final Levels plugin) {
        this.plugin = plugin;
    }
}
