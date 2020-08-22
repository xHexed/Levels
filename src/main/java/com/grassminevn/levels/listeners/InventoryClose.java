package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClose implements Listener {
    private final Levels plugin;

    public InventoryClose(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(final InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof GUI) {
            final Player player = (Player) e.getPlayer();
            player.updateInventory();
            Levels.call.getServer().getScheduler().runTaskLater(plugin, player::updateInventory, 3L);
        }
    }
}
