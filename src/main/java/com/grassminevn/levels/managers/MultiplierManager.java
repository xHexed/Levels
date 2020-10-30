package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class MultiplierManager extends Manager {
    public MultiplierManager(final Levels plugin) {
        super(plugin);
    }

    public void addMultiplier(final UUID uuid, final long time) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            final PlayerConnect playerConnect = plugin.getPlayerConnect(uuid);
            for (final String message : plugin.language.get.getStringList("multiplier.lost")) {
                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', message.replace("{player}", player.getName()).replace("{multiplier}", String.valueOf(playerConnect.getMultiplier()))));
            }
            playerConnect.getMultiplierInfo().stop();
        }, time * 20);
    }
}
