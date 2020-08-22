package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class XPManager {
    private final Levels plugin;

    public XPManager(final Levels plugin) {
        this.plugin = plugin;
    }

    public void check(final PlayerConnect playerConnect, final String entityType, final Entity entity, final Player killer, final boolean xpUP) {
        if (plugin.config.get.contains("xp." + entityType)) {
            final String group = plugin.systemManager.getGroup(killer, plugin.config.get, "xp." + entityType, true);
            if (group != null) {
                if (xpUP) {
                    if (plugin.systemManager.hasItem(killer, "xp." + entityType + "." + group)) {
                        if (plugin.zones.get.contains("zones." + entityType) && plugin.zones.get.getConfigurationSection("zones") != null && !plugin.zones.get.getConfigurationSection("zones").getKeys(false).isEmpty()) {
                            boolean isInLocation = false;
                            for (final String zone : plugin.zones.get.getConfigurationSection("zones." + entityType).getKeys(false)) {
                                final String[] loc1 = plugin.zones.get.getString("zones." + entityType + "." + zone + ".start").split(" ");
                                final String[] loc2 = plugin.zones.get.getString("zones." + entityType + "." + zone + ".end").split(" ");
                                final World world = plugin.getServer().getWorld(plugin.zones.get.getString("zones." + entityType + "." + zone + ".world"));
                                if (plugin.systemManager.isInLocation(killer.getLocation(), new Location(world, Integer.parseInt(loc1[0]), Integer.parseInt(loc1[1]), Integer.parseInt(loc1[2])), new Location(world, Integer.parseInt(loc2[0]), Integer.parseInt(loc2[1]), Integer.parseInt(loc2[2])))) {
                                    isInLocation = true;
                                }
                            }
                            if (!isInLocation) {
                                return;
                            }
                        }
                        String customName = "";
                        if (plugin.config.get.contains("xp." + entityType + "." + group + ".customName")) {
                            if (entity != null) {
                                if (entity.getCustomName() == null) {
                                    customName = entity.getName();
                                } else {
                                    customName = entity.getCustomName();
                                }
                            }
                            if (!plugin.PlaceholderReplace(killer, ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("xp." + entityType + "." + group + ".customName"))).equalsIgnoreCase(customName)) {
                                return;
                            }
                        }
                        getXP(playerConnect, killer, entityType, customName, group);
                    }
                } else {
                    loseXP(playerConnect, killer, entityType, group);
                }
            }
        }
    }

    private void getXP(final PlayerConnect playerConnect, final Player killer, final String entityType, final CharSequence customName, final String group) {
        final int add = plugin.random(plugin.config.get.getInt("xp." + entityType + "." + group + ".min"), plugin.config.get.getInt("xp." + entityType + "." + group + ".max"));
        long xp = playerConnect.xp() + add;
        long globalBooster = 0L;
        long personalBooster = 0L;
        if (plugin.boostersManager.hasGlobalActive() && !plugin.boosters.get.getStringList("global-settings.disabled-xp").contains(entityType)) {
            final long boosted = Math.round(add * plugin.boostersManager.type());
            globalBooster = boosted - add;
            xp = (xp - add) + boosted;
            if (plugin.boosters.get.contains("global-settings.commands")) {
                plugin.systemManager.executeCommands(killer, plugin.boosters.get, "global-settings.commands", "commands", 0L);
            }
        }
        if (playerConnect.getPersonalBooster() != null && !plugin.boosters.get.getStringList("personal-settings.disabled-xp").contains(entityType)) {
            final long boosted = Math.round(add * playerConnect.getPersonalBooster());
            personalBooster = boosted - add;
            xp = (xp - add) + boosted;
            if (plugin.boosters.get.contains("personal-settings.commands")) {
                plugin.systemManager.executeCommands(killer, plugin.boosters.get, "personal-settings.commands", "commands", 0L);
            }
        }
        playerConnect.xp(xp);
        if (!getLevel(playerConnect, killer)) {
            final String entityName = plugin.config.get.getString("xp." + entityType + "." + group + ".name").replace("{levels_player}", customName);
            final Long need = plugin.levels.get.getLong("levels." + (playerConnect.level() + 1) + ".xp") - xp;
            if (plugin.config.get.contains("xp." + entityType + "." + group + ".level-commands." + playerConnect.level())) {
                sendCommands(killer, "xp." + entityType + "." + group + ".level-commands." + playerConnect.level(), plugin.config.get, entityName, add, need, 0, globalBooster, personalBooster, entityType);
                return;
            }
            sendCommands(killer, "xp." + entityType + "." + group + ".commands", plugin.config.get, entityName, add, need, 0, globalBooster, personalBooster, entityType);
        }
    }

    private void loseXP(final PlayerConnect playerConnect, final Player killer, final String entityType, final String group) {
        if (plugin.config.get.contains("xp." + entityType + "." + group + ".xp-lose")) {
            boolean xpMessage = true;
            final int lost = plugin.random(plugin.config.get.getInt("xp." + entityType + "." + group + ".xp-lose.min"), plugin.config.get.getInt("xp." + entityType + "." + group + ".xp-lose.max"));
            final long xp = playerConnect.xp() - lost;
            if (xp > 0L) {
                playerConnect.xp(xp);
                if (!plugin.config.get.getBoolean("levelup.xp-clear") && playerConnect.xp() < plugin.levels.get.getLong("levels." + playerConnect.level() + ".xp")) {
                    xpMessage = loseLevel(playerConnect, playerConnect.level() - 1, killer, "xp." + entityType + "." + group + ".xp-lose.commands.level");
                }
            } else {
                final long level = playerConnect.level() - 1;
                final long lostXP = plugin.levels.get.getLong("levels." + playerConnect.level() + ".xp") - lost;
                xpMessage = loseLevel(playerConnect, level, killer, "xp." + entityType + "." + group + ".xp-lose.commands.level");
                if (lostXP >= 0) {
                    playerConnect.xp(lostXP);
                } else {
                    playerConnect.xp(0L);
                }
            }
            if (xp >= 0L && xpMessage) {
                sendCommands(killer, "xp." + entityType + "." + group + ".xp-lose.commands.lose", plugin.config.get, "", 0, 0L, lost, 0L, 0L, "");
            }
        }
    }

    public boolean loseLevel(final PlayerConnect playerConnect, final Long level, final Player player, final String commandPath) {
        if (level >= 0) {
            playerConnect.level(level);
            sendCommands(player, commandPath, plugin.config.get, "", 0, 0L, 0, 0L, 0L, "");
            if (plugin.levels.get.contains("levels." + level + ".lose-commands")) {
                sendCommands(player, "levels." + level + ".lose-commands", plugin.levels.get, "", 0, 0L, 0, 0L, 0L, "");
            }
            return false;
        }
        return true;
    }

    public boolean getLevel(final PlayerConnect playerConnect, final Player player) {
        final Long nextLevel = playerConnect.level() + 1;
        if (playerConnect.xp() >= plugin.levels.get.getLong("levels." + nextLevel + ".xp")) {
            if (plugin.config.get.getBoolean("levelup.xp-clear")) {
                playerConnect.xp(0L);
            }
            sendCommands(player, "levels." + nextLevel + ".commands", plugin.levels.get, "", 0, 0L, 0, 0L, 0L, "");
            playerConnect.level(nextLevel);
            return true;
        }
        return false;
    }

    public boolean isMaxLevel(final Player player, final PlayerConnect playerConnect) {
        final String group = plugin.systemManager.getGroup(player, plugin.config.get, "level-max", false);
        if (group != null) {
            return playerConnect.level() >= plugin.config.get.getLong("level-max." + group + ".max");
        }
        return false;
    }

    private void sendCommands(final Player killer, final String path, final ConfigurationSection fileConfiguration, final CharSequence customName, final int add, final Long need, final int lost, final Long globalBooster, final Long personalBooster, final String entityType) {
        if (path != null) {
            for (final String command : fileConfiguration.getStringList(path)) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, plugin.PlaceholderReplace(killer, command.replace("{levels_type}", customName).replace("{levels_xp_get}", String.valueOf(add)).replace("{levels_xp_needed}", String.valueOf(need)).replace("{levels_xp_lost}", String.valueOf(lost))).replace("{levels_booster_global_prefix}", plugin.boostersManager.globalPrefix(globalBooster, entityType)).replace("{levels_booster_personal_prefix}", plugin.boostersManager.personalPrefix(killer.getUniqueId().toString(), personalBooster, entityType)));
            }
        }
    }
}