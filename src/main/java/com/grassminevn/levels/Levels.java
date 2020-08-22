package com.grassminevn.levels;

import com.google.common.io.ByteStreams;
import com.grassminevn.levels.commands.*;
import com.grassminevn.levels.data.Database;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.files.*;
import com.grassminevn.levels.gui.GUI;
import com.grassminevn.levels.listeners.*;
import com.grassminevn.levels.managers.*;
import com.grassminevn.levels.placeholders.PlaceholderAPI;
import com.grassminevn.levels.utils.KillSessionUtils;
import com.grassminevn.levels.utils.TextUtils;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Levels extends JavaPlugin {
    public static Levels call;

    public Config config;
    public Language language;
    public com.grassminevn.levels.files.Levels levels;
    public Boosters boosters;
    public Zones zones;
    public TextUtils textUtils;
    public XPManager xpManager;
    public StatsManager statsManager;
    public SystemManager systemManager;
    public EntityManager entityManager;
    public KillSessionUtils killSessionUtils;
    public BoostersManager boostersManager;
    public PlaceholderManager placeholderManager;
    public GUIFolder guiFolder;
    public Database database;
    public final ConsoleCommandSender consoleCommandSender = Bukkit.getServer().getConsoleSender();
    private final Map<String, PlayerConnect> playerConnect = new HashMap<>();
    public final Collection<String> spawners = new HashSet<>();
    public final HashMap<String, GUI> guiList = new HashMap<>();
    public final HashMap<String, Integer> guiPageID = new HashMap<>();
    public final HashMap<String, String> guiPageSort = new HashMap<>();
    public final HashMap<String, Boolean> guiPageSortReverse = new HashMap<>();
    public final Map<String, String> lastDamagers = new HashMap<>();
    public final HashMap<String, Location> wandPos1 = new HashMap<>();
    public final HashMap<String, Location> wandPos2 = new HashMap<>();
    public ItemStack wand;

    public void onEnable() {
        call = this;
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        textUtils = new TextUtils(this);
        xpManager = new XPManager(this);
        statsManager = new StatsManager(this);
        systemManager = new SystemManager(this);
        entityManager = new EntityManager(this);
        killSessionUtils = new KillSessionUtils(this);
        boostersManager = new BoostersManager(this);
        placeholderManager = new PlaceholderManager(this);
        config = new Config(this);
        language = new Language(this);
        levels = new com.grassminevn.levels.files.Levels(this);
        boosters = new Boosters(this);
        zones = new Zones(this);
        database = new Database(this);
        guiFolder = new GUIFolder(this);
        if (database.set()) {
            textUtils.info("Database connected");
            if (config.get.getBoolean("load-players.reload")) { database.loadOnline(); }
            if (config.get.getBoolean("load-players.all")) { database.loadAll(); }
            getServer().getPluginManager().registerEvents(new EntityDeath(this), this);
            getServer().getPluginManager().registerEvents(new EntityDamageByEntity(this), this);
            getServer().getPluginManager().registerEvents(new InventoryClick(this), this);
            getServer().getPluginManager().registerEvents(new InventoryClose(this), this);
            getServer().getPluginManager().registerEvents(new PlayerLogin(this), this);
            getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
            getCommand("levels").setExecutor(new LevelsCommand(this));
            getCommand("stats").setExecutor(new StatsCommand(this));
            getCommand("top").setExecutor(new TopCommand(this));
            getCommand("boosters").setExecutor(new BoostersCommand(this));
            getCommand("profile").setExecutor(new ProfileCommand(this));
            getCommand("admin").setExecutor(new AdminCommand(this));
            getCommand("shop").setExecutor(new ShopCommand(this));
            if (config.get.getBoolean("placeholders.PlaceholderAPI")) { placeholders(); }
            if (config.get.getBoolean("save.use")) { systemManager.saveSchedule(); }
            wand = getWand();
        } else {
            textUtils.error("Disabling plugin cannot connect to database");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
        try {
            database.close();
        } catch (final SQLException exception) {
            textUtils.exception(exception.getStackTrace(), exception.getMessage());
        }
        call = null;
    }

    public void load(final String uuid) {
        final PlayerConnect data = new PlayerConnect(uuid);
        playerConnect.put(uuid, data);
    }

    public void unload(final String uuid) {
        final PlayerConnect data = playerConnect.remove(uuid);
        if (data != null) {
            data.save();
        }
    }

    public PlayerConnect get(final String uuid) {
        return playerConnect.get(uuid);
    }

    public Set<String> list() {
        return playerConnect.keySet();
    }

    public boolean isInt(final String s) {
        try {
            Integer.parseInt(s);
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

    public int random(final int min, final int max) {
        final Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private void placeholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
            textUtils.info("PlaceholderAPI (found) adding placeholders");
        }
    }

    public String PlaceholderReplace(final Player player, String message) {
        String group = "";
        String group_to = "";
        if (list().contains(player.getUniqueId().toString())) {
            group = statsManager.group(player);
            group_to = statsManager.group_to(player);
        }
        message = replacePlaceholders(message, player.getUniqueId().toString(), player.getName(), group, group_to);
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public String offlinePlaceholderReplace(final OfflinePlayer offlinePlayer, String message) {
        message = replacePlaceholders(message, offlinePlayer.getUniqueId().toString(), offlinePlayer.getName(), "", "");
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(offlinePlayer, message);
        }
        return message;
    }

    private String replacePlaceholders(String message, final String uuid, final CharSequence name, final CharSequence group, final CharSequence group_to) {
        if (list().contains(uuid)) {
            final PlayerConnect playerConnect = get(uuid);
            message = message
                    .replace("{levels_xp}", String.valueOf(playerConnect.xp()))
                    .replace("{levels_xp_required}", String.valueOf(statsManager.xp_required(uuid, false)))
                    .replace("{levels_xp_required_next}", String.valueOf(statsManager.xp_required(uuid, true)))
                    .replace("{levels_xp_progress}", String.valueOf(statsManager.xp_progress(uuid)))
                    .replace("{levels_xp_progress_style}", String.valueOf(statsManager.xp_progress_style(uuid)))
                    .replace("{levels_level}", String.valueOf(playerConnect.level()))
                    .replace("{levels_level_to}", String.valueOf(playerConnect.level() + 1))
                    .replace("{levels_group}", group)
                    .replace("{levels_group_to}", group_to)
                    .replace("{levels_coins}", String.valueOf(playerConnect.coins()));
        }
        message = message
                .replace("{levels_player}", name)
                .replace("{levels_uuid}", uuid);
        return message;
    }

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

    public void copy(final String filename, final File file) {
        try {
            ByteStreams.copy(getResource(filename), new FileOutputStream(file));
        } catch (final IOException exception) {
            textUtils.exception(exception.getStackTrace(), exception.getMessage());
        }
    }

    private ItemStack getWand() {
        final ItemStack itemStack = new ItemStack(Material.STICK);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bLevels Wand"));
        final List<String> list = new ArrayList<>();
        list.add(ChatColor.translateAlternateColorCodes('&', "&6This tool is used to select points"));
        itemMeta.setLore(list);
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}