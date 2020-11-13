package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public FileConfiguration get;
    private final File file;

    public Config(final Levels plugin) {
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.copy("config.yml", file);
                plugin.getLogger().info("config.yml ( A change was made )");
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        } else {
            plugin.getLogger().info("config.yml ( Loaded )");
        }
        load();
    }

    public void load() {
        get = YamlConfiguration.loadConfiguration(file);
    }
}
