package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class BoostersManager {

    private final Levels plugin;

    private String globalUUID;

    private Double globalBoost;

    private int globalSeconds;

    private int globalSeconds_time;

    private OfflinePlayer offlinePlayer;

    public BoostersManager(final Levels plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (!hasGlobalActive()) {
                    if (globalQueueSize() > 0) {
                        final String[] globalSelect = globalQueue().get(0).split(" ");
                        globalUUID = globalSelect[0];
                        globalBoost = Double.valueOf(globalSelect[1]);
                        globalSeconds = Integer.parseInt(globalSelect[2]);
                        offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(globalUUID));
                        globalStarted();
                        globalSeconds_time = globalSeconds;
                    }
                    return;
                }
                if (globalSeconds > 0) {
                    globalSeconds--;
                    return;
                }
                plugin.boosters.get.set("global-queue", globalRemove());
                plugin.boosters.save();
                globalEnded();
                globalUUID = null;
                globalBoost = null;
                globalSeconds = 0;
                globalSeconds_time = 0;
                offlinePlayer = null;
        }, 20L, 20L);
    }

    public boolean hasGlobalActive() {
        return globalUUID != null || globalBoost != null || globalSeconds != 0;
    }

    public OfflinePlayer getGlobalOfflinePlayer() {
        return offlinePlayer;
    }

    public Double getGlobalBooster() {
        return globalBoost;
    }

    public int getGlobalSeconds() {
        return globalSeconds;
    }

    public List<String> globalQueue() {
        return plugin.boosters.get.getStringList("global-queue");
    }

    private int globalQueueSize() {
        return globalQueue().size();
    }

    private List<String> globalRemove() {
        final List<String> queue = globalQueue();
        queue.remove(0);
        return queue;
    }

    public Double type() {
        return globalBoost;
    }

    public int isInQueueSize(final String uuid) {
        int value = 0;
        for (final String list : globalQueue()) {
            if (list.split(" ")[0].equalsIgnoreCase(uuid)) {
                value++;
            }
        }
        return value;
    }

    public int queueNumber(final String uuid) {
        final int value = 1;
        final List<Integer> number = new ArrayList<>();
        final List<String> list = globalQueue();
        for (int i = 0; i < globalQueueSize(); i++) {
            if (list.get(i).split(" ")[0].equalsIgnoreCase(uuid)) {
                number.add(value + i);
            }
        }
        return number.get(number.size() - 1);
    }

    private void globalStarted() {
        for (final String command : plugin.boosters.get.getStringList("global-settings.start")) {
            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", offlinePlayer.getName()).replace("{levels_booster_global_type}", String.valueOf(globalBoost)).replace("{levels_booster_global_time}", timeLeft(globalSeconds)));
        }
    }

    private void globalEnded() {
        for (final String command : plugin.boosters.get.getStringList("global-settings.stop.ended")) {
            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", offlinePlayer.getName()).replace("{levels_booster_global_type}", String.valueOf(globalBoost)));
        }
        if (globalQueueSize() == 0) {
            for (final String command : plugin.boosters.get.getStringList("global-settings.stop.queue")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", offlinePlayer.getName()).replace("{levels_booster_global_type}", String.valueOf(globalBoost)));
            }
        }
    }

    public String timeLeft(final int seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public String globalPrefix(final Long boost, final String entityType) {
        if (hasGlobalActive() && !plugin.boosters.get.getStringList("global-settings.disabled-xp").contains(entityType)) {
            return ChatColor.translateAlternateColorCodes('&', plugin.boosters.get.getString("global-settings.prefix").replace("{levels_booster_global_xp}", String.valueOf(boost)));
        } else {
            return "";
        }
    }

    public String personalPrefix(final String uuid, final Long boost, final String entityType) {
        if (plugin.get(uuid).getPersonalBooster() != null && !plugin.boosters.get.getStringList("personal-settings.disabled-xp").contains(entityType)) {
            return ChatColor.translateAlternateColorCodes('&', plugin.boosters.get.getString("personal-settings.prefix").replace("{levels_booster_personal_xp}", String.valueOf(boost)));
        } else {
            return "";
        }
    }

    public String getGlobalNamePlaceholder() {
        if (offlinePlayer != null) {
            return String.valueOf(offlinePlayer.getName());
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.name"));
    }

    public String getGlobalPlaceholder() {
        if (globalBoost != null) {
            return String.valueOf(globalBoost);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.none"));
    }

    public String getGlobalTimePlaceholder() {
        if (globalSeconds_time > 0) {
            return String.valueOf(globalSeconds_time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.time"));
    }

    public String getGlobalTimeLeftPlaceholder() {
        if (globalSeconds > 0) {
            return String.valueOf(globalSeconds);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.time-left"));
    }

    public String getGlobalTimePrefixPlaceholder() {
        if (globalSeconds_time > 0) {
            return timeLeft(globalSeconds_time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.time-prefix"));
    }

    public String getGlobalTimeLeftPrefixPlaceholder() {
        if (globalSeconds > 0) {
            return timeLeft(globalSeconds);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.global-booster.time-left-prefix"));
    }

    public String getPersonalPlaceholder(final String uuid) {
        final Double booster = plugin.get(uuid).getPersonalBooster();
        if (booster != null) {
            return String.valueOf(booster);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.personal-booster.none"));
    }

    public String getPersonalTimePlaceholder(final String uuid) {
        final int time = plugin.get(uuid).getPersonalBoosterTime();
        if (time > 0) {
            return String.valueOf(time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.personal-booster.time"));
    }

    public String getPersonalTimeLeftPlaceholder(final String uuid) {
        final int time = plugin.get(uuid).getPersonalBoosterTime_left();
        if (time > 0) {
            return String.valueOf(time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.personal-booster.time-left"));
    }

    public String getPersonalTimePrefixPlaceholder(final String uuid) {
        final int time = plugin.get(uuid).getPersonalBoosterTime();
        if (time > 0) {
            return timeLeft(time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.personal-booster.time-prefix"));
    }

    public String getPersonalTimeLeftPrefixPlaceholder(final String uuid) {
        final int time = plugin.get(uuid).getPersonalBoosterTime_left();
        if (time > 0) {
            return timeLeft(time);
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("placeholders.personal-booster.time-left-prefix"));
    }
}