package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final Levels plugin;

    public PlayerQuit(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent e) {
        plugin.asyncExecutorManager.execute(() -> {
            final PlayerConnect playerConnect = plugin.getPlayerConnect(e.getPlayer().getUniqueId());
            playerConnect.setTime();
            playerConnect.save();
        });
    }
}