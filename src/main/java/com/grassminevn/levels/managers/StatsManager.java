package com.grassminevn.levels.managers;

import com.google.common.base.Strings;
import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class StatsManager {

    private final Levels plugin;

    public StatsManager(final Levels plugin) {
        this.plugin = plugin;
    }

    public Long xp_required(final String uuid, final boolean next) {
        final PlayerConnect playerConnect = plugin.get(uuid);
        final Long level;
        if (!next) {
            level = playerConnect.level();
        } else {
            level = playerConnect.level() + 1;
        }
        if (plugin.levels.get.getConfigurationSection("levels").getKeys(false).size() > level) {
            return plugin.levels.get.getLong("levels." + (level + 1) + ".xp");
        }
        return 0L;
    }

    public String xp_progress(final String uuid) {
        final PlayerConnect playerConnect = plugin.get(uuid);
        Long currentXP = plugin.levels.get.getLong("levels." + playerConnect.level() + ".xp");
        if (plugin.config.get.getBoolean("levelup.xp-clear")) {
            currentXP = 0L;
        }
        try {
        return new DecimalFormat("#").format(Math.round(((Double.valueOf(playerConnect.xp()) - currentXP) / (plugin.levels.get.getLong("levels." + (playerConnect.level() + 1) + ".xp") - currentXP) * 100) * 10.0) / 10.0);
        } catch (final RuntimeException exception) {
            return "";
        }
    }

    public String xp_progress_style(final String uuid) {
        final char xp = (char) Integer.parseInt(plugin.config.get.getString("xp-progress-style.xp.symbol").substring(2), 16);
        final char none = (char) Integer.parseInt(plugin.config.get.getString("xp-progress-style.none.symbol").substring(2), 16);
        final ChatColor xpColor = getChatColor(plugin.config.get.getString("xp-progress-style.xp.color"));
        final ChatColor noneColor = getChatColor(plugin.config.get.getString("xp-progress-style.none.color"));
        final int bars = plugin.config.get.getInt("xp-progress-style.amount");
        final int progressBars = (int) (bars * Double.parseDouble(xp_progress(uuid)) / 100);
        try {
            return Strings.repeat(String.valueOf(xpColor) + xp, progressBars) + Strings.repeat(String.valueOf(noneColor) + none, bars - progressBars);
        } catch (final RuntimeException exception) {
            return "";
        }
    }

    public String group(final Player player) {
        return getGroup(player, plugin.get(player.getUniqueId().toString()).level());
    }

    public String group_to(final Player player) {
        return getGroup(player, plugin.get(player.getUniqueId().toString()).level() + 1L);
    }

    private String getGroup(final Player player, final Long level) {
        final String group = plugin.systemManager.getGroup(player, plugin.config.get, "groups.list", false);
        if (group != null) {
            if (plugin.config.get.contains("groups.list." + group + ".list." + level)) {
                return plugin.config.get.getString("groups.list." + group + ".list." + level);
            } else {
                return plugin.config.get.getString("groups.list." + group + ".none");
            }
        }
        return plugin.config.get.getString("groups.none");
    }

    public String prefix(final Player player) {
        String prefix_name = "%levels_prefix%";
        final String group = plugin.systemManager.getGroup(player, plugin.config.get, "placeholders.prefix", false);
        if (group != null) {
            final PlayerConnect playerConnect = plugin.get(player.getUniqueId().toString());
            if (plugin.config.get.contains("placeholders.prefix." + group + ".list." + playerConnect.level())) {
                prefix_name = ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(player, plugin.config.get.getString("placeholders.prefix." + group + ".list." + playerConnect.level())));
            } else {
                prefix_name = ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(player, plugin.config.get.getString("placeholders.prefix." + group + ".none")));
            }
        }
        return prefix_name;
    }

    public String getTopValue(final String type, final int number, final boolean key, final boolean reverse) {
        final List<String> map;
        if (key) {
            map = new ArrayList<String>(getTopMap(type, reverse).keySet());
            if (map.size() > number) {
                return plugin.get(map.get(number)).name();
            } else {
                return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("pvptop." + type + ".name"));
            }
        }
        map = new ArrayList<String>(getTopMap(type, reverse).values());
        if (map.size() > number) {
            return String.valueOf(map.get(number));
        } else {
            return ChatColor.translateAlternateColorCodes('&', plugin.config.get.getString("pvptop." + type + ".value"));
        }
    }

    public LinkedHashMap getTopMap(final String type, final boolean reverse) {
        final Map<String, Long> unsorted = new HashMap<>();
        for (final String uuid : plugin.list()) {
            if (type.equalsIgnoreCase("xp") && !plugin.config.get.getStringList("pvptop.xp.excluded").contains(uuid)) { unsorted.put(uuid, plugin.get(uuid).xp()); }
            if (type.equalsIgnoreCase("level") && !plugin.config.get.getStringList("pvptop.level.excluded").contains(uuid)) { unsorted.put(uuid, plugin.get(uuid).level()); }
            if (type.equalsIgnoreCase("xprequired")) { unsorted.put(uuid, plugin.statsManager.xp_required(uuid, false)); }
        }
        final LinkedHashMap<String, Long> sorted = new LinkedHashMap<>();
        if (reverse) {
            unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        } else {
            unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder())).forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        }
        return sorted;
    }

    private ChatColor getChatColor(final String colorCode){
        switch (colorCode){
            case "&0" : return ChatColor.BLACK;
            case "&1" : return ChatColor.DARK_BLUE;
            case "&2" : return ChatColor.DARK_GREEN;
            case "&3" : return ChatColor.DARK_AQUA;
            case "&4" : return ChatColor.DARK_RED;
            case "&5" : return ChatColor.DARK_PURPLE;
            case "&6" : return ChatColor.GOLD;
            case "&7" : return ChatColor.GRAY;
            case "&8" : return ChatColor.DARK_GRAY;
            case "&9" : return ChatColor.BLUE;
            case "&a" : return ChatColor.GREEN;
            case "&b" : return ChatColor.AQUA;
            case "&c" : return ChatColor.RED;
            case "&d" : return ChatColor.LIGHT_PURPLE;
            case "&e" : return ChatColor.YELLOW;
            case "&k" : return ChatColor.MAGIC;
            case "&l" : return ChatColor.BOLD;
            case "&m" : return ChatColor.STRIKETHROUGH;
            case "&n" : return ChatColor.UNDERLINE;
            case "&o" : return ChatColor.ITALIC;
            case "&r" : return ChatColor.RESET;
            default: return ChatColor.WHITE;
        }
    }
}
