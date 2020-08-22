package com.grassminevn.levels.files;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.gui.GUI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class GUIFolder {
    private final Levels plugin;
    private final Collection<File> files = new ArrayList<>();

    public GUIFolder(final Levels plugin) {
        this.plugin = plugin;
        final File folder = new File(plugin.getDataFolder() + File.separator + "gui");
        if (!folder.exists()) {
            folder.mkdir();
            final File boosters = new File(folder, "boosters.yml");
            final File globalboosters = new File(folder, "globalBoosters.yml");
            final File personalboosters = new File(folder, "personalBoosters.yml");
            final File profile = new File(folder, "profile.yml");
            final File profileall = new File(folder, "profileAll.yml");
            final File admin = new File(folder, "admin.yml");
            final File shop = new File(folder, "shop.yml");
            final File shop5 = new File(folder, "shop5.yml");
            final File shop10 = new File(folder, "shop10.yml");
            try {
                boosters.createNewFile();
                globalboosters.createNewFile();
                personalboosters.createNewFile();
                profile.createNewFile();
                profileall.createNewFile();
                admin.createNewFile();
                shop.createNewFile();
                shop5.createNewFile();
                shop10.createNewFile();
                plugin.copy("gui/boosters.yml", boosters);
                plugin.copy("gui/globalBoosters.yml", globalboosters);
                plugin.copy("gui/personalBoosters.yml", personalboosters);
                plugin.copy("gui/profile.yml", profile);
                plugin.copy("gui/profileAll.yml", profileall);
                plugin.copy("gui/admin.yml", admin);
                plugin.copy("gui/shop.yml", shop);
                plugin.copy("gui/shop5.yml", shop5);
                plugin.copy("gui/shop10.yml", shop10);
            } catch (final IOException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                files.add(file);
                plugin.guiList.put(fileName, new GUI(plugin, YamlConfiguration.loadConfiguration(file)));
                plugin.textUtils.info("[GUI] " + fileName + " ( Loaded )");
            } else {
                plugin.textUtils.error("[GUI] " + fileName + " ( Error Loading )");
            }
        }
    }

    public void load() {
        for (final File file : files) {
            plugin.guiList.put(file.getName(), new GUI(plugin, YamlConfiguration.loadConfiguration(file)));
        }
    }
}