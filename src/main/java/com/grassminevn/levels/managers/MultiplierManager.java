package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MultiplierManager extends Manager {
    public MultiplierManager(final Levels plugin) {
        super(plugin);
    }

    public void addMultiplier(final UUID uuid, final long time) {
        plugin.asyncExecutorManager.schedule(() -> {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            final PlayerConnect playerConnect = plugin.getPlayerConnect(uuid);
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (final String message : plugin.language.get.getStringList("multiplier.lost")) {
                    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', message.replace("{player}", player.getName()).replace("{multiplier}", String.valueOf(playerConnect.getMultiplier()))));
                }
            });
            playerConnect.getMultiplierInfo().stop();
        }, time * 20, TimeUnit.SECONDS);
    }
}
