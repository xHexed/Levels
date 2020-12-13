package com.grassminevn.levels.managers;

import com.google.common.base.Strings;
import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.text.SimpleDateFormat;
import java.util.*;

public class StatsManager extends Manager {
    public StatsManager(final Levels plugin) {
        super(plugin);
    }

    public Long xp_required(final PlayerConnect playerConnect, final boolean next) {
        final Long level;
        if (!next) {
            level = playerConnect.getLevel();
        } else {
            level = playerConnect.getLevel() + 1;
        }
        final String group = playerConnect.getGroup();
        if (plugin.levels.get.getConfigurationSection(group).getKeys(false).size() > level) {
            return plugin.levels.get.getLong(group + "." + (level + 1) + ".xp");
        }
        return 0L;
    }

    public Long xp_need(final PlayerConnect playerConnect) {
        return plugin.levels.get.getLong(playerConnect.getGroup() + "." + (playerConnect.getLevel() + 1) + ".xp") - playerConnect.getXP();
    }

    public int xp_progress(final PlayerConnect playerConnect) {
        final long xp_cur = playerConnect.getXP();
        final long xp_req_cur = plugin.levels.get.getLong(playerConnect.getGroup() + "." + playerConnect.getLevel() + ".xp");
        final long xp_req_next = plugin.levels.get.getLong(playerConnect.getGroup() + "." + (playerConnect.getLevel() + 1) + ".xp");
        final double set = (double) (xp_cur - xp_req_cur) / (xp_req_next - xp_req_cur);
        if (xp_cur > xp_req_next) {
            return 100;
        }
        return (int) Math.round(set * 100);
    }

    public String xp_progress_style(final PlayerConnect playerConnect, final String path) {
        final char xp = (char) Integer.parseInt(plugin.config.get.getString(path + ".xp.symbol").substring(2), 16);
        final char none = (char) Integer.parseInt(plugin.config.get.getString(path + ".none.symbol").substring(2), 16);
        final ChatColor xpColor = ChatColor.getByChar(plugin.config.get.getString(path + ".xp.color").charAt(1));
        final ChatColor noneColor = ChatColor.getByChar(plugin.config.get.getString(path + ".none.color").charAt(1));
        final int bars = plugin.config.get.getInt(path + ".amount");
        final int progressBars = (bars * xp_progress(playerConnect) / 100);
        try {
            return Strings.repeat("" + xpColor + xp, progressBars) + Strings.repeat("" + noneColor + none, bars - progressBars);
        } catch (final RuntimeException exception) {
            return "";
        }
    }

    public String prefix(final PlayerConnect playerConnect) {
        long level = playerConnect.getLevel();
        if (!plugin.levels.get.contains(playerConnect.getGroup() + "." + level + ".group")) {
            level = plugin.config.get.getLong("start-level");
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.internalReplace(playerConnect, plugin.levels.get.getString(playerConnect.getGroup() + "." + level + ".prefix")));
    }

    public String suffix(final PlayerConnect playerConnect) {
        long level = playerConnect.getLevel();
        if (!plugin.levels.get.contains(playerConnect.getGroup() + "." + level + ".group")) {
            level = plugin.config.get.getLong("start-level");
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.internalReplace(playerConnect, plugin.levels.get.getString(playerConnect.getGroup() + "." + level + ".suffix")));
    }

    public String group(final PlayerConnect playerConnect) {
        long level = playerConnect.getLevel();
        if (!plugin.levels.get.contains(playerConnect.getGroup() + "." + level + ".group")) {
            level = plugin.config.get.getLong("start-level");
        }
        return ChatColor.translateAlternateColorCodes('&', plugin.internalReplace(playerConnect, plugin.levels.get.getString(playerConnect.getGroup() + "." + level + ".group")));
    }

    public String getTopKey(final String type, final int number, final boolean reverse) {
        final LinkedHashMap<OfflinePlayer, Long> linkedHashMap = getTopMap(type, reverse);
        final ArrayList<OfflinePlayer> map = new ArrayList<>(linkedHashMap.keySet());
        if (map.size() > number) {
            return map.get(number).getName();
        } else {
            return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("top.name"));
        }
    }

    public String getTopValue(final String type, final int number, final boolean reverse) {
        final LinkedHashMap<OfflinePlayer, Long> linkedHashMap = getTopMap(type, reverse);
        final ArrayList<Long> map = new ArrayList<>(linkedHashMap.values());
        if (map.size() > number) {
            return String.valueOf(map.get(number));
        } else {
            return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("top.value"));
        }
    }

    public LinkedHashMap<OfflinePlayer, Long> getTopMap(final String type, final boolean reverse) {
        final Map<OfflinePlayer, Long> unsorted = new HashMap<>();
        for (final OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
            final UUID uuid = offlinePlayer.getUniqueId();
            final PlayerConnect playerConnect = plugin.getPlayerConnect(uuid);
            switch (type) {
                case "xp":
                    unsorted.put(offlinePlayer, playerConnect.getXP());
                    break;
                case "level":
                    unsorted.put(offlinePlayer, playerConnect.getLevel());
                    break;
                case "rating":
                    unsorted.put(offlinePlayer, (long) playerConnect.getRating().getMean());
                    break;
                case "lastseen":
                    unsorted.put(offlinePlayer, playerConnect.getTime().getTime());
                    break;
            }
        }
        final LinkedHashMap<OfflinePlayer, Long> sorted = new LinkedHashMap<>();
        if (reverse) {
            unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        } else {
            unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder())).forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        }
        return sorted;
    }

    public String time(final String path, final Date time) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(plugin.config.get.getString(path + ".format"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(plugin.config.get.getString(path + ".zone")));
        return simpleDateFormat.format(time);
    }
}