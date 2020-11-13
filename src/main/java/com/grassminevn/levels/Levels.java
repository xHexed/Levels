package com.grassminevn.levels;

import com.grassminevn.levels.commands.LevelsCommand;
import com.grassminevn.levels.commands.LevelsTabComplete;
import com.grassminevn.levels.data.Database;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.data.Purge;
import com.grassminevn.levels.files.Config;
import com.grassminevn.levels.files.Execute;
import com.grassminevn.levels.files.GUIFolder;
import com.grassminevn.levels.files.Language;
import com.grassminevn.levels.gui.Menu;
import com.grassminevn.levels.listeners.InventoryClick;
import com.grassminevn.levels.listeners.PlayerJoin;
import com.grassminevn.levels.listeners.PlayerLogin;
import com.grassminevn.levels.listeners.PlayerQuit;
import com.grassminevn.levels.managers.*;
import com.grassminevn.levels.placeholders.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Levels extends JavaPlugin {
    public static Levels call;

    public final ConsoleCommandSender consoleSender = Bukkit.getServer().getConsoleSender();
    public Database database;
    public Config config;
    public Language language;
    public com.grassminevn.levels.files.Levels levels;
    public Execute execute;
    public GUIFolder guiFolder;
    public GUIManager guiManager;
    public StatsManager statsManager;
    public XPManager xpManager;
    public MultiplierManager multiplierManager;
    public AsyncExecutorManager asyncExecutorManager;

    private final Map<UUID, PlayerConnect> playerConnect = new HashMap<>();
    private final HashMap<Player, Menu> playerMenu = new HashMap<>();
    public final HashMap<String, FileConfiguration> guiFiles = new HashMap<>();

    public void onEnable() {
        call = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        config = new Config(this);
        language = new Language(this);
        levels = new com.grassminevn.levels.files.Levels(this);
        execute = new Execute(this);

        guiFolder = new GUIFolder(this);

        asyncExecutorManager = new AsyncExecutorManager(this);
        guiManager = new GUIManager(this);
        statsManager = new StatsManager(this);
        xpManager = new XPManager(this);
        multiplierManager = new MultiplierManager(this);

        database = new Database(this);
        if (database.set()) {
            getServer().getPluginManager().registerEvents(new PlayerLogin(this), this);
            getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
            getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
            getServer().getPluginManager().registerEvents(new InventoryClick(), this);
            getCommand("levels").setExecutor(new LevelsCommand(this));
            getCommand("levels").setTabCompleter(new LevelsTabComplete(this));
            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PlaceholderAPI(this).register();
                getLogger().info("PlaceholderAPI (found)");
            }
            if (config.get.contains("mysql.purge")) {
                new Purge(this);
            }
            if (isLevelsValid()) {
                getLogger().info("Validator ( passed )");
            }
        } else {
            getLogger().severe("Disabling plugin cannot connect to database");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
        try {
            if (database != null) {
                database.close();
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        call = null;
    }

    public void unloadPlayerConnect(final UUID uuid) {
        final PlayerConnect data = playerConnect.remove(uuid);
        if (data != null) {
            data.save();
        }
    }

    public PlayerConnect getPlayerConnect(final UUID uuid) {
        if (playerConnect.containsKey(uuid)) {
            return playerConnect.get(uuid);
        }
        final PlayerConnect playerConnect = new PlayerConnect(uuid);
        this.playerConnect.put(uuid, playerConnect);
        return playerConnect;
    }

    public Set<UUID> listPlayerConnect() {
        return playerConnect.keySet();
    }

    public Menu getPlayerMenu(final Player player) {
        final Menu playerMenu;
        if (!this.playerMenu.containsKey(player)) {
            playerMenu = new Menu(player);
            this.playerMenu.put(player, playerMenu);
            return playerMenu;
        } else {
            return this.playerMenu.get(player);
        }
    }

    public String replacePlaceholders(final OfflinePlayer player, String message) {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
        }
        final UUID uuid = player.getUniqueId();
        final PlayerConnect playerConnect = getPlayerConnect(uuid);
            message = internalReplace(playerConnect, message)
                    .replace("{level_group}", statsManager.group(playerConnect))
                    .replace("{level_prefix}", statsManager.prefix(playerConnect))
                    .replace("{level_suffix}", statsManager.suffix(playerConnect))
            ;
        message = message
                .replace("{player}", player.getName())
                .replace("{uuid}", uuid.toString());
        return message;
    }

    public String internalReplace(final PlayerConnect playerConnect, final String message) {
        return message
                .replace("{xp}", String.valueOf(playerConnect.getXP()))
                .replace("{level}", String.valueOf(playerConnect.getLevel()))
                .replace("{level_next}", String.valueOf(playerConnect.getLevel() + 1))
                .replace("{xp_required}", String.valueOf(statsManager.xp_required(playerConnect, false)))
                .replace("{xp_need}", String.valueOf(statsManager.xp_need(playerConnect)))
                .replace("{xp_progress}", String.valueOf(statsManager.xp_progress(playerConnect)))
                .replace("{xp_progress_style}", String.valueOf(statsManager.xp_progress_style(playerConnect, "xp-progress-style")))
                .replace("{xp_progress_style_2}", String.valueOf(statsManager.xp_progress_style(playerConnect, "xp-progress-style-2")))
                .replace("{date}", statsManager.time("date", playerConnect.getTime().getTime()))
                .replace("{time}", statsManager.time("time", playerConnect.getTime().getTime()))
                .replace("{group}", playerConnect.getGroup())
                ;
    }

    public boolean isInt(final String s) {
        try {
            Integer.parseInt(s);
        } catch(final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isLong(final String s) {
        try {
            Long.parseLong(s);
        } catch(final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isDouble(final String s) {
        try {
            Double.parseDouble(s);
        } catch(final NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isString(final String text) {
        return text.matches("^[a-zA-Z]*$");
    }

    @SuppressWarnings("deprecation")
    public ItemStack getID(final String bb, final int amount) {
        try {
            final String[] parts = bb.split(":");
            final int matId = Integer.parseInt(parts[0]);
            if (parts.length == 2) {
                final short data = Short.parseShort(parts[1]);
                return new ItemStack(Material.getMaterial(matId), amount, data);
            }
            return new ItemStack(Material.getMaterial(matId));
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    public int random(final int min, final int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void copy(final String filename, final File file) {
        try {
            Files.copy(getResource(filename), file.toPath());
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean isLevelsValid() {
        boolean valid = true;
        for (final String group : levels.get.getConfigurationSection("").getKeys(false)) {
            if (!levels.get.contains(group + "." + config.get.getLong("start-level"))) {
                getLogger().severe("Validator ( not passed ) (group ( " + group + " ) (level ( " + config.get.getLong("start-level") + " ) is not found )");
                valid = false;
            }
            if (levels.get.getLong(group + "." + config.get.getLong("start-level") + ".xp") != 0) {
                getLogger().severe("Validator ( not passed ) (group ( " + group + " ) (level ( " + config.get.getLong("start-level") + " ) xp is not 0 )");
                valid = false;
            }
            if (!levels.get.contains(group + ".execute")) {
                getLogger().severe("Validator ( not passed ) (group ( " + group + " ) is missing execute )");
                valid = false;
            }
            for (final String level : levels.get.getConfigurationSection(group).getKeys(false)) {
                if (!level.equalsIgnoreCase("execute")) {
                    if (!isLong(level)) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) not a number )");
                        valid = false;
                    }
                    if (!levels.get.contains(group + "." + level + ".prefix")) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) is missing prefix )");
                        valid = false;
                    }
                    if (!levels.get.contains(group + "." + level + ".suffix")) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) is missing suffix )");
                        valid = false;
                    }
                    if (!levels.get.contains(group + "." + level + ".group")) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) is missing group )");
                        valid = false;
                    }
                    if (!levels.get.contains(group + "." + level + ".execute")) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) is missing execute )");
                        valid = false;
                    }
                    if (!execute.get.contains(levels.get.getString(group + "." + level + ".execute"))) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (execute group ( " + levels.get.getString(group + "." + level + ".execute") + " ) is not found in execute.yml)");
                        valid = false;
                    }
                    if (!levels.get.contains(group + "." + level + ".xp")) {
                        getLogger().severe("Validator ( not passed ) ( " + group + " ) (level ( " + level + " ) is missing xp )");
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }
}