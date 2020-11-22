package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerLogin implements Listener {
    private final Levels plugin;

    public PlayerLogin(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(final AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        plugin.database.insert(e.getUniqueId());
        plugin.getPlayerConnect(e.getUniqueId());
    }
}