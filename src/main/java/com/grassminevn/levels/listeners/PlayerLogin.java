package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLogin implements Listener {
    private final Levels plugin;

    public PlayerLogin(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLogin(final PlayerLoginEvent e) {
        final String uuid = e.getPlayer().getUniqueId().toString();
        final String name = e.getPlayer().getName();
        plugin.database.insert(uuid, name);
        if (!plugin.list().contains(uuid)) {
            plugin.load(uuid);
        }
        final PlayerConnect playerConnect = plugin.get(uuid);
        if (!playerConnect.name().equalsIgnoreCase(name)) {
            playerConnect.name(name);
        }
    }
}