package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoin implements Listener {

    private final Levels plugin;

    public PlayerJoin(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerConnect playerConnect = plugin.getPlayerConnect(uuid);
        if (playerConnect.getMultiplier() != 0) {
            for (final String message : plugin.language.get.getStringList("multiplier.join")) {
                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", player.getName())));
            }
            plugin.multipliers.add(player);
        }
    }
}
