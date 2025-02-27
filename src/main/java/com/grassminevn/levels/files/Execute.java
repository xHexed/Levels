package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Execute {

    public FileConfiguration get;
    private final File file;

    public Execute(final Levels plugin) {
        file = new File(plugin.getDataFolder(), "execute.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.copy("execute.yml", file);
                plugin.getLogger().info("execute.yml ( A change was made )");
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        } else {
            plugin.getLogger().info("execute.yml ( Loaded )");
        }
        load();
    }

    public void load() {
        get = YamlConfiguration.loadConfiguration(file);
    }
}
