package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Boosters {

    public FileConfiguration get;
    private final File file;

    private final Levels plugin;

    public Boosters(final Levels plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "boosters.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.copy("boosters.yml", file);
                plugin.textUtils.info("boosters.yml (A change was made)");
            } catch (final IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        } else {
            plugin.textUtils.info("boosters.yml ( Loaded )");
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
            plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
        }
    }
}