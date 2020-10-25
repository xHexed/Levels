package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GUIFolder {

    private final Levels plugin;

    final File admin;
    final File profiles;

    public GUIFolder(final Levels plugin) {
        this.plugin = plugin;
        final File folder = new File(plugin.getDataFolder() + File.separator + "gui");
        if (!folder.exists()) {
            folder.mkdir();
        }
        admin = new File(folder, "admin.yml");
        if (!admin.exists()) {
            plugin.copy("gui/admin.yml", admin);
        }
        profiles = new File(folder, "profiles.yml");
        if (!profiles.exists()) {
            plugin.copy("gui/profiles.yml", profiles);
        }
        load();
    }

    public void load() {
        plugin.guiFiles.put("admin", YamlConfiguration.loadConfiguration(admin));
        plugin.guiFiles.put("profiles", YamlConfiguration.loadConfiguration(profiles));
    }
}