package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SystemManager {
    private final Levels plugin;

    public SystemManager(final Levels plugin) {
        this.plugin = plugin;
    }

    public String getGroup(final Entity player, final ConfigurationSection fileConfiguration, final String path, final boolean world) {
        String currentGroup = null;
        for (final String group : fileConfiguration.getConfigurationSection(path).getKeys(false)) {
            if (!group.equalsIgnoreCase("worlds")) {
                if (player.hasPermission(fileConfiguration.getString(path + "." + group + ".permission"))) {
                    if (world) {
                        if (world(player, fileConfiguration, path + "." + group)) {
                            currentGroup = group;
                        }
                    } else {
                        currentGroup = group;
                    }
                }
            }
        }
        return currentGroup;
    }

    public boolean world(final Entity player, final ConfigurationSection fileConfiguration, final String path) {
        if (fileConfiguration.contains(path + ".worlds")) {
            final List<String> worlds = fileConfiguration.getStringList(path + ".worlds");
            return worlds.contains(player.getWorld().getName());
        }
        return true;
    }

    public boolean isInLocation(final Location block, final Location start, final Location end) {
        final Location loc1 = new Location(block.getWorld(), Math.min(start.getBlockX(), end.getBlockX()), Math.min(start.getBlockY(), end.getBlockY()), Math.min(start.getBlockZ(), end.getBlockZ()));
        final Location loc2 = new Location(block.getWorld(), Math.max(start.getBlockX(), end.getBlockX()), Math.max(start.getBlockY(), end.getBlockY()), Math.max(start.getBlockZ(), end.getBlockZ()));
        return block.getBlockX() >= loc1.getBlockX() && block.getBlockX() <= loc2.getBlockX() && block.getBlockY() >= loc1.getBlockY() && block.getBlockY() <= loc2.getBlockY() && block.getBlockZ() >= loc1.getBlockZ() && block.getBlockZ() <= loc2.getBlockZ();
    }

    public void executeCommands(final Player player, final ConfigurationSection fileConfiguration, final String path, final String key, final Long value) {
        final String group = getGroup(player, fileConfiguration, path, true);
        if (group != null) {
            if (value > 0 && !fileConfiguration.contains(path + "." + group + "." + value)) {
                return;
            }
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    String commandPath = path + "." + group + "." + key;
                    if (value > 0) {
                        commandPath = path + "." + group + "." + value + "." + key;
                    }
                    for (final String commands : fileConfiguration.getStringList(commandPath)) {
                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, plugin.PlaceholderReplace(player, commands));
                    }
                }, fileConfiguration.getLong(path + "." + group + ".delay"));
        }
    }

    public void saveSchedule() {
        final int interval = plugin.config.get.getInt("save.interval");
        plugin.textUtils.info("Saving cached data to the database every " + interval + " minutes");
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (final String uuid : plugin.list()) {
                plugin.get(uuid).save();
            }
            if (plugin.config.get.getBoolean("debug.save")) { plugin.textUtils.debug("Saved cached data to database"); }
        }, interval * 60,interval * 60);
    }

    public boolean hasItem(final Player player, final String path) {
        if (!plugin.config.get.contains(path + ".item")) { return true; }
        final PlayerInventory inventory = player.getInventory();
        final ItemStack itemStack = inventory.getItemInMainHand();
        if (itemStack == null) { return false; }
        final String itemStackName = itemStack.getType().name();
        if (plugin.config.get.contains(path + ".item.items")) {
            for (final String next : plugin.config.get.getConfigurationSection(path + ".item.items").getKeys(false)) {
                if (next.equalsIgnoreCase(itemStackName.toLowerCase())) {
                    if (itemStack.hasItemMeta()) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        boolean check = true;
                        final String displayName = plugin.PlaceholderReplace(player, ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString(path + ".item.items." + next + ".name")));
                        if (itemMeta.hasDisplayName()) {
                            if (!itemMeta.getDisplayName().equalsIgnoreCase(displayName)) {
                                check = false;
                            }
                        } else if (!displayName.isEmpty()) {
                            check = false;
                        }
                        final List<String> lores = plugin.config.get.getStringList(path + ".item.items." + next + ".lores");
                        if (itemMeta.hasLore()) {
                            final Collection<String> list = new ArrayList<>();
                            for (final String lore : lores) {
                                list.add(plugin.PlaceholderReplace(player, ChatColor.translateAlternateColorCodes('&', lore)));
                            }
                            if (!itemMeta.getLore().equals(list)) {
                                check = false;
                            }
                        } else if (!lores.isEmpty()) {
                            check = false;
                        }
                        if (check) {
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }
}