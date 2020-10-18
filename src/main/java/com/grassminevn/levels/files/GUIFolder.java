package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
            try {
                admin.createNewFile();
                if (!plugin.versionID()) {
                    plugin.copy("gui/admin.yml", admin);
                } else {
                    plugin.copy("old/gui/admin.yml", admin);
                }
            } catch (final IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        profiles = new File(folder, "profiles.yml");
        if (!profiles.exists()) {
            try {
                profiles.createNewFile();
                if (!plugin.versionID()) {
                    plugin.copy("gui/profiles.yml", profiles);
                } else {
                    plugin.copy("old/gui/profiles.yml", profiles);
                }
            } catch (final IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        load();
    }

    public void load() {
        plugin.guiFiles.put("admin", YamlConfiguration.loadConfiguration(admin));
        plugin.guiFiles.put("profiles", YamlConfiguration.loadConfiguration(profiles));
    }
}