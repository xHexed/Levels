package com.grassminevn.levels.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Levels {

    public FileConfiguration get;
    private final File file;

    private final com.grassminevn.levels.Levels plugin;

    public Levels(final com.grassminevn.levels.Levels plugin) {
        this.plugin = plugin;
        file = new File(com.grassminevn.levels.Levels.call.getDataFolder(), "levels.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.copy("levels.yml", file);
                plugin.getLogger().info("levels.yml ( A change was made )");
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        } else {
            plugin.getLogger().info("levels.yml ( Loaded )");
        }
        load();
    }

    public void load() {
        get = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            get.save(file);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }
}