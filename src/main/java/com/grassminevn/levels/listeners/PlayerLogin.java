package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
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
        plugin.database.insert(e.getPlayer().getUniqueId());
    }
}