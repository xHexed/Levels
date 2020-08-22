package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    private final Levels plugin;

    public PlayerQuit(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final String uuid = player.getUniqueId().toString();
        if (plugin.list().contains(uuid)) {
            if (plugin.config.get.getBoolean("unload-players.quit")) {
                plugin.unload(uuid);
            } else {
                plugin.get(uuid).save();
            }
        }
        plugin.guiPageID.remove(uuid);
        plugin.guiPageSort.remove(uuid);
        plugin.guiPageSortReverse.remove(uuid);
        plugin.lastDamagers.remove(uuid);
    }
}